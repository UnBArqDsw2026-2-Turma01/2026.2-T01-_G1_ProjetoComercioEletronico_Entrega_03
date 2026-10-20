package facade;

public class ResumoSessaoDTO {
    private final String email;
    private final String tokenJwt;
    private final String statusReplicacaoSSO;
    private final String statusDispositivo;
    private final String protocoloAuditoria;

    public ResumoSessaoDTO(String email, String tokenJwt, String statusReplicacaoSSO,
                           String statusDispositivo, String protocoloAuditoria) {
        this.email = email;
        this.tokenJwt = tokenJwt;
        this.statusReplicacaoSSO = statusReplicacaoSSO;
        this.statusDispositivo = statusDispositivo;
        this.protocoloAuditoria = protocoloAuditoria;
    }

    public String getEmail() { return email; }
    public String getTokenJwt() { return tokenJwt; }
    public String getStatusReplicacaoSSO() { return statusReplicacaoSSO; }
    public String getStatusDispositivo() { return statusDispositivo; }
    public String getProtocoloAuditoria() { return protocoloAuditoria; }
}
