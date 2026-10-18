package familias.pix;

import dominio.Dinheiro;
import produto.Autorizacao;
import produto.Autorizador;

/**
 * Produto concreto A da familia Pix: a autorizacao sai na hora.
 *
 * E o contraste com o boleto. No boleto o pedido fica em Autorizando ate a
 * compensacao chegar; no Pix o desfecho volta na mesma chamada, e o pedido
 * passa direto de Autorizando para Pago ou Recusado na Maquina de Estados do
 * Pedido (Entrega 2).
 *
 * Rastreabilidade (Entrega 2): Pix e uma das constantes da enumeracao
 * MeioPagamento do Diagrama de Classes da Subequipe 01, e ja tinha
 * comportamento proprio no Diagrama de Classes da Subequipe 03, que modelou um
 * ProcessadorPix separado do ProcessadorCartao para evitar condicionais por
 * meio de pagamento. Esta classe e esse ProcessadorPix, agora como parte de uma
 * familia. A tarefa "Autorizar pagamento" vem do BPMN 1 (Entrega 1).
 *
 * Autor: Maria Clara Sena de Lima (familia Pix).
 */
public final class AutorizadorPix implements Autorizador {

    /**
     * A Maquina de Estados do Pedido deixa after(tempo limite) sem valor de
     * proposito: o prazo nunca foi observado, porque o Recorte B da Entrega 1
     * parou antes da tela de pagamento. O contrato exige um int, entao este e
     * um parametro de demonstracao, e nao uma afirmacao sobre a plataforma.
     */
    private static final int PRAZO_DE_DEMONSTRACAO_EM_MINUTOS = 30;

    @Override
    public Autorizacao autorizar(String pedido, Dinheiro valor) {
        if (valor.centavos() == 0) {
            return Autorizacao.recusada("Pix sem valor nao pode ser gerado");
        }
        return Autorizacao.aprovada("PIX-" + pedido,
                "Pix confirmado na hora; sem espera por compensacao");
    }

    @Override
    public int prazoLimiteEmMinutos() {
        return PRAZO_DE_DEMONSTRACAO_EM_MINUTOS;
    }

    /** Pix: o desfecho chega na mesma chamada (contrato, reuniao de 18/10). */
    @Override
    public boolean ehAssincrono() {
        return false;
    }
}
