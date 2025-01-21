package vn.edu.uit.csbu.software_design.software_design_backend.config;

import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Backend Rest API Documentation",
        version = "1.0",
        description = "The documentation for the backend REST API of our software design project."
    ),
    servers = {
        @io.swagger.v3.oas.annotations.servers.Server(url = "http://localhost:8080", description = "Local server"),
        @io.swagger.v3.oas.annotations.servers.Server(url = "https://csbu-software-design-be.onrender.com", description = "Production server")

    }
)
public class SwaggerConfig {
}