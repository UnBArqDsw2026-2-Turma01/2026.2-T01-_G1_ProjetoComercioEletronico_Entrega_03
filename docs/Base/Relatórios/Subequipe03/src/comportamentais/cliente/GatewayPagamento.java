package cliente;

import modelo.Pagamento;
import modelo.StatusPagamento;

/**
 * Papel GoF: Client.
 * Simula o gateway que processa a transacao e informa o resultado ao pagamento.
 * Ele so muda o status: nao sabe quem sera avisado nem o que cada um fara.
 */
public class GatewayPagamento {

    public void informarResultado(Pagamento pagamento, StatusPagamento novo) {
        System.out.println("\nGateway: pagamento " + pagamento.getId() + " mudou para " + novo.getDescricao());
        pagamento.setStatus(novo);
    }
}
