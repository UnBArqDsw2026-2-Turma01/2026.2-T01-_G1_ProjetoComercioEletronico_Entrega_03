package fabrica;

import dominio.MeioPagamento;
import familias.boleto.AutorizadorBoleto;
import familias.boleto.ComprovanteBoleto;
import familias.boleto.EstornoBoleto;
import produto.Autorizador;
import produto.Comprovante;
import produto.PoliticaEstorno;

/**
 * Fabrica concreta (ConcreteFactory, GAMMA et al., 1995) da familia Boleto.
 *
 * Encapsula a criacao dos tres produtos necessarios para o meio de pagamento Boleto:
 * 1. Autorizador assincrono (aguarda compensacao bancaria);
 * 2. Comprovante com linha digitavel;
 * 3. Politica de estorno assimetrica (via deposito em conta corrente).
 *
 * O ServicoDeCheckout manipula apenas a interface FabricaMeioPagamento,
 * desacoplando a logica de negocio das peculiaridades do boleto bancario.
 *
 * Autor: Guilherme Davila Rodrigues Carneiro Sampaio (familia Boleto).
 */
public final class FabricaBoleto implements FabricaMeioPagamento {

    @Override
    public MeioPagamento meio() {
        return MeioPagamento.BOLETO;
    }

    @Override
    public Autorizador criarAutorizador() {
        return new AutorizadorBoleto();
    }

    @Override
    public Comprovante criarComprovante() {
        return new ComprovanteBoleto();
    }

    @Override
    public PoliticaEstorno criarPoliticaEstorno() {
        return new EstornoBoleto();
    }
}
