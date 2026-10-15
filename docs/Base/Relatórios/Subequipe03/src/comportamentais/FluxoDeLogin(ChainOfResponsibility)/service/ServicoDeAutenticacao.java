package service;

import context.RequisicaoLogin;
import handler.ValidadorCaptcha;
import handler.ValidadorCredenciais;
import handler.ValidadorHandler;
import handler.ValidadorRisco;
import handler.ValidadorTwoFactor;

public class ServicoDeAutenticacao {
    private ValidadorHandler cadeiaValidacao;

    public ServicoDeAutenticacao() {
        construirCadeia();
    }

    private void construirCadeia() {
        ValidadorHandler captcha = new ValidadorCaptcha();
        ValidadorHandler credenciais = new ValidadorCredenciais();
        ValidadorHandler risco = new ValidadorRisco();
        ValidadorHandler twoFactor = new ValidadorTwoFactor();

        // Montagem da cadeia: Captcha -> Credenciais -> Risco -> TwoFactor
        captcha.setProximo(credenciais)
               .setProximo(risco)
               .setProximo(twoFactor);

        this.cadeiaValidacao = captcha;
    }

    public boolean autorizar(RequisicaoLogin req) {
        System.out.println("==================================================================");
        System.out.println("Iniciando autorizacao para o usuario: " + req.getEmail());
        System.out.println("------------------------------------------------------------------");

        boolean autorizado = cadeiaValidacao.processarRequisicao(req);

        System.out.println("------------------------------------------------------------------");
        if (autorizado) {
            System.out.println("[SUCESSO] Login autorizado! Emitindo Token de Sessao.");
            System.out.println("[Achado-L05] Replicando sessao entre produtos (POST /session/replicate -> 204).");
        } else {
            System.out.println("[FALHA] A tentativa de login foi rejeitada pela cadeia de validacao.");
        }
        System.out.println("==================================================================\n");
        return autorizado;
    }
}
