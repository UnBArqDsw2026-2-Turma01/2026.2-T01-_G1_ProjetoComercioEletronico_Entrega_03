package familias.pix;

import dominio.Dinheiro;
import produto.Autorizacao;
import produto.PoliticaEstorno;

/**
 * Produto concreto C da familia Pix: devolucao para a conta de origem.
 *
 * O dinheiro volta pelo mesmo caminho por onde veio, e por isso a politica
 * precisa ser criada junto com o AutorizadorPix. Diferente do boleto, que nao
 * tem caminho de volta automatico, a devolucao do Pix e rapida.
 *
 * Rastreabilidade: estorno via Instituicao Financeira no fluxo de devolucao do
 * Rich Picture (Entrega 1); acao "Estornar pagamento via operadora", que sai por
 * IPagamento, no Diagrama de Atividades e no Diagrama de Componentes; transicao
 * "Em disputa -> Reembolsado / estornar pagamento" da Maquina de Estados do
 * Pedido (Entrega 2).
 *
 * Autor: Maria Clara Sena de Lima (familia Pix).
 */
public final class EstornoPix implements PoliticaEstorno {

    /**
     * Parametro de demonstracao: o prazo de devolucao nao foi observado. Zero
     * representa a devolucao rapida que distingue o Pix do boleto.
     */
    private static final int PRAZO_DE_DEMONSTRACAO_EM_DIAS = 0;

    @Override
    public boolean podeEstornar(Autorizacao autorizacao) {
        // So existe o que devolver se o Pix original foi confirmado.
        return autorizacao.aprovada();
    }

    @Override
    public String estornar(String pedido, Dinheiro valor) {
        return "Devolucao Pix de " + valor + " do pedido " + pedido
                + " enviada para a conta de origem";
    }

    @Override
    public int prazoDeDevolucaoEmDias() {
        return PRAZO_DE_DEMONSTRACAO_EM_DIAS;
    }
}
