package handler;

import context.RequisicaoLogin;

public class ValidadorTwoFactor extends ValidadorHandler {

    @Override
    public boolean processarRequisicao(RequisicaoLogin req) {
        System.out.println("[4. ValidadorTwoFactor] Verificando necessidade de 2FA...");

        if (!req.isExige2FA()) {
            System.out.println("   -> [BYPASS] 2FA nao exigido para esta sessao (Fluxo silencioso concluido).");
            return passarParaOProximo(req);
        }

        System.out.println("   -> [ACAO] Disparando codigo de confirmacao via Servico de Notificacao (SMS/E-mail)...");

        if ("123456".equals(req.getCodigo2FA())) {
            System.out.println("   -> [OK] Codigo 2FA (" + req.getCodigo2FA() + ") validado com sucesso!");
            return passarParaOProximo(req);
        } else {
            System.out.println("   -> [BLOQUEADO] Codigo 2FA invalido ou expirado. Habilitando cooldown de reenvio.");
            return false;
        }
    }
}
