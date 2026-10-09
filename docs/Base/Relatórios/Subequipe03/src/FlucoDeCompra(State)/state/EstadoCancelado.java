package state;

import context.Pedido;
import context.StatusPedido;

/**
 * Estado final: todas as operações são inválidas a partir daqui.
 */
public class EstadoCancelado implements EstadoPedido {

    @Override
    public void confirmarPagamento(Pedido pedido) {
        throw new IllegalStateException("Pedido cancelado — operação inválida.");
    }

    @Override
    public void cancelar(Pedido pedido) {
        throw new IllegalStateException("Pedido já está cancelado.");
    }

    @Override
    public void avancar(Pedido pedido) {
        throw new IllegalStateException("Pedido cancelado — operação inválida.");
    }

    @Override
    public StatusPedido getStatus() {
        return StatusPedido.CANCELADO;
    }
}