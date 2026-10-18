package familias.cartao;

import dominio.Dinheiro;
import produto.Autorizacao;
import produto.Autorizador;

/**
 * Produto concreto A da familia Cartao de credito.
 *
 * E a familia com os tres desfechos. O BPMN 1 da Entrega 1 modela a tarefa
 * "Autorizar pagamento" com dois eventos de borda — um de erro e um de
 * temporizador —, e a Maquina de Estados do Pedido (Entrega 2) os transforma em
 * dois estados distintos:
 *
 *   APROVADO -> Autorizando leva a Pago;
 *   RECUSADO -> evento de borda de erro, leva ao estado Recusado, que tem saida
 *               dupla (tentar outro meio ou desistir);
 *   EXPIRADO -> evento de borda de temporizador, after(tempo limite), que leva
 *               ao estado Expirado.
 *
 * Expirado e Recusado sao estados diferentes de proposito: um e passagem do
 * tempo, o outro e resposta da operadora. A distincao esta registrada na pagina
 * da Maquina de Estados da Entrega 2.
 *
 * Autor: Patrick Anderson Carvalho dos Santos (familia Cartao de credito).
 */
public final class AutorizadorCartao implements Autorizador {

    /**
     * Prazo de demonstracao. A Maquina de Estados do Pedido deixa
     * after(tempo limite) sem valor de proposito, porque o Recorte B da Entrega 1
     * parou antes da tela de pagamento. O contrato exige um int: este numero e
     * parametro de demonstracao, nao afirmacao sobre a plataforma.
     */
    private static final int PRAZO_LIMITE_DE_DEMONSTRACAO_EM_MINUTOS = 15;

    /** Limite de demonstracao acima do qual a operadora recusa. Inferido. */
    private static final long LIMITE_DE_DEMONSTRACAO_EM_CENTAVOS = 500000L;

    /**
     * Quantos minutos se passaram entre o pedido entrar em Aguardando pagamento
     * e esta tentativa. Parametro de demonstracao: e o unico jeito de exercitar
     * o caminho do temporizador sem depender do relogio.
     */
    private final int minutosDecorridosNaTentativa;

    public AutorizadorCartao() {
        this(0);
    }

    public AutorizadorCartao(int minutosDecorridosNaTentativa) {
        if (minutosDecorridosNaTentativa < 0) {
            throw new IllegalArgumentException("Minutos decorridos nao podem ser negativos");
        }
        this.minutosDecorridosNaTentativa = minutosDecorridosNaTentativa;
    }

    @Override
    public Autorizacao autorizar(String pedido, Dinheiro valor) {
        // Evento de borda de temporizador do BPMN 1: a janela fecha antes de a
        // operadora ser consultada, entao nao ha o que recusar — ha o que expirar.
        if (minutosDecorridosNaTentativa > PRAZO_LIMITE_DE_DEMONSTRACAO_EM_MINUTOS) {
            return Autorizacao.expirada("Janela de " + PRAZO_LIMITE_DE_DEMONSTRACAO_EM_MINUTOS
                    + " min encerrada apos " + minutosDecorridosNaTentativa
                    + " min; pedido expirado antes da autorizacao");
        }
        if (valor.centavos() == 0) {
            return Autorizacao.recusada("Cobranca de valor zero nao e aceita pela operadora");
        }
        // Evento de borda de erro do BPMN 1.
        if (valor.centavos() > LIMITE_DE_DEMONSTRACAO_EM_CENTAVOS) {
            return Autorizacao.recusada("Limite do cartao insuficiente para " + valor);
        }
        return Autorizacao.aprovada("CARTAO-" + pedido,
                "Autorizada pela operadora em " + minutosDecorridosNaTentativa + " min");
    }

    @Override
    public int prazoLimiteEmMinutos() {
        return PRAZO_LIMITE_DE_DEMONSTRACAO_EM_MINUTOS;
    }

    /** Cartao: a operadora responde na mesma chamada. */
    @Override
    public boolean ehAssincrono() {
        return false;
    }
}
