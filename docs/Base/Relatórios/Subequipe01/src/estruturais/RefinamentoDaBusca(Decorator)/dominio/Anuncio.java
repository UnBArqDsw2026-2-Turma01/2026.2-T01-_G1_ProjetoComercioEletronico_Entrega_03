package dominio;

/**
 * Oferta de um vendedor para um item de catalogo.
 *
 * Origem: decisao 1 do Diagrama de Classes da Entrega 2 — Produto e o item de
 * catalogo e Anuncio e a oferta, com preco, estoque e vendedor proprios
 * (RN-B09 e RN-C04). Aqui o Produto esta achatado no titulo, porque este
 * artefato modela a listagem, e nao o catalogo inteiro.
 *
 * TipoVendedor vem do Rich Picture (Entrega 1): a tensao entre loja oficial e
 * vendedor autonomo, que o Diagrama de Casos de Uso registrou como atores
 * especializados e o Diagrama de Classes como enumeracao.
 *
 * Autor: Pedro Luciano de Azevedo (dominio compartilhado e contratos).
 */
public final class Anuncio {

    public enum TipoVendedor { LOJA_OFICIAL, AUTONOMO }

    public enum Condicao { NOVO, USADO }

    private final String titulo;
    private final String vendedor;
    private final TipoVendedor tipoVendedor;
    private final Dinheiro preco;
    private final String categoria;
    private final Condicao condicao;
    private final double notaVendedor;
    private final int quantidadeAvaliacoes;
    private final int pesoEmGramas;

    public Anuncio(String titulo, String vendedor, TipoVendedor tipoVendedor, Dinheiro preco,
                   String categoria, Condicao condicao, double notaVendedor,
                   int quantidadeAvaliacoes, int pesoEmGramas) {
        this.titulo = titulo;
        this.vendedor = vendedor;
        this.tipoVendedor = tipoVendedor;
        this.preco = preco;
        this.categoria = categoria;
        this.condicao = condicao;
        this.notaVendedor = notaVendedor;
        this.quantidadeAvaliacoes = quantidadeAvaliacoes;
        this.pesoEmGramas = pesoEmGramas;
    }

    public String titulo() {
        return titulo;
    }

    public String vendedor() {
        return vendedor;
    }

    public TipoVendedor tipoVendedor() {
        return tipoVendedor;
    }

    public Dinheiro preco() {
        return preco;
    }

    public String categoria() {
        return categoria;
    }

    public Condicao condicao() {
        return condicao;
    }

    public double notaVendedor() {
        return notaVendedor;
    }

    public int quantidadeAvaliacoes() {
        return quantidadeAvaliacoes;
    }

    public int pesoEmGramas() {
        return pesoEmGramas;
    }

    @Override
    public String toString() {
        return titulo + " - " + preco + " (" + vendedor + ")";
    }
}
