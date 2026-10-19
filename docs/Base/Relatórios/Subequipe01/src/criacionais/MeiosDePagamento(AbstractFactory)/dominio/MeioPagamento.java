package dominio;

/**
 * Meios de pagamento registrados como enumeracao no Diagrama de Classes da
 * Entrega 2 (enum MeioPagamento). Cada constante corresponde a uma familia de
 * produtos do Abstract Factory.
 *
 * Autor: Pedro Luciano de Azevedo (dominio compartilhado e contratos).
 */
public enum MeioPagamento {

    CARTAO("Cartao de credito"),
    PIX("Pix"),
    BOLETO("Boleto bancario"),
    SALDO_EM_CONTA("Saldo em conta");

    private final String rotulo;

    MeioPagamento(String rotulo) {
        this.rotulo = rotulo;
    }

    public String rotulo() {
        return rotulo;
    }
}
