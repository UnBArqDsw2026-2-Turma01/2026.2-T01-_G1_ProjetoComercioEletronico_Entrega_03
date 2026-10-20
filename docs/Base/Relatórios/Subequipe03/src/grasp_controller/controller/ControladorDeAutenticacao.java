package controller;

import builder.RequisicaoLoginBuilder;
import builder.RequisicaoLoginConcretoBuilder;
import dto.ResultadoAutenticacaoDTO;
import product.RequisicaoLogin;
import service.ServicoDeAutenticacao;

public class ControladorDeAutenticacao {
    
    private final RequisicaoLoginBuilder builder;
    private final ServicoDeAutenticacao servicoAutenticacao;

    public ControladorDeAutenticacao() {
        this.builder = new RequisicaoLoginConcretoBuilder();
        this.servicoAutenticacao = new ServicoDeAutenticacao();
    }

    public ResultadoAutenticacaoDTO processarSolicitacaoLogin(
            String email, String senha, String tokenCaptcha, String nivelRisco, String codigo2FA) {
        
        try {
            // 1. Delega a montagem ao Builder
            RequisicaoLogin requisicao = builder.resetar()
                    .comCredenciais(email, senha)
                    .comTokenCaptcha(tokenCaptcha)
                    .comTelemetriaDispositivo("127.0.0.1", "WEB_CLIENT", nivelRisco)
                    .comDesafio2FA("SMS", codigo2FA)
                    .comReplicacaoEcossistema(true)
                    .build();

            // 2. Tenta autorizar na Chain of Responsibility
            boolean autorizado;
            try {
                autorizado = servicoAutenticacao.autorizar(paraContextoDaCadeia(requisicao));
            } catch (SecurityException ex) {
                // Cenários 3 e 4: A Chain barrou a requisição!
                return new ResultadoAutenticacaoDTO(false, "Cadeia de Segurança: " + ex.getMessage(), false);
            }

            // 3. Sucesso (Cenários 1 e 2)
            if (autorizado) {
                return new ResultadoAutenticacaoDTO(true, "Autenticação bem-sucedida. Sessão replicada (Achado-L05).", true);
            }
            return new ResultadoAutenticacaoDTO(false, "Falha desconhecida.", false);

        } catch (IllegalStateException e) {
            // Cenário 5: O Builder barrou antes de criar o objeto (Risco Alto sem 2FA)
            return new ResultadoAutenticacaoDTO(false, "Erro de validação do Builder: " + e.getMessage(), false);
        }
    }

    // Adaptador para a Chain
    private context.RequisicaoLogin paraContextoDaCadeia(RequisicaoLogin r) {
        String metadados = "IP=" + r.getIpOrigem() + "|RISCO_" + r.getNivelRisco();
        return new context.RequisicaoLogin(
                r.getEmail(), r.getSenhaHash(), r.getTokenCaptchaEnterprise(),
                r.getCodigo2FA(), metadados);
    }
}
