package com.example.productservice.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestInterceptor;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.concurrent.TimeUnit.SECONDS;

@Configuration
public class FeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;  // Logs full details of requests/responses
    }

    @Bean
    public Retryer feignRetryer() {
        // Retryer with 3 attempts and a 1-second interval between retries
        return new Retryer.Default(1000, SECONDS.toMillis(3), 3);
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Service-Name", "PRODUCT-SERVICE");
        };
    }
}
