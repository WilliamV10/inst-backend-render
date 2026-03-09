package sv.gob.bcr.onec.inst.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sv.gob.bcr.onec.inst.config.properties.AppProperties;

import java.util.List;

/**
 * Configuración de OpenAPI (Swagger) para la herramienta de captura de datos.
 */
@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {

    private final AppProperties appProperties;

    @Bean
    public OpenAPI myOpenAPI() {
        var openapi = appProperties.getOpenapi();

        Server devServer = new Server();
        devServer.setUrl(openapi.getDevUrl());
        devServer.setDescription("URL del servidor en entorno de DESARROLLO");

        Server prodServer = new Server();
        prodServer.setUrl(openapi.getProdUrl());
        prodServer.setDescription("URL del servidor en entorno de PRODUCCIÓN");

        Contact contact = new Contact();
        contact.setEmail("info@bcr.gob.sv");
        contact.setName("Banco Central de Reserva de El Salvador");
        contact.setUrl("https://onec.bcr.gob.sv/");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://onec.bcr.gob.sv/mit/");

        Info info = new Info()
                .title("Herramienta de Captura de Datos - BCR")
                .version("1.0")
                .contact(contact)
                .description("API para la gestión de formularios y captura de datos del BCR.")
                .termsOfService("https://onec.bcr.gob.sv/terms")
                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer, prodServer));
    }
}
