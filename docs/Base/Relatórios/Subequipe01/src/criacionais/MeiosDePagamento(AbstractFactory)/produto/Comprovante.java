package produto;

import dominio.Dinheiro;

/**
 * Produto abstrato B da familia: o que o comprador recebe depois de pagar.
 *
 * O formato muda com o meio — QR Code no Pix, linha digitavel no boleto, recibo
 * de parcelamento no cartao —, e e por isso que comprovante e autorizador
 * precisam vir da mesma familia: um comprovante de boleto para uma autorizacao
 * de cartao seria uma combinacao invalida.
 *
 * Autor: Pedro Luciano de Azevedo (contratos).
 * Implementado por: uma classe concreta em cada familia de familias/.
 */
public interface Comprovante {

    /** Texto do comprovante entregue ao comprador. */
    String gerar(String pedido, Dinheiro valor, Autorizacao autorizacao);

    /** Formato do comprovante, para exibicao: "QR Code", "linha digitavel"... */
    String formato();
}
