package dominio;

/**
 * Criterios de ordenacao da listagem.
 *
 * Origem: enumeracao Ordenacao do Diagrama de Classes da Entrega 2. RN-A02
 * registrou que a ordenacao padrao e por relevancia, e nao por preco — por isso
 * RELEVANCIA e a primeira constante e o valor assumido por ConsultaCatalogo.
 *
 * Autor: Pedro Luciano de Azevedo (dominio compartilhado e contratos).
 */
public enum Ordenacao {

    RELEVANCIA("Mais relevantes"),
    MENOR_PRECO("Menor preco"),
    MAIOR_PRECO("Maior preco");

    private final String rotulo;

    Ordenacao(String rotulo) {
        this.rotulo = rotulo;
    }

    public String rotulo() {
        return rotulo;
    }
}
