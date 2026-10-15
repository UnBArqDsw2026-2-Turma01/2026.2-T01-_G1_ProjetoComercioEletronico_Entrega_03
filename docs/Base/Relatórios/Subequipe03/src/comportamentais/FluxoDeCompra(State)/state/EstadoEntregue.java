package state;

import context.Pedido;
import context.StatusPedido;

public class EstadoEntregue implements EstadoPedido {

    @Override
    public void confirmarPagamento(Pedido pedido) {
        throw new IllegalStateException(
            "Pagamento já confirmado — pedido já foi entregue."
        );
    }

    @Override
    public void cancelar(Pedido pedido) {
        // Único cancelamento válido a partir daqui é o que vem do
        // fluxo de Devolução e Estorno (ver Diagrama de Atividades),
        // não um cancelamento espontâneo do cliente.
        pedido.mudarEstado(new EstadoCancelado());
    }

    @Override
    public void avancar(Pedido pedido) {
        throw new IllegalStateException(
            "Pedido entregue é o fim do ciclo normal de vida — nada para avançar."
        );
    }

    @Override
    public StatusPedido getStatus() {
        return StatusPedido.ENTREGUE;
    }
}