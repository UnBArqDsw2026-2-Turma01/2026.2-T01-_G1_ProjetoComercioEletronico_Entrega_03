package dto;

public final class ResultadoAutenticacaoDTO {
    private final boolean sucesso;
    private final String mensagem;
    private final boolean tokenSessaoEmitido;

    public ResultadoAutenticacaoDTO(boolean sucesso, String mensagem, boolean tokenSessaoEmitido) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
        this.tokenSessaoEmitido = tokenSessaoEmitido;
    }

    public static ResultadoAutenticacaoDTO autorizado(String mensagem) {
        return new ResultadoAutenticacaoDTO(true, mensagem, true);
    }

    public static ResultadoAutenticacaoDTO negado(String mensagem) {
        return new ResultadoAutenticacaoDTO(false, mensagem, false);
    }

    public boolean isSucesso() { return sucesso; }
    public String getMensagem() { return mensagem; }
    public boolean isTokenSessaoEmitido() { return tokenSessaoEmitido; }

    @Override
    public String toString() {
        return "ResultadoAutenticacaoDTO{sucesso=" + sucesso
                + ", tokenSessaoEmitido=" + tokenSessaoEmitido
                + ", mensagem='" + mensagem + "'}";
    }
}
