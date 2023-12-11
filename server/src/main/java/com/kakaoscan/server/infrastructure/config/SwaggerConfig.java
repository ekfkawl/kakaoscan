package com.kakaoscan.server.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@OpenAPIDefinition(
        info = @Info(
                title = "Kakaoscan API",
                version = "v1.0",
                description = "API for Kakaoscan service",
                contact = @Contact(name = "Support", email = "mail.kakaoscan@gmail.com"),
                license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html")
        )
)
@Configuration
@Profile("dev")
public class SwaggerConfig {
    private static final String ACCESS_TOKEN = "AccessToken";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(ACCESS_TOKEN))
                .components(new Components()
                        .addSecuritySchemes(ACCESS_TOKEN, new SecurityScheme()
                                .name(ACCESS_TOKEN)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("Bearer")
                                .bearerFormat("JWT")));
    }

    @Bean
    public OpenApiCustomizer addSecurityToOpenApi() {
        return openApi -> openApi
                .getPaths()
                .values()
                .forEach(pathItem -> pathItem.readOperations()
                        .forEach(operation -> operation.addSecurityItem(new SecurityRequirement().addList(ACCESS_TOKEN))));
    }

}
