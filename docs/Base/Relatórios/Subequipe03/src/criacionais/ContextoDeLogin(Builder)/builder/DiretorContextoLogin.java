package builder;

import product.RequisicaoLogin;

public class DiretorContextoLogin {
    private final RequisicaoLoginBuilder builder;

    public DiretorContextoLogin(RequisicaoLoginBuilder builder) {
        this.builder = builder;
    }

    /**
     * Receita 1: Login Web Padrão de Baixo Risco (Achados L01, L03 e L05)
     * Carrega token reCAPTCHA no page load, passa sem 2FA e replica sessão no Mercado Pago.
     */
    public RequisicaoLogin construirLoginWebBaixoRisco(String email, String senhaHash) {
        return builder.resetar()
                .comCredenciais(email, senhaHash)
                .comTokenCaptcha("CAPTCHA_ENTERPRISE_OK_9981")
                .comTelemetriaDispositivo("192.168.1.10", "CHROME_LINUX_CONHECIDO", "BAIXO")
                .comReplicacaoEcossistema(true)
                .build();
    }

    /**
     * Receita 2: Login em Dispositivo Novo de Alto Risco (Exige desafio 2FA por SMS)
     */
    public RequisicaoLogin construirLoginDispositivoNovoAltoRisco(String email, String senhaHash, String codigo2FA) {
        return builder.resetar()
                .comCredenciais(email, senhaHash)
                .comTokenCaptcha("CAPTCHA_ENTERPRISE_OK_7742")
                .comTelemetriaDispositivo("177.45.22.11", "DISPOSITIVO_NAO_RECONHECIDO", "ALTO")
                .comDesafio2FA("SMS", codigo2FA)
                .comReplicacaoEcossistema(true)
                .build();
    }

    /**
     * Receita 3: Tentativa Automatizada (Bot sem token Captcha e sem replicação)
     */
    public RequisicaoLogin construirTentativaBotSemCaptcha(String email, String senhaHash) {
        return builder.resetar()
                .comCredenciais(email, senhaHash)
                .comTelemetriaDispositivo("45.22.19.01", "HEADLESS_SCRIPT", "BAIXO")
                .comReplicacaoEcossistema(false)
                .build();
    }
}
