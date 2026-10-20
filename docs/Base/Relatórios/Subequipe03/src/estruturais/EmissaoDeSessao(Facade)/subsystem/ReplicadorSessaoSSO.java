package subsystem;

public class ReplicadorSessaoSSO {
    public String replicarParaEcossistema(String email, String tokenJwt, boolean ativarSSO) {
        if (!ativarSSO) {
            System.out.println("[Subsistema 2 - ReplicadorSessaoSSO] Replicacao SSO desativada para esta sessao.");
            return "SSO Inativo (Sessao restrita ao dominio principal)";
        }
        System.out.println("[Subsistema 2 - ReplicadorSessaoSSO] POST /session/replicate -> HTTP 204 No Content (Achado-L05).");
        System.out.println("   -> Sessao propagada para Mercado Pago e Mercado Envios.");
        return "POST /session/replicate -> 204 (Mercado Pago Sincronizado)";
    }
}
