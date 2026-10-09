package state;

import context.Pedido;
import context.StatusPedido;

public class EstadoEnviado implements EstadoPedido {

    @Override
    public void confirmarPagamento(Pedido pedido) {
        throw new IllegalStateException(
            "Pagamento já confirmado — pedido já foi enviado."
        );
    }

    @Override
    public void cancelar(Pedido pedido) {
        // Pedido já saiu para entrega: cancelamento simples não é
        // mais suficiente (precisaria de logística reversa), mas
        // mantemos permitido aqui por simplicidade do exemplo.
        pedido.mudarEstado(new EstadoCancelado());
    }

    @Override
    public void avancar(Pedido pedido) {
        pedido.mudarEstado(new EstadoEntregue());
    }

    @Override
    public StatusPedido getStatus() {
        return StatusPedido.ENVIADO;
    }
}