package produto;

/**
 * Resultado de uma tentativa de autorizacao.
 *
 * Os tres desfechos vem da Maquina de Estados do Pedido (Entrega 2): APROVADO
 * leva a Pago, RECUSADO leva ao estado Recusado (evento de borda de erro do
 * BPMN 1) e EXPIRADO vem do evento de borda de temporizador do mesmo modelo.
 *
 * Autor: Pedro Luciano de Azevedo (dominio compartilhado e contratos).
 */
public final class Autorizacao {

    public enum Status { APROVADO, RECUSADO, EXPIRADO }

    private final Status status;
    private final String identificador;
    private final String detalhe;

    private Autorizacao(Status status, String identificador, String detalhe) {
        this.status = status;
        this.identificador = identificador;
        this.detalhe = detalhe;
    }

    public static Autorizacao aprovada(String identificador, String detalhe) {
        return new Autorizacao(Status.APROVADO, identificador, detalhe);
    }

    public static Autorizacao recusada(String detalhe) {
        return new Autorizacao(Status.RECUSADO, "-", detalhe);
    }

    public static Autorizacao expirada(String detalhe) {
        return new Autorizacao(Status.EXPIRADO, "-", detalhe);
    }

    public Status status() {
        return status;
    }

    public boolean aprovada() {
        return status == Status.APROVADO;
    }

    public String identificador() {
        return identificador;
    }

    public String detalhe() {
        return detalhe;
    }

    @Override
    public String toString() {
        return status + " (" + identificador + ") - " + detalhe;
    }
}
