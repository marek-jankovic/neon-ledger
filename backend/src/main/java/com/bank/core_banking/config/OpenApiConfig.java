package com.bank.core_banking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Neon Ledger Central Node API")
                        .version("v1.0.4")
                        .description("High-availability financial protocol for District 01 banking operations. Secure ledger synchronization and credit transfer modules.")
                        .license(new License().name("Proprietary - District 01 Banking Authority")));
    }
}