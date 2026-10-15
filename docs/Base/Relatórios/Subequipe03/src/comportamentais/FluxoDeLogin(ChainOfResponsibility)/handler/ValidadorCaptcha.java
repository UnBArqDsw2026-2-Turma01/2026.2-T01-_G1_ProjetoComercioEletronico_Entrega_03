package handler;

import context.RequisicaoLogin;

public class ValidadorCaptcha extends ValidadorHandler {

    @Override
    public boolean processarRequisicao(RequisicaoLogin req) {
        System.out.println("[1. ValidadorCaptcha] Verificando token reCAPTCHA Enterprise carregado no page load (Achado-L01)...");

        if (req.getTokenCaptcha() == null || !req.getTokenCaptcha().startsWith("CAPTCHA_OK")) {
            System.out.println("   -> [BLOQUEADO] Token de Captcha invalido ou ausente. Interrompendo a cadeia.");
            return false;
        }

        System.out.println("   -> [OK] Token Captcha validado com sucesso.");
        return passarParaOProximo(req);
    }
}
