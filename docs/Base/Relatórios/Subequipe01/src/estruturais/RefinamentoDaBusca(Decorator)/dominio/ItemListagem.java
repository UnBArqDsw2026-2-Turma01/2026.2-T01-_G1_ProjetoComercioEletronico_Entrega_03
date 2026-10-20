package dominio;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Um anuncio como ele aparece no cartao da listagem: o dado bruto do catalogo
 * mais os selos que os decoradores acrescentam (frete e prazo, reputacao do
 * vendedor, e o que mais vier depois).
 *
 * A separacao entre Anuncio e ItemListagem e o que permite que os decoradores
 * de enriquecimento existam sem tocar na classe de dominio: o anuncio continua
 * sendo o que o catalogo guarda, e o item e o que a busca devolve.
 *
 * Imutavel: comSelo devolve uma copia nova, entao um decorador nunca altera o
 * resultado que recebeu do decorador anterior.
 *
 * Autor: Pedro Luciano de Azevedo (dominio compartilhado e contratos).
 */
public final class ItemListagem {

    private final Anuncio anuncio;
    private final Map<String, String> selos;

    public ItemListagem(Anuncio anuncio) {
        this(anuncio, new LinkedHashMap<String, String>());
    }

    private ItemListagem(Anuncio anuncio, Map<String, String> selos) {
        this.anuncio = anuncio;
        this.selos = selos;
    }

    /** Devolve uma copia do item com mais um selo. */
    public ItemListagem comSelo(String chave, String valor) {
        Map<String, String> novos = new LinkedHashMap<String, String>(selos);
        novos.put(chave, valor);
        return new ItemListagem(anuncio, novos);
    }

    public Anuncio anuncio() {
        return anuncio;
    }

    public Map<String, String> selos() {
        return Collections.unmodifiableMap(selos);
    }

    @Override
    public String toString() {
        StringBuilder linha = new StringBuilder(anuncio.toString());
        for (Map.Entry<String, String> selo : selos.entrySet()) {
            linha.append(" | ").append(selo.getKey()).append(": ").append(selo.getValue());
        }
        return linha.toString();
    }
}
