package decorador;

import componente.FonteDeResultados;
import dominio.ConsultaCatalogo;
import dominio.Listagem;

/**
 * Decorador abstrato (Decorator, GAMMA et al., 1995).
 *
 * Guarda a referencia para a fonte que ele envolve e, por padrao, repassa a
 * chamada sem alterar nada. Cada refinamento concreto sobrescreve buscar(),
 * chama super.buscar(consulta) para obter o resultado de quem esta embaixo e
 * entao acrescenta a sua parte — filtrar, ordenar ou enriquecer.
 *
 * Regra combinada na reuniao de 18/10/2026: nenhum decorador altera a listagem
 * que recebeu. Todos devolvem uma copia nova (Listagem.comItens e afins). E
 * isso que permite montar a cadeia em qualquer ordem sem efeito colateral — e
 * tambem o que torna a ordem *semanticamente* relevante, discussao que fica
 * registrada na visao critica da pagina.
 *
 * Autor: Pedro Luciano de Azevedo (contratos).
 * Estendido por: um refinamento concreto por integrante.
 */
public abstract class RefinamentoDecorator implements FonteDeResultados {

    /** A fonte envolvida: o catalogo ou outro decorador. */
    protected final FonteDeResultados origem;

    protected RefinamentoDecorator(FonteDeResultados origem) {
        if (origem == null) {
            throw new IllegalArgumentException("Um decorador precisa envolver uma fonte de resultados");
        }
        this.origem = origem;
    }

    @Override
    public Listagem buscar(ConsultaCatalogo consulta) {
        return origem.buscar(consulta);
    }

    @Override
    public String descricao() {
        return origem.descricao() + " + " + getClass().getSimpleName();
    }
}
