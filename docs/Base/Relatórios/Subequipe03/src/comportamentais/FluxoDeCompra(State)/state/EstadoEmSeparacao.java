package state;

import context.Pedido;
import context.StatusPedido;

public class EstadoEmSeparacao implements EstadoPedido {

    @Override
    public void confirmarPagamento(Pedido pedido) {
        throw new IllegalStateException(
            "Pagamento já confirmado — pedido já está em separação."
        );
    }

    @Override
    public void cancelar(Pedido pedido) {
        pedido.mudarEstado(new EstadoCancelado());
    }

    @Override
    public void avancar(Pedido pedido) {
        pedido.mudarEstado(new EstadoEnviado());
    }

    @Override
    public StatusPedido getStatus() {
        return StatusPedido.EM_SEPARACAO;
    }
}