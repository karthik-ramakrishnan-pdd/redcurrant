package com.pdd.redcurrant.application.springdoc;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger UI configuration for SpringDoc OpenAPI.
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Generic Application API",
                version = "1.0",
                description = "API documentation for Generic Application"
        )
)
@SecurityScheme(
        name = "BearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@Configuration
public class SwaggerUiConfiguration {

    @Value("${spring.application.name:Generic Application}")
    private String applicationName;

    @Value("${spring.application.description:API Documentation}")
    private String applicationDescription;

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("Public API")
                .packagesToScan("com.pdd")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new io.swagger.v3.oas.models.info.Info()
                                .title(applicationName)
                                .description(applicationDescription)
                                .version("1.0"))
                        .servers(getServers()))
                .build();
    }

    private List<Server> getServers() {
        return List.of(
                new Server().url("/").description("Local"),
                new Server().url("https://dev.redcurrant-platform.com").description("Developement"),
                new Server().url("https://stg.redcurrant-platform.com").description("Staging")
        );
    }

}
