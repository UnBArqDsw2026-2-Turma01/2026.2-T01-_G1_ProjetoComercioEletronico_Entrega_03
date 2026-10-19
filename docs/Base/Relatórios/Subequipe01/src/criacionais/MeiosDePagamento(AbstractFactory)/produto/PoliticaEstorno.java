package produto;

import dominio.Dinheiro;

/**
 * Produto abstrato C da familia: como o dinheiro volta quando a reclamacao
 * termina em reembolso.
 *
 * Rastreabilidade: fluxo de reclamacao e mediacao do Rich Picture (Entrega 1),
 * acao "Estornar pagamento via operadora" do Diagrama de Atividades e transicao
 * "Em disputa -> Reembolsado" da Maquina de Estados do Pedido (Entrega 2).
 *
 * E o produto que torna o Abstract Factory necessario em vez de um Factory
 * Method: o caminho de volta do dinheiro depende do meio pelo qual ele veio, e
 * precisa ser criado junto com o autorizador que o originou.
 *
 * Autor: Pedro Luciano de Azevedo (contratos).
 * Implementado por: uma classe concreta em cada familia de familias/.
 */
public interface PoliticaEstorno {

    /** Nem toda autorizacao pode ser estornada pelo mesmo caminho. */
    boolean podeEstornar(Autorizacao autorizacao);

    /** Executa o estorno e devolve a descricao do que foi feito. */
    String estornar(String pedido, Dinheiro valor);

    /** Prazo ate o dinheiro aparecer de volta para o comprador. */
    int prazoDeDevolucaoEmDias();
}
