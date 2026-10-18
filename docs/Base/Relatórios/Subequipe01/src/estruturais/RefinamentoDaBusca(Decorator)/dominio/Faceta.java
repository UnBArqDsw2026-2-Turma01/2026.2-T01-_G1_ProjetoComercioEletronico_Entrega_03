package dominio;

/**
 * Uma opcao de filtro com a contagem de quantos resultados restarao se o
 * comprador clicar nela.
 *
 * Origem: RN-A03 e RF-A02 da Entrega 1 — cada opcao de filtro exibe
 * antecipadamente quantos resultados restarao. A contagem e atributo, e nao
 * calculo sob demanda, porque ela ja vem pronta na resposta da busca, antes de
 * o comprador filtrar: contar antes de filtrar tem preco, e esse preco esta
 * registrado no SIG da Entrega 1 como contribuicao negativa sobre Tempo de
 * Resposta.
 *
 * Autor: Pedro Luciano de Azevedo (dominio compartilhado e contratos).
 */
public final class Faceta {

    private final String nome;
    private final String opcao;
    private final int contagemPrevia;

    public Faceta(String nome, String opcao, int contagemPrevia) {
        this.nome = nome;
        this.opcao = opcao;
        this.contagemPrevia = contagemPrevia;
    }

    public String nome() {
        return nome;
    }

    public String opcao() {
        return opcao;
    }

    public int contagemPrevia() {
        return contagemPrevia;
    }

    @Override
    public String toString() {
        return nome + ": " + opcao + " (" + contagemPrevia + ")";
    }
}
