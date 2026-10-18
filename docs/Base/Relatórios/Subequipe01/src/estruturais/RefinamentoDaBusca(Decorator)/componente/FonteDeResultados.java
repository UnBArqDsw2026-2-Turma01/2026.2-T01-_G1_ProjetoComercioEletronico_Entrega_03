package componente;

import dominio.ConsultaCatalogo;
import dominio.Listagem;

/**
 * Componente (Component, GAMMA et al., 1995): tudo o que sabe responder a uma
 * consulta devolvendo uma listagem.
 *
 * E a interface comum ao catalogo bruto e a todos os refinamentos, e e o que
 * torna o Decorator possivel: quem chama buscar() nao sabe se esta falando com
 * o catalogo ou com uma cadeia de quatro refinamentos empilhados.
 *
 * Autor: Pedro Luciano de Azevedo (contratos).
 */
public interface FonteDeResultados {

    /** Responde a consulta com a listagem correspondente. */
    Listagem buscar(ConsultaCatalogo consulta);

    /**
     * Descricao da cadeia montada, usada na demonstracao para tornar visivel
     * que o objeto final e uma pilha de decoradores, e nao uma classe so.
     */
    String descricao();
}
