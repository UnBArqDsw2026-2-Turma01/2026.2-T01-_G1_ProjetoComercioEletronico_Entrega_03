package decorador;

import componente.FonteDeResultados;
import dominio.ConsultaCatalogo;
import dominio.Faceta;
import dominio.ItemListagem;
import dominio.Listagem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Decorador concreto (ConcreteDecorator, GAMMA et al., 1995): filtro por
 * facetas com contagem previa.
 *
 * Origem: RN-A03 e RF-A02 da Entrega 1 — cada opcao de filtro exibe, antes do
 * clique, quantos resultados restarao, e os filtros sao cumulativos. No Diagrama
 * de Casos de Uso da Entrega 2, "Filtrar Resultados" e «extend» de "Buscar
 * Produtos": comportamento opcional, que so faz sentido depois que a busca ja
 * retornou resultados — exatamente o que este decorador faz sobre a fonte que
 * envolve. Na Maquina de Estados da busca (Entrega 2), alterar um filtro leva
 * de ExibindoResultados a Buscando e de volta a ExibindoResultados: o estado
 * Buscando e reutilizado, e este decorador e parte do que roda nele.
 *
 * Duas responsabilidades, nesta ordem:
 *  1) monta as facetas contando sobre a listagem recebida, ANTES de filtrar —
 *     foi o que a Entrega 1 observou: a contagem chega pronta com a resposta;
 *  2) filtra os itens pelos filtros da consulta.
 *
 * Como os filtros sao cumulativos, a contagem de uma opcao responde "quantos
 * restam se o comprador clicar nela mantendo os outros filtros". Por isso cada
 * dimensao e contada sobre os itens que passam nos filtros das DEMAIS dimensoes
 * (faceta disjuntiva): com categoria=Eletronicos ativo, "condicao: USADO" conta
 * so os eletronicos usados, e as outras categorias continuam visiveis com a sua
 * propria contagem, para o comprador poder trocar de categoria.
 *
 * Nao altera a listagem recebida: devolve uma copia com comItens e comFacetas.
 *
 * Autor: Maria Clara Sena de Lima (refinamento ComFiltroDeFacetas).
 */
public final class ComFiltroDeFacetas extends RefinamentoDecorator {

    public static final String CATEGORIA = "categoria";
    public static final String CONDICAO = "condicao";

    /** Dimensoes de faceta que este refinamento entende, na ordem de exibicao. */
    private static final String[] DIMENSOES = { CATEGORIA, CONDICAO };

    public ComFiltroDeFacetas(FonteDeResultados origem) {
        super(origem);
    }

    @Override
    public Listagem buscar(ConsultaCatalogo consulta) {
        Listagem recebida = super.buscar(consulta);

        // 1) contagem previa, calculada sobre a listagem recebida
        List<Faceta> facetas = new ArrayList<Faceta>();
        for (String dimensao : DIMENSOES) {
            facetas.addAll(contar(dimensao, recebida.itens(), consulta));
        }

        // 2) filtro cumulativo: o item precisa passar em todas as dimensoes
        List<ItemListagem> filtrados = new ArrayList<ItemListagem>();
        for (ItemListagem item : recebida.itens()) {
            if (passaNosFiltros(item, consulta, null)) {
                filtrados.add(item);
            }
        }

        return recebida.comItens(filtrados).comFacetas(facetas);
    }

    /**
     * Conta as opcoes de uma dimensao sobre os itens que passam nos filtros das
     * outras dimensoes. A ordem das opcoes segue a ordem em que aparecem.
     */
    private static List<Faceta> contar(String dimensao, List<ItemListagem> itens, ConsultaCatalogo consulta) {
        Map<String, Integer> contagem = new LinkedHashMap<String, Integer>();
        for (ItemListagem item : itens) {
            if (!passaNosFiltros(item, consulta, dimensao)) {
                continue;
            }
            String opcao = valor(item, dimensao);
            Integer atual = contagem.get(opcao);
            contagem.put(opcao, atual == null ? 1 : atual + 1);
        }
        List<Faceta> facetas = new ArrayList<Faceta>();
        for (Map.Entry<String, Integer> opcao : contagem.entrySet()) {
            facetas.add(new Faceta(dimensao, opcao.getKey(), opcao.getValue()));
        }
        return facetas;
    }

    /**
     * true se o item atende a todos os filtros de faceta da consulta, exceto o
     * da dimensao ignorada (null para nao ignorar nenhuma). Filtros com chaves
     * que este refinamento nao conhece sao deixados para outros decoradores.
     */
    private static boolean passaNosFiltros(ItemListagem item, ConsultaCatalogo consulta, String ignorada) {
        for (String dimensao : DIMENSOES) {
            if (dimensao.equals(ignorada)) {
                continue;
            }
            String escolhida = consulta.filtro(dimensao);
            if (escolhida != null && !escolhida.equalsIgnoreCase(valor(item, dimensao))) {
                return false;
            }
        }
        return true;
    }

    private static String valor(ItemListagem item, String dimensao) {
        if (CATEGORIA.equals(dimensao)) {
            return item.anuncio().categoria();
        }
        return item.anuncio().condicao().name();
    }
}
