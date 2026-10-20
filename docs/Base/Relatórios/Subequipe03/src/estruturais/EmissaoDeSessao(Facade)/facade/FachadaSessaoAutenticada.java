package facade;

import subsystem.GeradorTokenJWT;
import subsystem.RegistroDispositivoConfiavel;
import subsystem.ReplicadorSessaoSSO;
import subsystem.ServicoAuditoriaSeguranca;

public class FachadaSessaoAutenticada {

    private final GeradorTokenJWT geradorJwt;
    private final ReplicadorSessaoSSO replicadorSSO;
    private final RegistroDispositivoConfiavel registroDispositivo;
    private final ServicoAuditoriaSeguranca servicoAuditoria;

    public FachadaSessaoAutenticada() {
        this.geradorJwt = new GeradorTokenJWT();
        this.replicadorSSO = new ReplicadorSessaoSSO();
        this.registroDispositivo = new RegistroDispositivoConfiavel();
        this.servicoAuditoria = new ServicoAuditoriaSeguranca();
    }

    /**
     * Metodo unico da Fachada (GoF Facade): oculta a orquestracao de 4 subsistemas
     * complexos de pos-autenticacao (JWT, SSO, Dispositivo e Auditoria).
     */
    public ResumoSessaoDTO iniciarSessaoEcossistema(
            String email, String ipOrigem, String fingerprint, String canal,
            boolean replicarSSO, boolean lembrarDispositivo) {

        System.out.println("==================================================================");
        System.out.println(">>> [Facade] Iniciando provisionamento de sessao para: " + email);

        // 1. Emite o token JWT assinado (Achado-L04)
        String token = geradorJwt.emitirTokenAssinado(email, canal);

        // 2. Propaga a sessao via SSO para o ecossistema Mercado Pago (Achado-L05)
        String statusSSO = replicadorSSO.replicarParaEcossistema(email, token, replicarSSO);

        // 3. Registra o fingerprint do dispositivo para futuros logins silenciosos (Achado-L03)
        String statusDisp = registroDispositivo.vincularFingerprint(email, fingerprint, lembrarDispositivo);

        // 4. Registra trilha de auditoria antifraude
        String protocolo = servicoAuditoria.registrarEventoLogin(email, ipOrigem, canal);

        System.out.println(">>> [Facade] Sessao provisionada com sucesso em todos os subsistemas!");
        System.out.println("==================================================================\n");

        return new ResumoSessaoDTO(email, token, statusSSO, statusDisp, protocolo);
    }
}
