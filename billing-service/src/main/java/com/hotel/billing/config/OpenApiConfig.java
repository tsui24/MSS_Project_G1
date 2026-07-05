package com.hotel.billing.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI billingServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Folio & Billing Service API")
                        .description("Folio wallet, charge items and payment transaction history")
                        .version("v1.0"));
    }
}
