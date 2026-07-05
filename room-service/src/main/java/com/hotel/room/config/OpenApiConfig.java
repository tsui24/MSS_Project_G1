package com.hotel.room.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI roomServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Room & Service Catalog Service API")
                        .description("Room classes, physical rooms and the hotel service menu")
                        .version("v1.0"));
    }
}
