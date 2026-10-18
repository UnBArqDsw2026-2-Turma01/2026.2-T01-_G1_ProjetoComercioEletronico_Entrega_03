package familias.cartao;

import java.util.List;

import dominio.Dinheiro;
import produto.Autorizacao;
import produto.Comprovante;

/**
 * Produto concreto B da familia Cartao de credito: recibo com o parcelamento.
 *
 * E o comprovante que distingue o cartao dos demais meios. O Diagrama de
 * Classes da Entrega 2 registra parcelamento como atributo da oferta, e o
 * Recorte A da Entrega 1 observou preco e parcelamento juntos na ficha do
 * produto e no cartao da listagem.
 *
 * O rateio usa Dinheiro.parcelar(n), do dominio do Pedro, que distribui o resto
 * da divisao inteira uma unidade por vez — nenhum centavo se perde, e a soma das
 * parcelas e sempre igual ao total.
 *
 * Autor: Patrick Anderson Carvalho dos Santos (familia Cartao de credito).
 */
public final class ComprovanteCartao implements Comprovante {

    private final int parcelas;

    public ComprovanteCartao() {
        this(1);
    }

    public ComprovanteCartao(int parcelas) {
        if (parcelas <= 0) {
            throw new IllegalArgumentException("Numero de parcelas deve ser maior que zero");
        }
        this.parcelas = parcelas;
    }

    @Override
    public String gerar(String pedido, Dinheiro valor, Autorizacao autorizacao) {
        if (!autorizacao.aprovada()) {
            return "Pagamento com cartao do pedido " + pedido + " nao concluido: "
                    + autorizacao.detalhe() + ". Nenhum recibo foi emitido.";
        }

        List<Dinheiro> rateio = valor.parcelar(parcelas);
        StringBuilder recibo = new StringBuilder();
        recibo.append("Comprovante de pagamento com cartao\n")
              .append("  Pedido ........: ").append(pedido).append('\n')
              .append("  Valor .........: ").append(valor).append('\n')
              .append("  Autorizacao ...: ").append(autorizacao.identificador()).append('\n')
              .append("  Parcelamento ..: ").append(parcelas).append("x\n");
        for (int i = 0; i < rateio.size(); i++) {
            recibo.append("    parcela ").append(i + 1).append('/').append(parcelas)
                  .append(" ...: ").append(rateio.get(i)).append('\n');
        }
        recibo.append("  Situacao ......: aprovado");
        return recibo.toString();
    }

    @Override
    public String formato() {
        return "recibo de parcelamento";
    }
}
