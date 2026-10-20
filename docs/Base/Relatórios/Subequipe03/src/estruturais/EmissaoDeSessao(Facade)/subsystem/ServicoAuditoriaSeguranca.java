package subsystem;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServicoAuditoriaSeguranca {
    public String registrarEventoLogin(String email, String ipOrigem, String canal) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String protocolo = "AUD-" + Math.abs((email + ipOrigem).hashCode() % 90000 + 10000);
        System.out.println("[Subsistema 4 - AuditoriaSeguranca] Log gravado [" + protocolo + "] em " + timestamp + " | IP: " + ipOrigem + " | Canal: " + canal);
        return protocolo + " (" + timestamp + ")";
    }
}
