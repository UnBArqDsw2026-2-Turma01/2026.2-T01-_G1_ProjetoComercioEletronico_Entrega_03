package context;

public class RequisicaoLogin {
    private String email, senhaHash, tokenCaptcha, codigo2FA;
    private Object metadadosDispositivo;

    public RequisicaoLogin(String email, String senhaHash, String tokenCaptcha, String codigo2FA, Object metadados) {
        this.email = email; this.senhaHash = senhaHash; this.tokenCaptcha = tokenCaptcha; 
        this.codigo2FA = codigo2FA; this.metadadosDispositivo = metadados;
    }

    public String getSenhaHash() { return senhaHash; }
    public String getTokenCaptcha() { return tokenCaptcha; }
    public String getCodigo2FA() { return codigo2FA; }
    public Object getMetadadosDispositivo() { return metadadosDispositivo; }
}
