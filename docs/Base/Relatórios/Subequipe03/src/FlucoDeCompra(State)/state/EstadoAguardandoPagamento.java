package state;

import context.Pedido;
import context.StatusPedido;

public class EstadoAguardandoPagamento implements EstadoPedido {

    @Override
    public void confirmarPagamento(Pedido pedido) {
        pedido.mudarEstado(new EstadoPago());
    }

    @Override
    public void cancelar(Pedido pedido) {
        pedido.mudarEstado(new EstadoCancelado());
    }

    @Override
    public void avancar(Pedido pedido) {
        throw new IllegalStateException(
            "Não é possível avançar um pedido que ainda aguarda pagamento."
        );
    }

    @Override
    public StatusPedido getStatus() {
        return StatusPedido.AGUARDANDO_PAGAMENTO;
    }
}