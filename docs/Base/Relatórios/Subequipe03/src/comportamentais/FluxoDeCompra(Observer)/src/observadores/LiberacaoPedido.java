package observadores;

import modelo.Pagamento;
import modelo.StatusPagamento;

/**
 * Papel GoF: ConcreteObserver.
 * So reage quando o pagamento e APROVADO: libera o pedido para o vendedor preparar.
 */
public class LiberacaoPedido implements ObservadorDePagamento {

    @Override
    public void atualizar(Pagamento pagamento) {
        if (pagamento.getStatus() == StatusPagamento.APROVADO) {
            System.out.println("  [Pedido] Pedido do pagamento " + pagamento.getId()
                    + " liberado para o vendedor preparar.");
        }
    }
}
