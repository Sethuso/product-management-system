package com.example.pricingservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI pricingServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pricing Service API")
                        .description("API Documentation for Pricing Service")
                        .version("1.0"));
    }
}
