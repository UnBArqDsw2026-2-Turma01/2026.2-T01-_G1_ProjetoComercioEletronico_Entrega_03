package subsystem;

public class RegistroDispositivoConfiavel {
    public String vincularFingerprint(String email, String fingerprint, boolean lembrarDispositivo) {
        if (!lembrarDispositivo) {
            System.out.println("[Subsistema 3 - RegistroDispositivo] Dispositivo mantido como temporario: " + fingerprint);
            return "Temporario (Exigira revalidacao futura)";
        }
        System.out.println("[Subsistema 3 - RegistroDispositivo] Vinculando fingerprint confiavel para bypass futuro (Achado-L03): " + fingerprint);
        return "Vinculado como Confiavel (" + fingerprint + ")";
    }
}
