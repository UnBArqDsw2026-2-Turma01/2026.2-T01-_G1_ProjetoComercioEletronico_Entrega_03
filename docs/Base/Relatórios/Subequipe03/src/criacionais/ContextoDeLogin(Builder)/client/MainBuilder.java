package client;

import builder.DiretorContextoLogin;
import builder.RequisicaoLoginBuilder;
import builder.RequisicaoLoginConcretoBuilder;
import product.RequisicaoLogin;

public class MainBuilder {
    public static void main(String[] args) {
        RequisicaoLoginBuilder builder = new RequisicaoLoginConcretoBuilder();
        DiretorContextoLogin diretor = new DiretorContextoLogin(builder);

        System.out.println(">>> CENARIO 1: Receita do Director - Login Web de Baixo Risco (Achado-L03 e L05)");
        RequisicaoLogin loginBaixoRisco = diretor.construirLoginWebBaixoRisco("pedro@unb.br", "HASH_SENHA_123");
        loginBaixoRisco.exibirResumo();

        System.out.println(">>> CENARIO 2: Receita do Director - Login em Dispositivo Novo com 2FA");
        RequisicaoLogin loginAltoRisco = diretor.construirLoginDispositivoNovoAltoRisco("pedro@unb.br", "HASH_SENHA_123", "849201");
        loginAltoRisco.exibirResumo();

        System.out.println(">>> CENARIO 3: Uso Direto do Builder (Fluente) - Login Mobile Customizado");
        RequisicaoLogin loginCustomizado = builder.resetar()
                .comCredenciais("julia@unb.br", "HASH_9988")
                .comTokenCaptcha("CAPTCHA_MOBILE_SDK_551")
                .comTelemetriaDispositivo("10.0.0.5", "APP_ANDROID_14", "BAIXO")
                .comReplicacaoEcossistema(false)
                .build();
        loginCustomizado.exibirResumo();

        System.out.println(">>> CENARIO 4: Validacao de Invariante do Builder (Risco Alto sem informar 2FA)");
        try {
            builder.resetar()
                   .comCredenciais("joao@unb.br", "HASH_1122")
                   .comTelemetriaDispositivo("200.10.1.9", "TOR_BROWSER", "ALTO")
                   // Esqueceu de chamar .comDesafio2FA(...)
                   .build();
        } catch (IllegalStateException e) {
            System.out.println("   -> [SUCESSO NA PREVENCAO] O Builder impediu a criacao de um objeto inconsistente:");
            System.out.println("      " + e.getMessage());
        }
    }
}
