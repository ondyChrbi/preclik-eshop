package cz.preclik.shop.preclikshop.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Preclik E-Shop REST API documentation",
                description = "Official REST documentation",
                contact = @Contact(name = "Ondřej Chrbolka", email = "ondrej.chrbolka@gmail.com"),
                version = "0.0.1"),
        servers = @Server(url = "http://localhost:8080")
)
class OpenAPIConfiguration {
}