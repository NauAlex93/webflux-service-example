package ru.webfluxExample.ds.rest;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.webfluxExample.ds.config.SecurityConfig;
import ru.webfluxExample.ds.config.WebServerConfig;
import ru.webfluxExample.ds.config.routes.AutoValidationSelectTemplateRoute;
import ru.webfluxExample.ds.dto.request.SelectTemplateRequest;
import ru.webfluxExample.ds.exceptions.BasicApiResponseException;
import ru.webfluxExample.ds.service.PreValidationService;
import ru.webfluxExample.ds.util.FileUtils;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;

class TemplateSelectorHandlerTest {

    PreValidationService preValidationService = mock(PreValidationService.class);

    TemplateSelectorHandler templateSelectorHandler = new TemplateSelectorHandler(preValidationService);

    private final WebServerConfig config = new WebServerConfig();

    private final AutoValidationSelectTemplateRoute autoValidationSelectTemplateRoute = new AutoValidationSelectTemplateRoute();

    private final SecurityConfig securityConfig = new SecurityConfig(null, null);

    private final WebTestClient client = WebTestClient.bindToRouterFunction(
            autoValidationSelectTemplateRoute.autoValidationSelectTemplateRoutes(templateSelectorHandler)
    ).build();

    @Test
    void shouldSuccessfullySelectTemplate() {
        SelectTemplateRequest request = FileUtils.fromJson(
                FileUtils.readResource("json/api-validation-select-template.json"),
                SelectTemplateRequest.class
        );

        UUID selectedTemplateUUID = UUID.randomUUID();

        doReturn(Mono.just(selectedTemplateUUID))
                .when(preValidationService)
                .selectTemplate(any(SelectTemplateRequest.class));

        client.post()
                .uri(AutoValidationSelectTemplateRoute.PATH_SELECT_TEMPLATE)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(request), SelectTemplateRequest.class)
                .exchange()
                .expectStatus().isOk().expectBody(UUID.class).isEqualTo(selectedTemplateUUID);
    }

    @Test
    void shouldFailOnSelectTemplate() {
        SelectTemplateRequest request = FileUtils.fromJson(FileUtils.readResource("json/api-validation-select-template.json"), SelectTemplateRequest.class);

        doReturn(Mono.error(new BasicApiResponseException("Got exception while getting report", HttpStatus.BAD_GATEWAY)))
                .when(preValidationService)
                .selectTemplate(any());

        client.post()
                .uri(AutoValidationSelectTemplateRoute.PATH_SELECT_TEMPLATE)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(request), SelectTemplateRequest.class)
                .exchange()
                .expectStatus().is5xxServerError().expectBody(BasicApiResponseException.class);
    }

}