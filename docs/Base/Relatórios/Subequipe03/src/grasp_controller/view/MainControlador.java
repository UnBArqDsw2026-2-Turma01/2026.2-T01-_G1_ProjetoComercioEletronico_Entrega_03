package view;

import controller.ControladorDeAutenticacao;
import dto.ResultadoAutenticacaoDTO;

public class MainControlador {
    private static final String EMAIL = "pedro@unb.br";
    private static final String SENHA = "HASH_SENHA_123";
    private static final String CAPTCHA_OK = "CAPTCHA_OK_TOKEN_VALIDO";

    public static void main(String[] args) {
        ControladorDeAutenticacao controlador = new ControladorDeAutenticacao();

        executar(controlador, "CENARIO 1: Risco baixo (bypass silencioso de 2FA)",
                EMAIL, SENHA, CAPTCHA_OK, "BAIXO", "");
        executar(controlador, "CENARIO 2: Risco alto com 2FA correto",
                EMAIL, SENHA, CAPTCHA_OK, "ALTO", "123456");
        executar(controlador, "CENARIO 3: Senha incorreta (curto-circuito no Elo 2)",
                EMAIL, "SENHA_ERRADA", CAPTCHA_OK, "BAIXO", "");
        executar(controlador, "CENARIO 4: Token Captcha falso (curto-circuito no Elo 1)",
                EMAIL, SENHA, "TOKEN_FALSO", "BAIXO", "");
        executar(controlador, "CENARIO 5: Risco alto sem codigo 2FA (barrado pelo Builder)",
                EMAIL, SENHA, CAPTCHA_OK, "ALTO", "");
    }

    private static void executar(ControladorDeAutenticacao c, String titulo,
                                 String email, String senha, String captcha, String risco, String codigo) {
        System.out.println("\n######## " + titulo + " ########");
        ResultadoAutenticacaoDTO dto = c.processarSolicitacaoLogin(email, senha, captcha, risco, codigo);
        System.out.println("[View] sucesso=" + dto.isSucesso() + " | tokenEmitido=" + dto.isTokenSessaoEmitido());
    }
}
