package controller;

import builder.RequisicaoLoginBuilder;
import builder.RequisicaoLoginConcretoBuilder;
import dto.ResultadoAutenticacaoDTO;
import product.RequisicaoLogin;
import service.ServicoDeAutenticacao;

public class ControladorDeAutenticacao {

    // Telemetria fixa de demonstração (a UI Swing não captura IP/fingerprint reais).
    private static final String IP_DEMO = "127.0.0.1";
    private static final String FINGERPRINT_DEMO = "WEB_CLIENT_SESSION";
    private static final String CANAL_2FA_PADRAO = "SMS";

    private final RequisicaoLoginBuilder builder;
    private final ServicoDeAutenticacao servicoAutenticacao;

    public ControladorDeAutenticacao() {
        this(new RequisicaoLoginConcretoBuilder(), new ServicoDeAutenticacao());
    }

    /** Construtor com injeção de dependências: permite testar o Controller com dublês. */
    public ControladorDeAutenticacao(RequisicaoLoginBuilder builder, ServicoDeAutenticacao servicoAutenticacao) {
        this.builder = builder;
        this.servicoAutenticacao = servicoAutenticacao;
    }

    /** Operação de sistema: tratamento do evento "clique em Entrar". */
    public ResultadoAutenticacaoDTO processarSolicitacaoLogin(
            String email, String senha, String tokenCaptcha, String nivelRisco, String codigo2FA) {

        System.out.println(">>> [GRASP Controller] Recebendo evento de sistema da UI...");
        System.out.println("[ControladorDeAutenticacao] Solicitacao de login recebida para: " + email);

        ResultadoAutenticacaoDTO resultado;
        try {
            // 1. Builder: monta o contexto imutável e valida as invariantes (fail-fast)
            RequisicaoLogin requisicao = montarRequisicao(email, senha, tokenCaptcha, nivelRisco, codigo2FA);

            // 2. Chain: adapta o contexto do Builder ao contexto da Chain e delega
            boolean autorizado = servicoAutenticacao.autorizar(paraContextoDaCadeia(requisicao));

            // 3. DTO: traduz o booleano da cadeia para a UI
            resultado = autorizado
                    ? ResultadoAutenticacaoDTO.autorizado("Autenticação bem-sucedida. Sessão replicada (Achado-L05).")
                    : ResultadoAutenticacaoDTO.negado("Falha na validação de segurança da cadeia.");

        } catch (IllegalStateException e) {
            // Invariante violada no Builder: a requisição nem chega a existir; a Chain não é acionada
            resultado = ResultadoAutenticacaoDTO.negado("Erro de validação do Builder: " + e.getMessage());
        }

        System.out.println(">>> [GRASP Controller] Retorno enviado à UI via DTO: " + resultado.getMensagem());
        return resultado;
    }

    private RequisicaoLogin montarRequisicao(
            String email, String senha, String tokenCaptcha, String nivelRisco, String codigo2FA) {

        builder.resetar()
                .comCredenciais(email, senha)
                .comTokenCaptcha(tokenCaptcha)
                .comTelemetriaDispositivo(IP_DEMO, FINGERPRINT_DEMO, nivelRisco);

        // O 2FA só entra na montagem quando o risco é ALTO (Achado-L03).
        // Chamar comDesafio2FA incondicionalmente forçaria exige2FA = true em todo login.
        if ("ALTO".equalsIgnoreCase(nivelRisco)) {
            builder.comDesafio2FA(CANAL_2FA_PADRAO, codigo2FA);
        }

        return builder.comReplicacaoEcossistema(true).build();
    }

    /**
     * Adaptador entre os dois modelos de requisição do projeto:
     * product.RequisicaoLogin (Builder, imutável) e context.RequisicaoLogin (Chain, mutável).
     */
    private context.RequisicaoLogin paraContextoDaCadeia(RequisicaoLogin r) {
        String metadados = "IP=" + r.getIpOrigem()
                + "|FP=" + r.getDeviceFingerprint()
                + "|RISCO_" + r.getNivelRisco().toUpperCase();

        return new context.RequisicaoLogin(
                r.getEmail(), r.getSenhaHash(), r.getTokenCaptchaEnterprise(),
                r.getCodigo2FA(), metadados);
    }
}
