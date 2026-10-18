package dominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * O que a busca devolve: os itens encontrados, as facetas com contagem previa e,
 * quando nao ha resultado, os termos alternativos.
 *
 * Origem: decisoes 2 e 3 do Diagrama de Classes da Entrega 2.
 * Listagem.termosAlternativos vem de RN-A01 e RF-A06 — a busca sem resultado
 * oferece termos alternativos em vez de uma tela vazia.
 *
 * Imutavel: cada decorador recebe uma listagem e devolve outra, com os itens
 * filtrados, ordenados ou enriquecidos. E esse contrato que mantem a cadeia
 * previsivel, independentemente da ordem em que ela for montada.
 *
 * Autor: Pedro Luciano de Azevedo (dominio compartilhado e contratos).
 */
public final class Listagem {

    private final List<ItemListagem> itens;
    private final List<Faceta> facetas;
    private final List<String> termosAlternativos;

    public Listagem(List<ItemListagem> itens) {
        this(itens, new ArrayList<Faceta>(), new ArrayList<String>());
    }

    private Listagem(List<ItemListagem> itens, List<Faceta> facetas, List<String> termosAlternativos) {
        this.itens = itens;
        this.facetas = facetas;
        this.termosAlternativos = termosAlternativos;
    }

    public Listagem comItens(List<ItemListagem> novosItens) {
        return new Listagem(novosItens, facetas, termosAlternativos);
    }

    public Listagem comFacetas(List<Faceta> novasFacetas) {
        return new Listagem(itens, novasFacetas, termosAlternativos);
    }

    public Listagem comTermosAlternativos(List<String> novosTermos) {
        return new Listagem(itens, facetas, novosTermos);
    }

    public List<ItemListagem> itens() {
        return Collections.unmodifiableList(itens);
    }

    public List<Faceta> facetas() {
        return Collections.unmodifiableList(facetas);
    }

    public List<String> termosAlternativos() {
        return Collections.unmodifiableList(termosAlternativos);
    }

    /** Quantidade de resultados desta listagem. */
    public int total() {
        return itens.size();
    }

    public boolean vazia() {
        return itens.isEmpty();
    }
}
