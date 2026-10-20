package decorador;

import componente.FonteDeResultados;
import dominio.ConsultaCatalogo;
import dominio.ItemListagem;
import dominio.Listagem;
import dominio.Ordenacao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Decorador concreto (ConcreteDecorator, GAMMA et al., 1995): reordenacao da listagem.
 *
 * Origem: RN-A02 e RF-A03 da Entrega 1. A regra de negocio observada registra que
 * a ordenacao padrao e por RELEVANCIA, e nao por preco, permitindo ao comprador
 * alternar entre MENOR_PRECO e MAIOR_PRECO.
 *
 * Rastreabilidade:
 * - Diagrama de Casos de Uso (Entrega 2): "Ordenar listagem" e um relacionamento «extend»
 *   de "Buscar produto". O decorador encapsula esse comportamento opcional e dinamico.
 * - Diagrama de Atividades (Subequipe 02, Entrega 2): no fluxo de busca e filtragem,
 *   apos a recuperacao dos resultados, ha a etapa de ordenacao/ranqueamento dos itens.
 * - Diagrama de Componentes (Subequipe 02, Entrega 2): componente "Motor de Ranqueamento"
 *   que atua sobre o conjunto de resultados recuperados.
 *
 * Regra de Relevancia e Inferencia Declarada:
 * Para Ordenacao.RELEVANCIA, a ordem original fornecida pelo catalogo de resultados
 * e estritamente preservada. O criterio real de ranqueamento por relevancia (score textual,
 * engajamento, cliques) e um algoritmo interno do marketplace nao observavel externamente
 * em testes de caixa-preta. Portanto, declarar que RELEVANCIA preserva a ordem do catalogo
 * e uma inferencia declarada e consciente, nao uma omissao.
 *
 * REGRA DO PROJETO: nenhum decorador altera a listagem recebida — sempre devolve copia nova.
 *
 * Autor: Guilherme Davila Rodrigues Carneiro Sampaio (refinamento ComOrdenacao).
 */
public final class ComOrdenacao extends RefinamentoDecorator {

    public ComOrdenacao(FonteDeResultados origem) {
        super(origem);
    }

    @Override
    public Listagem buscar(ConsultaCatalogo consulta) {
        Listagem recebida = super.buscar(consulta);

        List<ItemListagem> listaParaOrdenar = new ArrayList<ItemListagem>(recebida.itens());
        Ordenacao ordem = consulta.ordenacao();

        if (ordem == null || ordem == Ordenacao.RELEVANCIA) {
            // Preserva a ordem original do catalogo/fonte subjacente (inferencia declarada)
            return recebida.comItens(listaParaOrdenar);
        }

        if (ordem == Ordenacao.MENOR_PRECO) {
            Collections.sort(listaParaOrdenar, new Comparator<ItemListagem>() {
                @Override
                public int compare(ItemListagem a, ItemListagem b) {
                    return Long.compare(a.anuncio().preco().centavos(), b.anuncio().preco().centavos());
                }
            });
        } else if (ordem == Ordenacao.MAIOR_PRECO) {
            Collections.sort(listaParaOrdenar, new Comparator<ItemListagem>() {
                @Override
                public int compare(ItemListagem a, ItemListagem b) {
                    return Long.compare(b.anuncio().preco().centavos(), a.anuncio().preco().centavos());
                }
            });
        }

        return recebida.comItens(listaParaOrdenar);
    }
}