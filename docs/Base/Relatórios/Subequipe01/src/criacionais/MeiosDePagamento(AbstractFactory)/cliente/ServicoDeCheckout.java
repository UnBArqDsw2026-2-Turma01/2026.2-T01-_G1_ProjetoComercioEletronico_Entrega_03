package cliente;

import dominio.Dinheiro;
import dominio.MeioPagamento;
import fabrica.FabricaMeioPagamento;
import produto.Autorizacao;
import produto.Autorizador;
import produto.Comprovante;
import produto.PoliticaEstorno;

/**
 * Cliente (Client, GAMMA et al., 1995) do Abstract Factory.
 *
 * Conduz o checkout de ponta a ponta — autorizar, emitir comprovante e, se a
 * reclamacao terminar em reembolso, estornar — sem nunca escrever o nome de uma
 * classe concreta. Tudo o que ele conhece sao as quatro interfaces do contrato:
 * FabricaMeioPagamento, Autorizador, Comprovante e PoliticaEstorno.
 *
 * O ponto do padrao esta no construtor: os tres produtos sao criados de uma vez
 * so, a partir da *mesma* fabrica, e guardados. Depois disso nao ha como obter
 * um comprovante de uma familia e um estorno de outra, porque nao existe no
 * codigo nenhum caminho que crie um produto isolado. A combinacao invalida nao
 * e proibida por validacao: ela e inalcancavel por construcao.
 *
 * Rastreabilidade: o Servico de Checkout e Pedidos do Diagrama de Componentes
 * (Entrega 2) e o componente que requer IPagamento; a sequencia autorizar ->
 * registrar -> notificar vem do BPMN 1 (Entrega 1) e dos estados Autorizando e
 * Pago da Maquina de Estados do Pedido.
 *
 * Autor: Patrick Anderson Carvalho dos Santos (cliente do criacional).
 */
public final class ServicoDeCheckout {

    private final MeioPagamento meio;
    private final Autorizador autorizador;
    private final Comprovante comprovante;
    private final PoliticaEstorno politicaEstorno;

    /**
     * Recebe a familia escolhida. A selecao da fabrica a partir do
     * MeioPagamento acontece fora daqui, em CatalogoDeFabricas: a interface da
     * fabrica nao deve saber escolher entre as suas implementacoes.
     */
    public ServicoDeCheckout(FabricaMeioPagamento fabrica) {
        if (fabrica == null) {
            throw new IllegalArgumentException("O checkout precisa de uma familia de pagamento");
        }
        this.meio = fabrica.meio();
        this.autorizador = fabrica.criarAutorizador();
        this.comprovante = fabrica.criarComprovante();
        this.politicaEstorno = fabrica.criarPoliticaEstorno();
    }

    public MeioPagamento meio() {
        return meio;
    }

    /** Estado Autorizando da Maquina de Estados do Pedido. */
    public Autorizacao pagar(String pedido, Dinheiro valor) {
        return autorizador.autorizar(pedido, valor);
    }

    /** O que o comprador recebe depois de pagar. */
    public String comprovanteDe(String pedido, Dinheiro valor, Autorizacao autorizacao) {
        return comprovante.gerar(pedido, valor, autorizacao);
    }

    /** Transicao "Em disputa -> Reembolsado" da Maquina de Estados do Pedido. */
    public String reembolsar(String pedido, Dinheiro valor, Autorizacao autorizacao) {
        if (!politicaEstorno.podeEstornar(autorizacao)) {
            return "Nada a estornar no pedido " + pedido
                    + ": a cobranca nao chegou a ser capturada (" + autorizacao.status() + ")";
        }
        return politicaEstorno.estornar(pedido, valor)
                + " (prazo de " + politicaEstorno.prazoDeDevolucaoEmDias() + " dia(s))";
    }

    /** Resumo da familia montada, para a demonstracao. */
    public String resumoDaFamilia() {
        return meio.rotulo()
                + " | comprovante em " + comprovante.formato()
                + " | janela de " + autorizador.prazoLimiteEmMinutos() + " min"
                + " | " + (autorizador.ehAssincrono() ? "compensacao assincrona" : "resposta imediata")
                + " | devolucao em " + politicaEstorno.prazoDeDevolucaoEmDias() + " dia(s)";
    }
}
