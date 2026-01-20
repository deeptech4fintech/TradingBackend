package com.trading.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Swagger/OpenAPI documentation
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI tradingPlatformAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Trading Platform API")
                        .description("A mini Spring Boot trading platform demonstrating core trading operations for B.Tech students. " +
                                "Features include user management, stock trading (buy/sell), portfolio tracking, " +
                                "transaction history, and live stock data integration with Finnhub API.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Trading Platform Team")
                                .email("support@tradingplatform.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
