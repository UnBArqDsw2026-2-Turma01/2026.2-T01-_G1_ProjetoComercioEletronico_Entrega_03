package familias.boleto;

import dominio.Dinheiro;
import produto.Autorizacao;
import produto.PoliticaEstorno;

/**
 * Produto concreto C da familia Boleto: estorno por deposito ou transferencia bancaria.
 *
 * Esta classe destaca a assimetria fundamental que justifica a adocao do Abstract Factory:
 * nao existe tecnicamente a operacao de "estornar um boleto" pela propria instituicao
 * emissora pelo mesmo canal de ida (como ocorre no cartao ou pix). O dinheiro recebido
 * apos compensacao precisa ser devolvido por meio de deposito em conta bancaria ou TED/DOC,
 * solicitando os dados do comprador.
 *
 * Rastreabilidade:
 * - Diagrama de Atividades (fluxo de reclamacao / reembolso que encerra em "Estornar pagamento via operadora");
 * - Maquina de Estados do Pedido: transicao "Em disputa -> Reembolsado / estornar pagamento".
 *
 * Autor: Guilherme Davila Rodrigues Carneiro Sampaio (familia Boleto).
 */
public final class EstornoBoleto implements PoliticaEstorno {

    /**
     * Prazo de demonstracao para liquidacao de estorno bancario manual / deposito.
     * Inferido: nao foi observado no Recorte B, pois demandaria fluxo real de estorno.
     */
    private static final int PRAZO_DE_DEMONSTRACAO_EM_DIAS = 3;

    @Override
    public boolean podeEstornar(Autorizacao autorizacao) {
        // So ha o que estornar se o boleto tiver sido aprovado/compensado previamente.
        return autorizacao.aprovada();
    }

    @Override
    public String estornar(String pedido, Dinheiro valor) {
        return "Devolucao de " + valor + " do pedido " + pedido
                + " solicitada via deposito em conta corrente indicada pelo comprador";
    }

    @Override
    public int prazoDeDevolucaoEmDias() {
        return PRAZO_DE_DEMONSTRACAO_EM_DIAS;
    }
}
