package com.alejandro.microservices.notifications.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI notificationsOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Servidor de desarrollo");

        Server prodServer = new Server();
        prodServer.setUrl("https://api.notifications.com");
        prodServer.setDescription("Servidor de producción");

        Contact contact = new Contact();
        contact.setEmail("alejandro@biershoot.com");
        contact.setName("Alejandro");
        contact.setUrl("https://github.com/Biershoot");

        License license = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("API de Notificaciones en Tiempo Real")
                .version("1.0.0")
                .contact(contact)
                .description("API REST para gestión de notificaciones en tiempo real con Spring Boot y MySQL")
                .termsOfService("https://github.com/Biershoot/API_Notificaciones_Tiempo_Real")
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer, prodServer));
    }
}
