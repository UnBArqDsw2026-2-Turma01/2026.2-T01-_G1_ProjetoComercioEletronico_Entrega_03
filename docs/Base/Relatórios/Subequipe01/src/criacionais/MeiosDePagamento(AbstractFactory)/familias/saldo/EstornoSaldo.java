package familias.saldo;

import dominio.Dinheiro;
import produto.Autorizacao;
import produto.PoliticaEstorno;

/**
 * Produto concreto C da familia Saldo em conta: credito imediato de volta no
 * mesmo saldo.
 *
 * E o fecho do Diagrama de Atividades da reclamacao (Entrega 2): a mediacao
 * decide pelo reembolso, a reclamacao encerra como REEMBOLSADA, e o dinheiro
 * volta. La, o reembolso passa por "Estornar pagamento via operadora", por
 * IPagamento, e foi modelado em fork com a notificacao por ser uma chamada
 * externa que leva tempo. No saldo em conta nao existe operadora — o estorno e
 * um lancamento interno, e o dinheiro volta para o mesmo saldo de onde saiu, na
 * hora. O diagrama assumiu que todo estorno sai pela operadora; esta familia
 * mostra o caso em que essa suposicao nao vale.
 *
 * Rastreabilidade: processo 3 do Rich Picture (reclamacao com resposta ou
 * reembolso); transicao "Em disputa -> Reembolsado / estornar pagamento" da
 * Maquina de Estados do Pedido (Entrega 2).
 *
 * Autor: Guilherme Costa Zanella (familia Saldo em conta).
 */
public final class EstornoSaldo implements PoliticaEstorno {

    /** Lancamento interno: nao ha compensacao nem operadora a esperar. INFERIDO. */
    private static final int PRAZO_DE_DEVOLUCAO_EM_DIAS = 0;

    private final AutorizadorSaldo autorizador;

    public EstornoSaldo(AutorizadorSaldo autorizador) {
        this.autorizador = autorizador;
    }

    @Override
    public boolean podeEstornar(Autorizacao autorizacao) {
        // So ha o que devolver se o debito original aconteceu.
        return autorizacao.aprovada();
    }

    @Override
    public String estornar(String pedido, Dinheiro valor) {
        autorizador.creditar(valor);
        return "Estorno de " + valor + " do pedido " + pedido
                + " creditado de volta no saldo em conta; saldo atual " + autorizador.saldo();
    }

    @Override
    public int prazoDeDevolucaoEmDias() {
        return PRAZO_DE_DEVOLUCAO_EM_DIAS;
    }
}
