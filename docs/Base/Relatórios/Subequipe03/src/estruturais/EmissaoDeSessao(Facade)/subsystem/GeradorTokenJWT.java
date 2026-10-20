package subsystem;

public class GeradorTokenJWT {
    public String emitirTokenAssinado(String email, String escopo) {
        String hashSimulado = Integer.toHexString((email + escopo).hashCode()).toUpperCase();
        String jwt = "eyJhbGciOiJSUzI1NiJ9." + hashSimulado + ".SESSAO_ASSINADA";
        System.out.println("[Subsistema 1 - GeradorTokenJWT] Emitindo token criptografico (Achado-L04): " + jwt);
        return jwt;
    }
}
