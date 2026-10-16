package modelo;

import java.util.ArrayList;
import java.util.List;

import observadores.ObservadorDePagamento;

/**
 * Papel GoF: ConcreteSubject.
 * Guarda o estado (status) e a lista de observadores. Sempre que o status muda,
 * avisa todos os observadores registrados, sem conhecer a logica de nenhum deles.
 */
public class Pagamento implements PagamentoObservavel {

    private final String id;
    private final double valor;
    private StatusPagamento status;
    private final List<ObservadorDePagamento> observadores = new ArrayList<>();

    public Pagamento(String id, double valor) {
        this.id = id;
        this.valor = valor;
        this.status = StatusPagamento.PENDENTE;
    }

    public String getId() {
        return id;
    }

    public double getValor() {
        return valor;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    /**
     * Ponto central do padrao: mudou o estado, notifica os observadores.
     * Se o status nao mudou, ninguem e avisado.
     */
    public void setStatus(StatusPagamento novo) {
        if (novo == this.status) {
            return;
        }
        this.status = novo;
        notificarObservadores();
    }

    @Override
    public void adicionarObservador(ObservadorDePagamento observador) {
        if (!observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    @Override
    public void removerObservador(ObservadorDePagamento observador) {
        observadores.remove(observador);
    }

    /**
     * Percorre uma copia da lista, para que um observador possa se remover durante a notificacao.
     * A falha de um observador e tratada isoladamente, sem impedir que os demais sejam avisados.
     */
    @Override
    public void notificarObservadores() {
        for (ObservadorDePagamento observador : new ArrayList<>(observadores)) {
            try {
                observador.atualizar(this);
            } catch (RuntimeException e) {
                System.out.println("  [ERRO] " + observador.getClass().getSimpleName()
                        + " falhou: " + e.getMessage());
            }
        }
    }
}
