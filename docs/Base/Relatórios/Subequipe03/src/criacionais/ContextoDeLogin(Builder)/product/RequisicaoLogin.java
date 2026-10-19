package product;

public class RequisicaoLogin {
    private final String email;
    private final String senhaHash;
    private final String tokenCaptchaEnterprise;
    private final String ipOrigem;
    private final String deviceFingerprint;
    private final String nivelRisco;
    private final boolean exige2FA;
    private final String canal2FA;
    private final String codigo2FA;
    private final boolean replicarSessaoEcossistema;

    public RequisicaoLogin(String email, String senhaHash, String tokenCaptchaEnterprise,
                           String ipOrigem, String deviceFingerprint, String nivelRisco,
                           boolean exige2FA, String canal2FA, String codigo2FA,
                           boolean replicarSessaoEcossistema) {
        this.email = email;
        this.senhaHash = senhaHash;
        this.tokenCaptchaEnterprise = tokenCaptchaEnterprise;
        this.ipOrigem = ipOrigem;
        this.deviceFingerprint = deviceFingerprint;
        this.nivelRisco = nivelRisco;
        this.exige2FA = exige2FA;
        this.canal2FA = canal2FA;
        this.codigo2FA = codigo2FA;
        this.replicarSessaoEcossistema = replicarSessaoEcossistema;
    }

    public String getEmail() { return email; }
    public String getSenhaHash() { return senhaHash; }
    public String getTokenCaptchaEnterprise() { return tokenCaptchaEnterprise; }
    public String getIpOrigem() { return ipOrigem; }
    public String getDeviceFingerprint() { return deviceFingerprint; }
    public String getNivelRisco() { return nivelRisco; }
    public boolean isExige2FA() { return exige2FA; }
    public String getCanal2FA() { return canal2FA; }
    public String getCodigo2FA() { return codigo2FA; }
    public boolean isReplicarSessaoEcossistema() { return replicarSessaoEcossistema; }

    public void exibirResumo() {
        System.out.println("------------------------------------------------------------------");
        System.out.println("[Produto Construido: RequisicaoLogin]");
        System.out.println("  * E-mail / Senha Hash    : " + email + " / " + senhaHash);
        System.out.println("  * Token Captcha (L01)    : " + (tokenCaptchaEnterprise != null ? tokenCaptchaEnterprise : "[AUSENTE]"));
        System.out.println("  * Telemetria Dispositivo : IP=" + ipOrigem + " | Fingerprint=" + deviceFingerprint);
        System.out.println("  * Nivel de Risco (L03)   : " + nivelRisco + " (Exige 2FA? " + (exige2FA ? "SIM" : "NAO - Bypass Silencioso") + ")");
        if (exige2FA) {
            System.out.println("  * Desafio 2FA            : Canal=" + canal2FA + " | Codigo=" + codigo2FA);
        }
        System.out.println("  * Replicar Sessao (L05)  : " + (replicarSessaoEcossistema ? "ATIVO (POST /session/replicate -> 204)" : "INATIVO"));
        System.out.println("------------------------------------------------------------------\n");
    }
}
