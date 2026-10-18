package familias.saldo;

import dominio.Dinheiro;
import produto.Autorizacao;
import produto.Comprovante;

/**
 * Produto concreto B da familia Saldo em conta: recibo de debito em conta.
 *
 * Alem de citar o identificador gerado pelo AutorizadorSaldo, informa o saldo
 * que restou — informacao que so existe porque o comprovante le o mesmo
 * autorizador que acabou de debitar. Um ComprovanteSaldo ligado a outro
 * autorizador mostraria um saldo que nao foi debitado; e essa combinacao que a
 * FabricaSaldo impede ao criar os dois juntos.
 *
 * O conteudo e simulado: a tela de pagamento nao foi observada na Entrega 1.
 *
 * Autor: Guilherme Costa Zanella (familia Saldo em conta).
 */
public final class ComprovanteSaldo implements Comprovante {

    private final AutorizadorSaldo autorizador;

    public ComprovanteSaldo(AutorizadorSaldo autorizador) {
        this.autorizador = autorizador;
    }

    @Override
    public String gerar(String pedido, Dinheiro valor, Autorizacao autorizacao) {
        if (!autorizacao.aprovada()) {
            return "Pagamento com saldo do pedido " + pedido + " nao concluido: " + autorizacao.detalhe()
                    + ". Nenhum valor foi debitado.";
        }
        return "Recibo de debito em conta\n"
                + "  Pedido ........: " + pedido + "\n"
                + "  Valor .........: " + valor + "\n"
                + "  Transacao .....: " + autorizacao.identificador() + "\n"
                + "  Saldo restante : " + autorizador.saldo() + "\n"
                + "  Situacao ......: pago na hora, sem sair da plataforma";
    }

    @Override
    public String formato() {
        return "recibo de debito em conta";
    }
}
