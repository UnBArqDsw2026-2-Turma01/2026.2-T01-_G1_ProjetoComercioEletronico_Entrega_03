package familias.cartao;

import dominio.Dinheiro;
import produto.Autorizacao;
import produto.PoliticaEstorno;

/**
 * Produto concreto C da familia Cartao de credito: o dinheiro volta pela fatura.
 *
 * E o produto que mostra por que Abstract Factory e nao Factory Method: o
 * caminho de volta depende do caminho de ida. No Pix a devolucao cai na conta de
 * origem quase na hora; no cartao ela passa pela operadora e aparece na proxima
 * fatura, o que muda o prazo que o atendimento informa ao comprador.
 *
 * Rastreabilidade: acao "Estornar pagamento via operadora" do Diagrama de
 * Atividades (Entrega 2), que sai pela interface IPagamento do Diagrama de
 * Componentes, e a transicao "Em disputa -> Reembolsado / estornar pagamento"
 * da Maquina de Estados do Pedido.
 *
 * Autor: Patrick Anderson Carvalho dos Santos (familia Cartao de credito).
 */
public final class EstornoCartao implements PoliticaEstorno {

    /**
     * Prazo de demonstracao: o ciclo de fatura nao foi observado na Entrega 1.
     * O numero existe para contrastar com a devolucao imediata do Pix.
     */
    private static final int PRAZO_DE_DEMONSTRACAO_EM_DIAS = 30;

    @Override
    public boolean podeEstornar(Autorizacao autorizacao) {
        // So ha o que estornar se a operadora chegou a aprovar a cobranca.
        // Pedido recusado ou expirado nunca teve valor capturado.
        return autorizacao.aprovada();
    }

    @Override
    public String estornar(String pedido, Dinheiro valor) {
        return "Estorno de " + valor + " do pedido " + pedido
                + " solicitado a operadora; credito aparece na proxima fatura";
    }

    @Override
    public int prazoDeDevolucaoEmDias() {
        return PRAZO_DE_DEMONSTRACAO_EM_DIAS;
    }
}
