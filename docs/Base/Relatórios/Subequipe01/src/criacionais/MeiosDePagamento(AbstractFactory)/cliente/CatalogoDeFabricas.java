package cliente;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import dominio.MeioPagamento;
import fabrica.FabricaMeioPagamento;

/**
 * Selecao da familia a partir da constante de MeioPagamento.
 *
 * Esta responsabilidade fica fora da interface FabricaMeioPagamento de
 * proposito: uma fabrica abstrata nao escolhe entre as suas implementacoes. Em
 * GAMMA et al. (1995) a escolha da fabrica concreta e feita uma unica vez, na
 * inicializacao, e e exatamente o papel desta classe.
 *
 * As fabricas sao resolvidas pelo nome da classe, e nao por referencia direta,
 * por uma razao de organizacao de equipe e nao de arquitetura: as quatro
 * familias sao escritas por quatro pessoas diferentes, em paralelo, na mesma
 * branch (decisao 9 da ata de 18/10/2026). Resolver por nome permite que este
 * artefato compile e rode enquanto uma familia ainda nao chegou, e que o seu
 * autor a acrescente sem precisar editar este arquivo — o que a ata proibe.
 * Quando as quatro estiverem no repositorio, as quatro aparecem.
 *
 * Autor: Patrick Anderson Carvalho dos Santos (cliente do criacional).
 */
public final class CatalogoDeFabricas {

    /** Nome da fabrica concreta esperada para cada familia. */
    private static final Map<MeioPagamento, String> CLASSES;

    /** Quem ficou responsavel por cada familia, conforme a ata de 18/10/2026. */
    private static final Map<MeioPagamento, String> RESPONSAVEIS;

    static {
        Map<MeioPagamento, String> classes = new LinkedHashMap<MeioPagamento, String>();
        classes.put(MeioPagamento.CARTAO, "fabrica.FabricaCartao");
        classes.put(MeioPagamento.PIX, "fabrica.FabricaPix");
        classes.put(MeioPagamento.BOLETO, "fabrica.FabricaBoleto");
        classes.put(MeioPagamento.SALDO_EM_CONTA, "fabrica.FabricaSaldoEmConta");
        CLASSES = Collections.unmodifiableMap(classes);

        Map<MeioPagamento, String> donos = new LinkedHashMap<MeioPagamento, String>();
        donos.put(MeioPagamento.CARTAO, "Patrick Anderson Carvalho dos Santos");
        donos.put(MeioPagamento.PIX, "Maria Clara Sena de Lima");
        donos.put(MeioPagamento.BOLETO, "Guilherme Davila Rodrigues Carneiro Sampaio");
        donos.put(MeioPagamento.SALDO_EM_CONTA, "Guilherme Costa Zanella");
        RESPONSAVEIS = Collections.unmodifiableMap(donos);
    }

    private CatalogoDeFabricas() {
    }

    /** true quando a familia ja esta no repositorio. */
    public static boolean disponivel(MeioPagamento meio) {
        return instanciar(meio) != null;
    }

    public static String responsavelPor(MeioPagamento meio) {
        return RESPONSAVEIS.get(meio);
    }

    /**
     * Devolve a fabrica da familia pedida.
     *
     * @throws IllegalStateException quando a familia ainda nao foi entregue.
     */
    public static FabricaMeioPagamento para(MeioPagamento meio) {
        FabricaMeioPagamento fabrica = instanciar(meio);
        if (fabrica == null) {
            throw new IllegalStateException("Familia " + meio.rotulo()
                    + " ainda nao esta no repositorio (responsavel: " + responsavelPor(meio) + ")");
        }
        return fabrica;
    }

    private static FabricaMeioPagamento instanciar(MeioPagamento meio) {
        String nome = CLASSES.get(meio);
        if (nome == null) {
            return null;
        }
        try {
            Class<?> tipo = Class.forName(nome);
            Object instancia = tipo.getDeclaredConstructor().newInstance();
            return (FabricaMeioPagamento) instancia;
        } catch (ClassNotFoundException ausente) {
            return null;                 // familia ainda nao entregue
        } catch (Exception problema) {
            return null;                 // sem construtor publico sem argumentos
        }
    }
}
