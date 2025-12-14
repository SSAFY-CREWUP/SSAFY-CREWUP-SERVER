package com.ssafy.crewup.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Value("${app.server.url}")
    private String serverUrl;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CrewUp API")
                        .description("CrewUp 프로젝트 API 명세서")
                        .version("1.0.0"))
                .servers(List.of(
                        new Server().url(serverUrl).description("CrewUp Server API")
                ));
    }
}