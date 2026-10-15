package observadores;

import modelo.Pagamento;
import modelo.StatusPagamento;

/**
 * Papel GoF: ConcreteObserver.
 * So reage quando o pagamento e RECUSADO ou ESTORNADO: devolve o item reservado ao estoque.
 */
public class LiberacaoEstoque implements ObservadorDePagamento {

    @Override
    public void atualizar(Pagamento pagamento) {
        StatusPagamento status = pagamento.getStatus();
        if (status == StatusPagamento.RECUSADO || status == StatusPagamento.ESTORNADO) {
            System.out.println("  [Estoque] Item do pagamento " + pagamento.getId()
                    + " devolvido ao estoque.");
        }
    }
}
