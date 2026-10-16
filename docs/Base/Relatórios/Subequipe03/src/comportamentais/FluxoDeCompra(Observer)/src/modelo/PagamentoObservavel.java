package modelo;

import observadores.ObservadorDePagamento;

/**
 * Papel GoF: Subject.
 * Define o contrato de quem pode ser observado: registrar, remover e avisar observadores.
 */
public interface PagamentoObservavel {

    void adicionarObservador(ObservadorDePagamento observador);

    void removerObservador(ObservadorDePagamento observador);

    void notificarObservadores();
}
