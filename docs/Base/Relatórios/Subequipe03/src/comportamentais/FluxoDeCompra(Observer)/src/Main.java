import cliente.GatewayPagamento;
import modelo.Pagamento;
import modelo.StatusPagamento;
import observadores.EmissaoNotaFiscal;
import observadores.LiberacaoEstoque;
import observadores.LiberacaoPedido;
import observadores.NotificacaoComprador;
import observadores.ObservadorDePagamento;

/**
 * Demonstracao do GoF Observer no subsistema de pagamento do G1_ProjetoComercioEletronico.
 */
public class Main {

    public static void main(String[] args) {
        GatewayPagamento gateway = new GatewayPagamento();

        ObservadorDePagamento comprador = new NotificacaoComprador();
        ObservadorDePagamento pedido = new LiberacaoPedido();
        ObservadorDePagamento notaFiscal = new EmissaoNotaFiscal();
        ObservadorDePagamento estoque = new LiberacaoEstoque();

        System.out.println("=== Cenario 1: pagamento aprovado ===");
        Pagamento p1 = criarPagamento("PG001", 249.90, comprador, pedido, notaFiscal, estoque);
        gateway.informarResultado(p1, StatusPagamento.EM_ANALISE);
        gateway.informarResultado(p1, StatusPagamento.APROVADO);

        System.out.println("\n=== Cenario 2: pagamento recusado ===");
        Pagamento p2 = criarPagamento("PG002", 89.00, comprador, pedido, notaFiscal, estoque);
        gateway.informarResultado(p2, StatusPagamento.EM_ANALISE);
        gateway.informarResultado(p2, StatusPagamento.RECUSADO);

        System.out.println("\n=== Cenario 3: aprovado e depois estornado ===");
        Pagamento p3 = criarPagamento("PG003", 1200.00, comprador, pedido, notaFiscal, estoque);
        gateway.informarResultado(p3, StatusPagamento.APROVADO);
        gateway.informarResultado(p3, StatusPagamento.ESTORNADO);

        System.out.println("\n=== Cenario 4: comprador desativa as notificacoes ===");
        Pagamento p4 = criarPagamento("PG004", 59.90, comprador, pedido, notaFiscal, estoque);
        p4.removerObservador(comprador);
        gateway.informarResultado(p4, StatusPagamento.APROVADO);

        System.out.println("\n=== Cenario 5: novo observador sem mexer no Pagamento ===");
        Pagamento p5 = criarPagamento("PG005", 35.00, comprador);
        p5.adicionarObservador(pag -> System.out.println(
                "  [SMS] Seu pagamento " + pag.getId() + " foi " + pag.getStatus().getDescricao()));
        gateway.informarResultado(p5, StatusPagamento.APROVADO);
    }

    private static Pagamento criarPagamento(String id, double valor, ObservadorDePagamento... observadores) {
        Pagamento pagamento = new Pagamento(id, valor);
        for (ObservadorDePagamento observador : observadores) {
            pagamento.adicionarObservador(observador);
        }
        return pagamento;
    }
}
