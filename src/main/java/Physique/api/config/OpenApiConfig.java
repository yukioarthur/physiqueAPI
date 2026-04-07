package Physique.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Physique API",
                version = "1.0.0",
                description = "API REST para gerenciamento de treinos, músculos, exercícios, usuários e resultados de treino.",
                contact = @Contact(name = "Jorge Vieira", email = "sem-email@exemplo.com"),
                license = @License(name = "Uso acadêmico")
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Servidor local")
        }
)
public class OpenApiConfig {
}
