package familias.boleto;

import dominio.Dinheiro;
import produto.Autorizacao;
import produto.Autorizador;

/**
 * Produto concreto A da familia Boleto: autorizacao assincrona por compensacao bancaria.
 *
 * E o grande contraste com Pix e Cartao: o desfecho nao chega na hora. O boleto
 * e emitido e fica aguardando liquidacao pela rede bancaria, de modo que o
 * pedido permanece em "Autorizando" ate a confirmacao assincrona (compensacao).
 *
 * Rastreabilidade:
 * - Diagrama de Componentes (Subequipe 02): comunicacao com o componente externo
 *   "Operadora de Pagamento" / Sistema Bancario via barramento assincrono.
 * - Diagrama de Classes (Subequipe 01, Entrega 2): MeioPagamento.BOLETO.
 * - Maquina de Estados do Pedido: transicao assincrona de Autorizando para Pago
 *   ou Expirado apos a janela limite de pagamento.
 *
 * Autor: Guilherme Davila Rodrigues Carneiro Sampaio (familia Boleto).
 */
public final class AutorizadorBoleto implements Autorizador {

    /**
     * Prazo de demonstracao para pagamento do boleto antes de expirar (3 dias = 4320 minutos).
     * O Recorte B da Entrega 1 parou antes do pagamento real, entao o valor exato
     * e parametro de demonstracao / inferido.
     */
    private static final int PRAZO_DE_DEMONSTRACAO_EM_MINUTOS = 4320;

    @Override
    public Autorizacao autorizar(String pedido, Dinheiro valor) {
        if (valor.centavos() == 0) {
            return Autorizacao.recusada("Boleto com valor zero nao pode ser emitido");
        }
        return Autorizacao.aprovada("BOL-" + pedido,
                "Boleto gerado aguardando compensacao bancaria (assincrono)");
    }

    @Override
    public int prazoLimiteEmMinutos() {
        return PRAZO_DE_DEMONSTRACAO_EM_MINUTOS;
    }

    /**
     * Boleto e essencialmente assincrono: liquidacao depende do processamento
     * e envio do arquivo de retorno bancario.
     */
    @Override
    public boolean ehAssincrono() {
        return true;
    }
}
