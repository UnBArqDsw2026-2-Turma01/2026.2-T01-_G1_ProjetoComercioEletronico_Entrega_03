package observadores;

import modelo.Pagamento;

/**
 * Papel GoF: Observer.
 * Contrato de quem quer ser avisado quando o status de um pagamento muda.
 * O proprio pagamento e passado no aviso (modelo pull): cada observador consulta o que precisa.
 */
public interface ObservadorDePagamento {

    void atualizar(Pagamento pagamento);
}
