package modelo;

/**
 * Estados possiveis de um pagamento no G1_ProjetoComercioEletronico.
 * Cada mudanca de estado e o evento que dispara a notificacao dos observadores.
 */
public enum StatusPagamento {
    PENDENTE("Pendente"),
    EM_ANALISE("Em analise"),
    APROVADO("Aprovado"),
    RECUSADO("Recusado"),
    ESTORNADO("Estornado");

    private final String descricao;

    StatusPagamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
