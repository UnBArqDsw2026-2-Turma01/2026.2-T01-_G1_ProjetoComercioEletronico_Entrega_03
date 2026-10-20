package produto;

import dominio.Dinheiro;

/**
 * Produto abstrato A da familia: quem conversa com o meio de pagamento e devolve
 * o desfecho da tentativa.
 *
 * Rastreabilidade: a tarefa "Autorizar pagamento" do BPMN 1 (Entrega 1) e o
 * estado Autorizando da Maquina de Estados do Pedido (Entrega 2).
 *
 * Autor: Pedro Luciano de Azevedo (contratos).
 * Implementado por: uma classe concreta em cada familia de familias/.
 */
public interface Autorizador {

    /** Tenta autorizar o valor do pedido junto ao meio de pagamento. */
    Autorizacao autorizar(String pedido, Dinheiro valor);

    /**
     * Prazo que o comprador tem para concluir o pagamento antes de o pedido
     * expirar. Corresponde ao evento de borda de temporizador do BPMN 1 e ao
     * after(tempo limite) da Maquina de Estados do Pedido.
     */
    int prazoLimiteEmMinutos();

    /**
     * true quando o desfecho nao chega na hora e o pedido fica aguardando
     * confirmacao externa (caso do boleto).
     */
    boolean ehAssincrono();
}
