package state;

import context.Pedido;
import context.StatusPedido;

/**
 * State — padroniza as operações que todo estado do Pedido
 * deve saber responder (confirmarPagamento, cancelar, avancar),
 * além de expor qual StatusPedido ele representa.
 */
public interface EstadoPedido {

    void confirmarPagamento(Pedido pedido);

    void cancelar(Pedido pedido);

    void avancar(Pedido pedido);

    StatusPedido getStatus();
}