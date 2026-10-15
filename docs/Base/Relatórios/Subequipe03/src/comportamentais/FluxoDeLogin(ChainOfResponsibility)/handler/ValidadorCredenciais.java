package handler;

import context.RequisicaoLogin;

public class ValidadorCredenciais extends ValidadorHandler {

    @Override
    public boolean processarRequisicao(RequisicaoLogin req) {
        System.out.println("[2. ValidadorCredenciais] Consultando usuario no Banco de Dados...");

        boolean usuarioExiste = "pedro@unb.br".equalsIgnoreCase(req.getEmail());
        boolean senhaCorreta = "HASH_SENHA_123".equals(req.getSenhaHash());

        if (!usuarioExiste || !senhaCorreta) {
            System.out.println("   -> [BLOQUEADO] Credenciais invalidas.");
            System.out.println("   -> [Achado-L02] Retornando HTTP 200 com payload comprimido generico e recarregando desafio Captcha.");
            return false;
        }

        System.out.println("   -> [OK] Credenciais validas para: " + req.getEmail());
        return passarParaOProximo(req);
    }
}
