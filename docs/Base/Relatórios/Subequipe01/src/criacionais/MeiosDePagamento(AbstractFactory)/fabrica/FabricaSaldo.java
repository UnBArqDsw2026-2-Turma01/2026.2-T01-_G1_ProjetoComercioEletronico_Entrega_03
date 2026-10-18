package fabrica;

import dominio.Dinheiro;
import dominio.MeioPagamento;
import familias.saldo.AutorizadorSaldo;
import familias.saldo.ComprovanteSaldo;
import familias.saldo.EstornoSaldo;
import produto.Autorizador;
import produto.Comprovante;
import produto.PoliticaEstorno;

/**
 * Fabrica concreta (ConcreteFactory, GAMMA et al., 1995) da familia Saldo em
 * conta.
 *
 * E a familia em que a coerencia entre os produtos fica mais visivel: os tres
 * operam sobre o mesmo saldo. O autorizador debita, o comprovante informa o
 * saldo restante e o estorno credita de volta. Para isso, a fabrica cria um
 * unico AutorizadorSaldo e o entrega ao comprovante e ao estorno. O
 * ServicoDeCheckout recebe esta fabrica como FabricaMeioPagamento e nunca ve
 * essa ligacao.
 *
 * Autor: Guilherme Costa Zanella (familia Saldo em conta).
 */
public final class FabricaSaldo implements FabricaMeioPagamento {

    /** Saldo de demonstracao (R$ 500,00): o valor real nao foi observado. */
    private static final long SALDO_DE_DEMONSTRACAO_EM_CENTAVOS = 50000;

    private final AutorizadorSaldo autorizador;

    public FabricaSaldo() {
        this(Dinheiro.emReais(SALDO_DE_DEMONSTRACAO_EM_CENTAVOS));
    }

    public FabricaSaldo(Dinheiro saldoDisponivel) {
        this.autorizador = new AutorizadorSaldo(saldoDisponivel);
    }

    @Override
    public MeioPagamento meio() {
        return MeioPagamento.SALDO_EM_CONTA;
    }

    @Override
    public Autorizador criarAutorizador() {
        return autorizador;
    }

    @Override
    public Comprovante criarComprovante() {
        return new ComprovanteSaldo(autorizador);
    }

    @Override
    public PoliticaEstorno criarPoliticaEstorno() {
        return new EstornoSaldo(autorizador);
    }
}
