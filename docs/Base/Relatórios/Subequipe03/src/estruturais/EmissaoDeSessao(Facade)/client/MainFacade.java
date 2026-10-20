package client;

import facade.FachadaSessaoAutenticada;
import facade.ResumoSessaoDTO;

public class MainFacade {
    public static void main(String[] args) {
        // O Cliente conhece APENAS a Fachada, desconhecendo os 4 subsistemas internos
        FachadaSessaoAutenticada fachada = new FachadaSessaoAutenticada();

        System.out.println("### CENARIO 1: Login Web Completo (SSO Mercado Pago + Dispositivo Confiavel) ###");
        ResumoSessaoDTO sessaoWeb = fachada.iniciarSessaoEcossistema(
                "pedro@unb.br", "192.168.1.10", "CHROME_LINUX_NIXOS", "WEB_DESKTOP", true, true);
        imprimirResumo(sessaoWeb);

        System.out.println("### CENARIO 2: Login Mobile Restrito (Sem Replicacao SSO e Dispositivo Temporario) ###");
        ResumoSessaoDTO sessaoMobile = fachada.iniciarSessaoEcossistema(
                "julia@unb.br", "177.45.22.11", "ANDROID_14_APP", "MOBILE_APP", false, false);
        imprimirResumo(sessaoMobile);
    }

    private static void imprimirResumo(ResumoSessaoDTO dto) {
        System.out.println("[Cliente recebeu ResumoSessaoDTO da Fachada]");
        System.out.println("  * Usuario            : " + dto.getEmail());
        System.out.println("  * Token JWT (L04)    : " + dto.getTokenJwt());
        System.out.println("  * Replicacao (L05)   : " + dto.getStatusReplicacaoSSO());
        System.out.println("  * Dispositivo (L03)  : " + dto.getStatusDispositivo());
        System.out.println("  * Protocolo Auditoria: " + dto.getProtocoloAuditoria() + "\n");
    }
}
