package service;

import context.RequisicaoLogin;

public class MainLogin {
    public static void main(String[] args) {
        ServicoDeAutenticacao authService = new ServicoDeAutenticacao();

        // CENARIO 1: Login com Risco Baixo (Caminho Feliz Silencioso - Achado-L03)
        System.out.println(">>> CENARIO 1: Risco Baixo (Sem 2FA - Achado-L03)");
        RequisicaoLogin reqRiscoBaixo = new RequisicaoLogin(
            "pedro@unb.br", 
            "HASH_SENHA_123", 
            "CAPTCHA_OK_9981", 
            null, 
            "IP_CONHECIDO | RISCO_BAIXO"
        );
        authService.autorizar(reqRiscoBaixo);

        // CENARIO 2: Login com Risco Alto + 2FA Valido
        System.out.println(">>> CENARIO 2: Risco Alto + Codigo 2FA Correto");
        RequisicaoLogin reqRiscoAltoSucesso = new RequisicaoLogin(
            "pedro@unb.br", 
            "HASH_SENHA_123", 
            "CAPTCHA_OK_7742", 
            "123456", 
            "NOVO_DISPOSITIVO | RISCO_ALTO"
        );
        authService.autorizar(reqRiscoAltoSucesso);

        // CENARIO 3: Credenciais Invalidas (Curto-circuito no Elo 2 - Achado-L02)
        System.out.println(">>> CENARIO 3: Senha Incorreta (Interrupcao no Elo 2 - Achado-L02)");
        RequisicaoLogin reqSenhaInvalida = new RequisicaoLogin(
            "pedro@unb.br", 
            "SENHA_ERRADA", 
            "CAPTCHA_OK_3321", 
            null, 
            "IP_CONHECIDO | RISCO_BAIXO"
        );
        authService.autorizar(reqSenhaInvalida);

        // CENARIO 4: Falha logo no Captcha (Curto-circuito no Elo 1 - Achado-L01)
        System.out.println(">>> CENARIO 4: Bot bloqueado pelo Captcha (Interrupcao no Elo 1 - Achado-L01)");
        RequisicaoLogin reqBot = new RequisicaoLogin(
            "pedro@unb.br", 
            "HASH_SENHA_123", 
            "TOKEN_FALSO", 
            null, 
            "BOT_DETECTED | RISCO_ALTO"
        );
        authService.autorizar(reqBot);
    }
}
