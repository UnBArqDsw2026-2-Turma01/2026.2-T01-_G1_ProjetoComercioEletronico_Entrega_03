package handler;

import context.RequisicaoLogin;

public class ValidadorRisco extends ValidadorHandler {

    @Override
    public boolean processarRequisicao(RequisicaoLogin req) {
        System.out.println("[3. ValidadorRisco] Avaliando score de risco e metadados do dispositivo...");

        String metadados = String.valueOf(req.getMetadadosDispositivo());

        if (metadados.contains("RISCO_ALTO")) {
            System.out.println("   -> [ALERTA] Score de risco elevado detectado! Exigindo confirmacao 2FA.");
            req.setExige2FA(true);
        } else {
            System.out.println("   -> [OK] Score de risco baixo. Passando silenciosamente sem exigir 2FA (Achado-L03).");
            req.setExige2FA(false);
        }

        return passarParaOProximo(req);
    }
}
