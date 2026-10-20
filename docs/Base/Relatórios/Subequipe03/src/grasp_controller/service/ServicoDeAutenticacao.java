package service;

import context.RequisicaoLogin;

public class ServicoDeAutenticacao {
    public boolean autorizar(RequisicaoLogin req) {
        // Simulador da Chain of Responsibility
        
        // Cenário 4 (Corrigido: Rejeita QUALQUER token que não comece com "CAPTCHA_")
        if (req.getTokenCaptcha() == null || !req.getTokenCaptcha().startsWith("CAPTCHA_")) {
            throw new SecurityException("[Falha no Elo 1] ValidadorCaptcha barrou o token inválido ou ausente (Achado L01).");
        }
        
        // Cenário 3
        if (!"HASH_SENHA_123".equals(req.getSenhaHash())) {
            throw new SecurityException("[Falha no Elo 2] ValidadorCredenciais identificou senha errada (Achado L02).");
        }
        
        // Cenário 2 (O Cenário 5 não chega aqui porque o Builder barra antes)
        String metadados = String.valueOf(req.getMetadadosDispositivo());
        if (metadados.contains("RISCO_ALTO") && !"123456".equals(req.getCodigo2FA())) {
             throw new SecurityException("[Falha no Elo 4] ValidadorTwoFactor rejeitou o código 2FA.");
        }
        
        // Cenário 1 (Tudo OK)
        return true;
    }
}
