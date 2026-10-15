package observadores;

import modelo.Pagamento;

/**
 * Papel GoF: ConcreteObserver.
 * Reage a qualquer mudanca de status, avisando o comprador.
 */
public class NotificacaoComprador implements ObservadorDePagamento {

    @Override
    public void atualizar(Pagamento pagamento) {
        System.out.println("  [Comprador] Pagamento " + pagamento.getId()
                + " agora esta: " + pagamento.getStatus().getDescricao());
    }
}
