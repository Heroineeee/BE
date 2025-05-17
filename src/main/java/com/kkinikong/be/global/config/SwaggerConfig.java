package com.kkinikong.be.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@OpenAPIDefinition(
    servers = {
      @Server(url = "https://kkinikong.store", description = "Production Server"),
      @Server(url = "http://localhost:8080", description = "Local Server")
    })
@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    // Security Scheme 정의
    SecurityScheme securityScheme =
        new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .in(SecurityScheme.In.HEADER)
            .name("Authorization");

    // Security Requirement 정의
    SecurityRequirement securityRequirement = new SecurityRequirement().addList("BearerAuth");

    return new OpenAPI()
        .components(new Components())
        .info(
            new Info()
                .title("끼니콩 REST API")
                .description("Heroine Backend Team")
                .contact(
                    new Contact().name("Heroine BE Github").url("https://github.com/Heroineeee/BE"))
                .version("1.0.0"))
        .addSecurityItem(securityRequirement)
        .schemaRequirement("BearerAuth", securityScheme);
  }
}
