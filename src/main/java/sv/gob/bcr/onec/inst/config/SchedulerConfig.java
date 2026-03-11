package sv.gob.bcr.onec.inst.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 * Habilita el soporte de tareas programadas (@Scheduled) en la aplicación
 * y registra los beans de infraestructura necesarios para los schedulers.
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
