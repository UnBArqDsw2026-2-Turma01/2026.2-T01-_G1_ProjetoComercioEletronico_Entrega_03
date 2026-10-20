package builder;

import product.RequisicaoLogin;

public class RequisicaoLoginConcretoBuilder implements RequisicaoLoginBuilder {
    private String email, senha, token, ip, fp, risco = "BAIXO", codigo2FA;

    public RequisicaoLoginBuilder resetar() { return this; }
    public RequisicaoLoginBuilder comCredenciais(String email, String senha) { this.email = email; this.senha = senha; return this; }
    public RequisicaoLoginBuilder comTokenCaptcha(String token) { this.token = token; return this; }
    public RequisicaoLoginBuilder comTelemetriaDispositivo(String ip, String fp, String risco) { this.ip = ip; this.fp = fp; this.risco = risco; return this; }
    public RequisicaoLoginBuilder comDesafio2FA(String canal, String codigo) { this.codigo2FA = codigo; return this; }
    public RequisicaoLoginBuilder comReplicacaoEcossistema(boolean ativar) { return this; }
    
    public RequisicaoLogin build() { 
        // Fail-Fast do Builder (Cenário 5)
        if ("ALTO".equals(risco) && (codigo2FA == null || codigo2FA.trim().isEmpty())) {
            throw new IllegalStateException("Sessão de RISCO ALTO exige código 2FA preenchido.");
        }
        return new RequisicaoLogin(email, senha, token, ip, fp, risco, codigo2FA); 
    }
}
