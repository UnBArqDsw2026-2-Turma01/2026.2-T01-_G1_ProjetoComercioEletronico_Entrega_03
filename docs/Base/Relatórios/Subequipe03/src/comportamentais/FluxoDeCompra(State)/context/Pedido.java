package context;

import state.EstadoAguardandoPagamento;
import state.EstadoPedido;

/**
 * Context — mantém uma referência para o EstadoPedido atual
 * e delega a ele toda decisão sobre transições. O Pedido em si
 * não sabe quais transições são válidas; quem sabe é cada
 * ConcreteState.
 */
public class Pedido {

    private final String id;
    private EstadoPedido estado;

    public Pedido(String id) {
        this.id = id;
        // Todo pedido nasce aguardando pagamento.
        this.estado = new EstadoAguardandoPagamento();
    }

    public void confirmarPagamento() {
        estado.confirmarPagamento(this);
    }

    public void cancelar() {
        estado.cancelar(this);
    }

    public void avancar() {
        estado.avancar(this);
    }

    /**
     * Só os ConcreteState devem chamar este método — é assim
     * que a transição efetivamente acontece.
     */
    public void mudarEstado(EstadoPedido novoEstado) {
        System.out.println(
            "[Pedido " + id + "] " + estado.getStatus()
            + " -> " + novoEstado.getStatus()
        );
        this.estado = novoEstado;
    }

    public StatusPedido getStatus() {
        return estado.getStatus();
    }

    public String getId() {
        return id;
    }
}