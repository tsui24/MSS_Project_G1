package com.hotel.review.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI reviewServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Review Service API")
                        .description("Post-stay guest reviews and staff replies")
                        .version("v1.0"));
    }
}
