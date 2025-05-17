package com.trueque_api.staff.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Trueque - API de Trocas",
        version = "1.0.0",
        description = "API para gerenciar trocas de produtos entre usuários.",
        contact = @Contact(
            name = "Weliton",
            email = "",
            url = "https://github.com/welitonlimaa"
        ),
        license = @License(
            name = "MIT",
            url = "https://opensource.org/licenses/MIT"
        )
    )
)
public class OpenApiConfig {
}