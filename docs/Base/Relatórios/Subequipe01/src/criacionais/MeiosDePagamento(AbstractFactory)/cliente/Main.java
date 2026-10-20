package cliente;

import dominio.Dinheiro;
import dominio.MeioPagamento;
import fabrica.FabricaCartao;
import produto.Autorizacao;

/**
 * Demonstracao do Abstract Factory no terminal.
 *
 * Tres partes:
 *   1. o mesmo pedido pago pelas quatro familias, mostrando que cada uma e
 *      coerente de ponta a ponta — autorizacao, comprovante e estorno;
 *   2. os tres desfechos da familia Cartao, que sao os dois eventos de borda do
 *      BPMN 1 mais o caminho feliz;
 *   3. por que a combinacao invalida de produtos de familias diferentes nao e
 *      proibida por validacao, e sim inalcancavel por construcao.
 *
 * Autor: Patrick Anderson Carvalho dos Santos (cliente do criacional).
 */
public final class Main {

    private static final String PEDIDO = "PED-2026-0001";
    private static final Dinheiro VALOR = Dinheiro.emReais(29990);

    public static void main(String[] args) {
        titulo("1. O mesmo pedido, quatro familias");
        for (MeioPagamento meio : MeioPagamento.values()) {
            if (!CatalogoDeFabricas.disponivel(meio)) {
                System.out.println("- " + meio.rotulo() + ": familia ainda nao entregue (responsavel: "
                        + CatalogoDeFabricas.responsavelPor(meio) + ")\n");
                continue;
            }
            ServicoDeCheckout checkout = new ServicoDeCheckout(CatalogoDeFabricas.para(meio));
            System.out.println("- " + checkout.resumoDaFamilia());

            Autorizacao autorizacao = checkout.pagar(PEDIDO, VALOR);
            System.out.println("  autorizacao: " + autorizacao);
            System.out.println(indentar(checkout.comprovanteDe(PEDIDO, VALOR, autorizacao)));
            System.out.println("  reembolso: " + checkout.reembolsar(PEDIDO, VALOR, autorizacao));
            System.out.println();
        }

        titulo("2. Os tres desfechos da familia Cartao");
        System.out.println("Os dois eventos de borda do BPMN 1 viram estados distintos na Maquina");
        System.out.println("de Estados do Pedido: erro leva a Recusado, temporizador leva a Expirado.\n");

        desfecho("aprovado", new FabricaCartao(), VALOR);
        desfecho("recusado (acima do limite)", new FabricaCartao(), Dinheiro.emReais(900000));
        desfecho("expirado (fora da janela)", FabricaCartao.aposMinutos(45), VALOR);

        titulo("3. Por que a combinacao invalida nao acontece");
        System.out.println("O ServicoDeCheckout cria os tres produtos no construtor, de uma unica");
        System.out.println("fabrica, e nao expoe nenhum caminho para criar um produto isolado.");
        System.out.println("Nao existe no codigo a expressao que juntaria um comprovante de uma");
        System.out.println("familia a um autorizador de outra: ela nao compila, porque nao ha");
        System.out.println("construtor publico de produto alcancavel a partir do cliente.\n");
        for (MeioPagamento meio : MeioPagamento.values()) {
            if (!CatalogoDeFabricas.disponivel(meio)) {
                continue;
            }
            ServicoDeCheckout checkout = new ServicoDeCheckout(CatalogoDeFabricas.para(meio));
            System.out.println("  " + checkout.meio().rotulo() + " -> " + checkout.resumoDaFamilia());
        }
    }

    private static void desfecho(String rotulo, FabricaCartao fabrica, Dinheiro valor) {
        ServicoDeCheckout checkout = new ServicoDeCheckout(fabrica);
        Autorizacao autorizacao = checkout.pagar(PEDIDO, valor);
        System.out.println("- " + rotulo + " -> " + autorizacao.status());
        System.out.println("  " + autorizacao.detalhe());
        System.out.println("  reembolso: " + checkout.reembolsar(PEDIDO, valor, autorizacao));
        System.out.println();
    }

    private static void titulo(String texto) {
        System.out.println();
        System.out.println("=======================================================================");
        System.out.println(" " + texto);
        System.out.println("=======================================================================");
        System.out.println();
    }

    private static String indentar(String bloco) {
        StringBuilder saida = new StringBuilder();
        for (String linha : bloco.split("\n")) {
            saida.append("  ").append(linha).append('\n');
        }
        return saida.toString().replaceAll("\n$", "");
    }
}
