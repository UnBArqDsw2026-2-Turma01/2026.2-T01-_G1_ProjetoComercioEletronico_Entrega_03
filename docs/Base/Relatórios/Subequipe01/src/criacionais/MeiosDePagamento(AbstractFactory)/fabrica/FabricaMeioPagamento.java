package fabrica;

import dominio.MeioPagamento;
import produto.Autorizador;
import produto.Comprovante;
import produto.PoliticaEstorno;

/**
 * Fabrica abstrata (AbstractFactory, GAMMA et al., 1995).
 *
 * Cada meio de pagamento e uma familia coerente de tres produtos: quem autoriza,
 * o que o comprador recebe e como o dinheiro volta. O cliente escolhe a familia
 * uma unica vez, no inicio do checkout, e dali em diante nunca mais decide qual
 * autorizador, comprovante ou politica de estorno usar — e e isso que impede a
 * combinacao invalida de produtos de familias diferentes.
 *
 * Autor: Pedro Luciano de Azevedo (contratos).
 * Implementado por: uma fabrica concreta por familia, em familias/.
 */
public interface FabricaMeioPagamento {

    /** Qual meio esta familia atende. */
    MeioPagamento meio();

    Autorizador criarAutorizador();

    Comprovante criarComprovante();

    PoliticaEstorno criarPoliticaEstorno();
}
