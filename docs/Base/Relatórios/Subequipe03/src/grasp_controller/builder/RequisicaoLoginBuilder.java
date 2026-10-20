package builder;

import product.RequisicaoLogin;

public interface RequisicaoLoginBuilder {
    RequisicaoLoginBuilder resetar();
    RequisicaoLoginBuilder comCredenciais(String email, String senha);
    RequisicaoLoginBuilder comTokenCaptcha(String token);
    RequisicaoLoginBuilder comTelemetriaDispositivo(String ip, String fp, String risco);
    RequisicaoLoginBuilder comDesafio2FA(String canal, String codigo);
    RequisicaoLoginBuilder comReplicacaoEcossistema(boolean ativar);
    RequisicaoLogin build();
}
