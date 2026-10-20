package product;

public class RequisicaoLogin {
    private String email, senhaHash, tokenCaptchaEnterprise, ipOrigem, deviceFingerprint, nivelRisco, codigo2FA;

    public RequisicaoLogin(String email, String senhaHash, String tokenCaptchaEnterprise,
                           String ipOrigem, String deviceFingerprint, String nivelRisco, String codigo2FA) {
        this.email = email; this.senhaHash = senhaHash; this.tokenCaptchaEnterprise = tokenCaptchaEnterprise;
        this.ipOrigem = ipOrigem; this.deviceFingerprint = deviceFingerprint; this.nivelRisco = nivelRisco; this.codigo2FA = codigo2FA;
    }

    public String getEmail() { return email; }
    public String getSenhaHash() { return senhaHash; }
    public String getTokenCaptchaEnterprise() { return tokenCaptchaEnterprise; }
    public String getIpOrigem() { return ipOrigem; }
    public String getDeviceFingerprint() { return deviceFingerprint; }
    public String getNivelRisco() { return nivelRisco; }
    public String getCodigo2FA() { return codigo2FA; }
}
