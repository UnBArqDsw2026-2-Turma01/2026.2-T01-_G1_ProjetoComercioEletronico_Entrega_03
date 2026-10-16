package observadores;

import modelo.Pagamento;
import modelo.StatusPagamento;

/**
 * Papel GoF: ConcreteObserver.
 * So reage quando o pagamento e APROVADO: emite a nota fiscal no valor pago.
 */
public class EmissaoNotaFiscal implements ObservadorDePagamento {

    @Override
    public void atualizar(Pagamento pagamento) {
        if (pagamento.getStatus() == StatusPagamento.APROVADO) {
            System.out.printf("  [Fiscal] Nota fiscal emitida para o pagamento %s no valor de R$ %.2f.%n",
                    pagamento.getId(), pagamento.getValor());
        }
    }
}
