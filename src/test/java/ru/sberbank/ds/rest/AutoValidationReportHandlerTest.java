package ru.webfluxExample.ds.rest;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.webfluxExample.ds.config.SecurityConfig;
import ru.webfluxExample.ds.config.WebServerConfig;
import ru.webfluxExample.ds.config.routes.AutoValidationReportRoute;
import ru.webfluxExample.ds.exceptions.BasicApiResponseException;
import ru.webfluxExample.ds.service.ProjectAutoValidationService;
import ru.webfluxExample.ds.util.FileUtils;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;

class AutoValidationReportHandlerTest {

    private static final String NOT_A_UUID = "not_a_uuid";

    private final ProjectAutoValidationService projectAutoValidationService = mock(ProjectAutoValidationService.class);

    private final AutoValidationHandler handler = new AutoValidationHandler(projectAutoValidationService);

    private final WebServerConfig config = new WebServerConfig();

    private final AutoValidationReportRoute autoValidationReportRoute = new AutoValidationReportRoute();

    private final SecurityConfig securityConfig = new SecurityConfig(null, null);

    private final WebTestClient client = WebTestClient.bindToRouterFunction(
            autoValidationReportRoute.autoValidationReportRoutes(handler)
    ).build();

    @Test
    void shouldSuccessfullyGetReport() {
        String response = FileUtils.readResource("json/api-report-communicator-json-report.json");

        doReturn(Mono.just(response))
                .when(projectAutoValidationService)
                .getReport(any(UUID.class));

        client.get()
                .uri(AutoValidationReportRoute.PATH_GET_REPORT, UUID.randomUUID())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo(response);
    }

    @Test
    public void testGetReportFailsWithBadUuid() {
        client.get()
                .uri(AutoValidationReportRoute.PATH_GET_REPORT, NOT_A_UUID)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }


    @Test
    void shouldFailOnGettingReport() {
        doReturn(Mono.error(new BasicApiResponseException("Got exception while getting report", HttpStatus.BAD_GATEWAY)))
                .when(projectAutoValidationService)
                .getReport(any(UUID.class));

        client.get()
                .uri(AutoValidationReportRoute.PATH_GET_REPORT, UUID.randomUUID())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError().expectBody(BasicApiResponseException.class);
    }

}