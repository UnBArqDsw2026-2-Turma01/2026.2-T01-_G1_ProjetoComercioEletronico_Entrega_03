package builder;

import product.RequisicaoLogin;

public class RequisicaoLoginConcretoBuilder implements RequisicaoLoginBuilder {
    private String email;
    private String senhaHash;
    private String tokenCaptchaEnterprise;
    private String ipOrigem;
    private String deviceFingerprint;
    private String nivelRisco;
    private boolean exige2FA;
    private String canal2FA;
    private String codigo2FA;
    private boolean replicarSessaoEcossistema;

    public RequisicaoLoginConcretoBuilder() {
        resetar();
    }

    @Override
    public RequisicaoLoginBuilder resetar() {
        this.email = null;
        this.senhaHash = null;
        this.tokenCaptchaEnterprise = null;
        this.ipOrigem = "0.0.0.0";
        this.deviceFingerprint = "DESCONHECIDO";
        this.nivelRisco = "BAIXO";
        this.exige2FA = false;
        this.canal2FA = null;
        this.codigo2FA = null;
        this.replicarSessaoEcossistema = false;
        return this;
    }

    @Override
    public RequisicaoLoginBuilder comCredenciais(String email, String senhaHash) {
        this.email = email;
        this.senhaHash = senhaHash;
        return this;
    }

    @Override
    public RequisicaoLoginBuilder comTokenCaptcha(String token) {
        this.tokenCaptchaEnterprise = token;
        return this;
    }

    @Override
    public RequisicaoLoginBuilder comTelemetriaDispositivo(String ip, String fingerprint, String risco) {
        this.ipOrigem = ip;
        this.deviceFingerprint = fingerprint;
        this.nivelRisco = risco;
        // Se o risco for ALTO, ativa automaticamente a exigência de 2FA (Achado-L03)
        this.exige2FA = "ALTO".equalsIgnoreCase(risco);
        return this;
    }

    @Override
    public RequisicaoLoginBuilder comDesafio2FA(String canal, String codigo) {
        this.exige2FA = true;
        this.canal2FA = canal;
        this.codigo2FA = codigo;
        return this;
    }

    @Override
    public RequisicaoLoginBuilder comReplicacaoEcossistema(boolean ativar) {
        this.replicarSessaoEcossistema = ativar;
        return this;
    }

    private void validarInvariantes() {
        if (email == null || email.isBlank() || senhaHash == null || senhaHash.isBlank()) {
            throw new IllegalStateException(
                "[Erro do Builder] Nao e possivel construir uma RequisicaoLogin sem e-mail e senhaHash."
            );
        }
        if (exige2FA && (canal2FA == null || codigo2FA == null || codigo2FA.isBlank())) {
            throw new IllegalStateException(
                "[Erro do Builder] Sessao classificada como RISCO ALTO exige que o canal e o codigo 2FA sejam preenchidos."
            );
        }
    }

    @Override
    public RequisicaoLogin build() {
        validarInvariantes();

        RequisicaoLogin produtoPronto = new RequisicaoLogin(
            email, senhaHash, tokenCaptchaEnterprise,
            ipOrigem, deviceFingerprint, nivelRisco,
            exige2FA, canal2FA, codigo2FA,
            replicarSessaoEcossistema
        );

        resetar();
        return produtoPronto;
    }
}
