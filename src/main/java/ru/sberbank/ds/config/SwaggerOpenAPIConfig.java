package ru.webfluxExample.ds.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerOpenAPIConfig {

    @Value("${springdoc.swagger-ui.oauth.url-namespace}")
    private String urlNamespace;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("oauthAuth"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(
                        new Components()
                                .addSecuritySchemes("oauthAuth",
                                        new SecurityScheme()
                                                .name("oauthAuth")
                                                .flows(
                                                        new OAuthFlows()
                                                                .password(
                                                                        new OAuthFlow()
                                                                                .tokenUrl(urlNamespace + "/auth-server/oauth/token")
                                                                )
                                                )
                                                .type(SecurityScheme.Type.OAUTH2)
                                )
                                .addSecuritySchemes("bearerAuth",
                                        new SecurityScheme()
                                                .name("bearerAuth")
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                );
    }

    @Bean
    public GroupedOpenApi projectOpenApi() {
        String[] paths = {"/api/project/**"};
        return GroupedOpenApi.builder().group("project").pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi AllOpenApi() {
        String[] paths = {"/**"};
        return GroupedOpenApi.builder()
                .group("all").pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi ReportOpenApi() {
        String[] paths = {"/api/report/**"};
        return GroupedOpenApi.builder().group("report").pathsToMatch(paths)
                .build();
    }
}
