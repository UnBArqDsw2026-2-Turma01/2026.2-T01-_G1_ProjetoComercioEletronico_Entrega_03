package familias.saldo;

import dominio.Dinheiro;
import produto.Autorizacao;
import produto.Autorizador;

/**
 * Produto concreto A da familia Saldo em conta: a autorizacao e uma consulta
 * ao saldo que o comprador mantem na propria plataforma.
 *
 * Nao ha operadora nem banco do outro lado. Por isso o desfecho sai na hora,
 * como no Pix, mas por outro motivo: no Pix a resposta vem rapido de fora; aqui
 * ela nao precisa sair de dentro. E e a unica familia em que o motivo da recusa
 * e conhecido pela plataforma — saldo insuficiente —, e nao devolvido por um
 * terceiro.
 *
 * O autorizador e quem guarda o saldo. A FabricaSaldo entrega este mesmo
 * objeto ao ComprovanteSaldo, que informa o saldo restante, e ao EstornoSaldo,
 * que credita de volta: os tres produtos falam da mesma conta.
 *
 * Rastreabilidade: tarefa "Autorizar pagamento" do BPMN 1 (Entrega 1); estado
 * Autorizando e as saidas para Pago e Recusado da Maquina de Estados do Pedido
 * (Entrega 2). O Rich Picture registrou que o dinheiro da Jenny passa todo pela
 * plataforma; o saldo em conta e a forma extrema disso.
 *
 * INFERIDO: a Entrega 1 nao observou saldo em conta (o Recorte B parou antes da
 * tela de pagamento); o meio existe na enumeracao MeioPagamento da Entrega 2. A
 * regra "recusa quando o saldo nao cobre o valor" e a regra minima para que um
 * meio chamado saldo faca sentido. Pagamento combinado (parte em saldo, parte
 * em outro meio) nao e modelado.
 *
 * Autor: Guilherme Costa Zanella (familia Saldo em conta).
 */
public final class AutorizadorSaldo implements Autorizador {

    /**
     * Parametro de demonstracao, como nas demais familias: a Maquina de Estados
     * do Pedido deixou after(tempo limite) sem valor, porque o prazo nunca foi
     * observado. Mais curto que o do Pix porque nao ha nada externo a esperar.
     */
    private static final int PRAZO_DE_DEMONSTRACAO_EM_MINUTOS = 10;

    private Dinheiro saldo;

    public AutorizadorSaldo(Dinheiro saldoDisponivel) {
        this.saldo = saldoDisponivel;
    }

    @Override
    public Autorizacao autorizar(String pedido, Dinheiro valor) {
        if (valor.centavos() == 0) {
            return Autorizacao.recusada("Pagamento com saldo sem valor nao pode ser autorizado");
        }
        if (saldo.centavos() < valor.centavos()) {
            return Autorizacao.recusada("Saldo insuficiente: disponivel " + saldo
                    + ", necessario " + valor);
        }
        // Dinheiro nao tem subtracao (valores negativos sao recusados): debito em centavos.
        saldo = new Dinheiro(saldo.centavos() - valor.centavos(), saldo.moeda());
        return Autorizacao.aprovada("SALDO-" + pedido,
                "Debitado do saldo em conta; nenhuma operadora envolvida");
    }

    @Override
    public int prazoLimiteEmMinutos() {
        return PRAZO_DE_DEMONSTRACAO_EM_MINUTOS;
    }

    /** Saldo: a decisao e interna, o desfecho chega na mesma chamada. */
    @Override
    public boolean ehAssincrono() {
        return false;
    }

    /** Saldo atual, lido pelo ComprovanteSaldo. */
    public Dinheiro saldo() {
        return saldo;
    }

    /** Devolve um valor ao saldo; usado pelo EstornoSaldo. */
    public void creditar(Dinheiro valor) {
        saldo = saldo.somar(valor);
    }
}
