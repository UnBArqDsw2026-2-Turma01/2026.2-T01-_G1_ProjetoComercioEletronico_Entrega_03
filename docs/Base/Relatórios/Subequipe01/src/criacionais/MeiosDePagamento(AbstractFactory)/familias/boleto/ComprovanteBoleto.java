package familias.boleto;

import dominio.Dinheiro;
import produto.Autorizacao;
import produto.Comprovante;

/**
 * Produto concreto B da familia Boleto: comprovante com linha digitavel e codigo de barras.
 *
 * O comprovante de boleto nao e um recibo de pagamento instantaneo nem possui QR Code
 * Pix ou parcelamento no cartao. Ele contem a linha digitavel necessaria para que o
 * comprador pague atraves do seu banco ou correspondente bancario.
 *
 * Rastreabilidade e Coerencia:
 * Cria um comprovante compativel exclusivamente com uma emissao de boleto bancario.
 * Associar um comprovante de boleto a uma cobranca de cartao ou debito em conta seria
 * uma combinacao invalida de produtos, que o Abstract Factory impede estruturalmente.
 *
 * O formato "linha digitavel" e o acordado no contrato Comprovante (ata de 18/10/2026).
 *
 * Autor: Guilherme Davila Rodrigues Carneiro Sampaio (familia Boleto).
 */
public final class ComprovanteBoleto implements Comprovante {

    @Override
    public String gerar(String pedido, Dinheiro valor, Autorizacao autorizacao) {
        if (!autorizacao.aprovada()) {
            return "Boleto do pedido " + pedido + " nao pode ser emitido: " + autorizacao.detalhe();
        }
        // Gera linha digitavel simulada para demonstracao baseada no identificador e valor
        String codigoBanco = "341"; // Itau / Banco liquidante simulado
        String linhaDigitavel = String.format("%s91.00000 00000.000000 00000.000000 8 %010d",
                codigoBanco, valor.centavos());

        return "Boleto Bancario\n"
                + "  Pedido ........: " + pedido + "\n"
                + "  Valor .........: " + valor + "\n"
                + "  Transacao .....: " + autorizacao.identificador() + "\n"
                + "  Linha digitavel: " + linhaDigitavel + "\n"
                + "  Situacao ......: emitido, aguardando compensacao";
    }

    @Override
    public String formato() {
        return "linha digitavel";
    }
}
