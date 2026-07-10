package com.sportsscore.match_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI matchServiceAPI() {
        return new OpenAPI().info(new Info().title("Sports Score Live - Match Service")
                .description("Rest API for scheduling sports matches and broadcasting live score updates.")
                .version("v1.0.0"));
    }

}
