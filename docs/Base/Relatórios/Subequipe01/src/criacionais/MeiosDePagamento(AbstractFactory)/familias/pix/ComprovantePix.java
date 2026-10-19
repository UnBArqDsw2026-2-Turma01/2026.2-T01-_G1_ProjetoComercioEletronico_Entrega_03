package familias.pix;

import dominio.Dinheiro;
import produto.Autorizacao;
import produto.Comprovante;

/**
 * Produto concreto B da familia Pix: comprovante em QR Code.
 *
 * So faz sentido junto com uma autorizacao Pix, porque cita o identificador que
 * o AutorizadorPix gerou. Um comprovante de Pix para uma autorizacao de boleto
 * apontaria para uma transacao que nao existe — e essa combinacao invalida que
 * a FabricaPix impede ao criar os dois juntos.
 *
 * O formato "QR Code" e o fixado no contrato Comprovante (reuniao de 18/10).
 * O conteudo do QR Code abaixo e simulado: a tela de pagamento nao foi
 * observada na Entrega 1.
 *
 * Autor: Maria Clara Sena de Lima (familia Pix).
 */
public final class ComprovantePix implements Comprovante {

    @Override
    public String gerar(String pedido, Dinheiro valor, Autorizacao autorizacao) {
        if (!autorizacao.aprovada()) {
            return "Pix do pedido " + pedido + " nao concluido: " + autorizacao.detalhe()
                    + ". Nenhum QR Code de comprovante foi emitido.";
        }
        return "Comprovante Pix\n"
                + "  Pedido ........: " + pedido + "\n"
                + "  Valor .........: " + valor + "\n"
                + "  Transacao .....: " + autorizacao.identificador() + "\n"
                + "  QR Code .......: [" + autorizacao.identificador() + "|" + valor.centavos() + "]\n"
                + "  Situacao ......: pago na hora";
    }

    @Override
    public String formato() {
        return "QR Code";
    }
}
