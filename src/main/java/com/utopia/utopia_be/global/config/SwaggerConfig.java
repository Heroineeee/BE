package com.utopia.utopia_be.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("UTopia REST API")
                        .description("Heroine Backend Team")
                        .contact(new Contact()
                                .name("Heroine BE Github")
                                .url("https://github.com/Heroineeee/BE"))
                        .version("1.0.0"));
    }
}
