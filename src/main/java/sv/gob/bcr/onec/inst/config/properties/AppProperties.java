package sv.gob.bcr.onec.inst.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Clase para centralizar toda la configuración personalizada de la aplicación.
 * Mapea las propiedades con el prefijo "app" definido en application.yaml.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Cors cors = new Cors();
    private final Openapi openapi = new Openapi();

    @Getter
    @Setter
    public static class Cors {

        /**
         * Lista de orígenes permitidos por CORS.
         */
        private List<String> allowedOrigins;

        /**
         * Lista de métodos HTTP permitidos (GET, POST, etc).
         */
        private List<String> allowedMethods;

        /**
         * Cabeceras permitidas en las peticiones.
         */
        private List<String> allowedHeaders = List.of("*");

        /**
         * Tiempo máximo (en segundos) que se cachea la verificación CORS.
         */
        private long maxAge = 3600;

        /**
         * Si se permiten credenciales (cookies, auth headers).
         */
        private boolean allowCredentials = true;
    }

    @Getter
    @Setter
    public static class Openapi {

        /**
         * URL del servidor para el entorno de desarrollo.
         */
        private String devUrl;

        /**
         * URL del servidor para el entorno de producción.
         */
        private String prodUrl;
    }

}
