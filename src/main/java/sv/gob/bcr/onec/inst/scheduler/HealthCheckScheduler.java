package sv.gob.bcr.onec.inst.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Cron job que realiza un ping al endpoint /actuator/health para mantener
 * la instancia activa en servicios como Render.
 *
 * <p>Horario: lunes a viernes de 8:00 AM a 6:00 PM (hora El Salvador, UTC-6),
 * cada 14 minutos.</p>
 *
 * <p>Expresión cron: {@code 0 0/14 8-17 * * MON-FRI}
 * <ul>
 *   <li>Ejecuciones en cada hora: :00, :14, :28, :42, :56</li>
 *   <li>Última ejecución del día: 17:56 (dentro de la ventana 8 AM–6 PM)</li>
 * </ul>
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HealthCheckScheduler {

    private final RestTemplate restTemplate;

    /**
     * URL base del servidor (ej: http://localhost:8080).
     * Se construye a partir del puerto y el context-path configurados.
     */
    @Value("${app.health-check.url:http://localhost:${server.port:8080}${server.servlet.context-path:/}actuator/health}")
    private String healthCheckUrl;

    /**
     * Ping al endpoint de salud cada 14 minutos,
     * de lunes a viernes entre las 8:00 AM y las 6:00 PM (hora El Salvador).
     *
     * <p>Zona horaria: {@code America/El_Salvador} (UTC-6, sin horario de verano).</p>
     */
    @Scheduled(cron = "0 0/14 8-18 * * MON-FRI", zone = "America/El_Salvador")
    public void pingHealthEndpoint() {
        log.debug("HealthCheckScheduler: iniciando ping a {}", healthCheckUrl);
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(healthCheckUrl, String.class);
            log.info("HealthCheckScheduler: respuesta del actuator - HTTP {}", response.getStatusCode());
        } catch (RestClientException ex) {
            log.warn("HealthCheckScheduler: error al contactar el endpoint de salud - {}", ex.getMessage());
        }
    }
}
