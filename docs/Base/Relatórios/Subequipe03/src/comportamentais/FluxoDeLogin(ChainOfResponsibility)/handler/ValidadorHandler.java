package handler;

import context.RequisicaoLogin;

public abstract class ValidadorHandler {
    private ValidadorHandler proximo;

    public ValidadorHandler setProximo(ValidadorHandler handler) {
        this.proximo = handler;
        return handler;
    }

    public abstract boolean processarRequisicao(RequisicaoLogin req);

    protected boolean passarParaOProximo(RequisicaoLogin req) {
        if (this.proximo == null) {
            return true;
        }
        return this.proximo.processarRequisicao(req);
    }
}
