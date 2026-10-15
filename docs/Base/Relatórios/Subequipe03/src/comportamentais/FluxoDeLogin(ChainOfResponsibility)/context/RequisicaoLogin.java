package context;

public class RequisicaoLogin {
    private String email;
    private String senhaHash;
    private String tokenCaptcha;
    private String codigo2FA;
    private Object metadadosDispositivo;
    private boolean exige2FA;

    public RequisicaoLogin(String email, String senhaHash, String tokenCaptcha, 
                           String codigo2FA, Object metadadosDispositivo) {
        this.email = email;
        this.senhaHash = senhaHash;
        this.tokenCaptcha = tokenCaptcha;
        this.codigo2FA = codigo2FA;
        this.metadadosDispositivo = metadadosDispositivo;
        this.exige2FA = false;
    }

    public String getEmail() {
        return email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public String getTokenCaptcha() {
        return tokenCaptcha;
    }

    public String getCodigo2FA() {
        return codigo2FA;
    }

    public Object getMetadadosDispositivo() {
        return metadadosDispositivo;
    }

    public boolean isExige2FA() {
        return exige2FA;
    }

    public void setExige2FA(boolean exige2FA) {
        this.exige2FA = exige2FA;
    }
}
