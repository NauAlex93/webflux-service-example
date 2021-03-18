package ru.webfluxExample.ds.rest;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.webfluxExample.ds.config.SecurityConfig;
import ru.webfluxExample.ds.config.WebServerConfig;
import ru.webfluxExample.ds.config.routes.AutoValidationProjectRoute;
import ru.webfluxExample.ds.dto.CreateProjectRequest;
import ru.webfluxExample.ds.dto.CreateReportRequest;
import ru.webfluxExample.ds.dto.ProjectRunStatus;
import ru.webfluxExample.ds.dto.ProjectShortInfoDto;
import ru.webfluxExample.ds.exceptions.BasicApiResponseException;
import ru.webfluxExample.ds.service.ProjectAutoValidationService;
import ru.webfluxExample.ds.util.FileUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.http.MediaType.TEXT_PLAIN;

class AutoValidationProjectHandlerTest {

    private static final String NOT_A_UUID = "not_a_uuid";

    private final ProjectAutoValidationService projectAutoValidationService = mock(ProjectAutoValidationService.class);

    private final AutoValidationHandler handler = new AutoValidationHandler(projectAutoValidationService);

    private final WebServerConfig config = new WebServerConfig();

    private final AutoValidationProjectRoute autoValidationProjectRoute = new AutoValidationProjectRoute();

    private final SecurityConfig securityConfig = new SecurityConfig(null, null);

    private final WebTestClient client = WebTestClient.bindToRouterFunction(
            autoValidationProjectRoute.autoValidationProjectRoutes(handler)
    ).build();

    @Test
    void shouldSuccessfullyCreateProject() {
        final UUID sourceProjectId = UUID.randomUUID();
        final ProjectShortInfoDto sourceProjectInfo = (ProjectShortInfoDto) new ProjectShortInfoDto()
                .setId(sourceProjectId)
                .setNodes(new ArrayList<>())
                .setName("Target project")
                .setDescription("Target description");

        doReturn(Mono.just(sourceProjectInfo))
                .when(projectAutoValidationService)
                .createProject(any(CreateProjectRequest.class));

        CreateProjectRequest createProjectRequest = new CreateProjectRequest()
                .setName("Target project")
                .setDescription("Target description")
                .setSourceProjectId(sourceProjectId);

        client.post()
                .uri(AutoValidationProjectRoute.PATH_CREATE_PROJECT)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(createProjectRequest), CreateProjectRequest.class)
                .exchange()
                .expectBody(ProjectShortInfoDto.class)
                .isEqualTo(sourceProjectInfo);
    }

    @Test
    void shouldFailOnCreatingProject() {
        doReturn(Mono.error(new BasicApiResponseException("Got exception while creating project", HttpStatus.BAD_GATEWAY)))
                .when(projectAutoValidationService)
                .createProject(any(CreateProjectRequest.class));

        UUID sourceProjectId = UUID.randomUUID();
        CreateProjectRequest createProjectRequest = new CreateProjectRequest()
                .setName("Target project")
                .setSourceProjectId(sourceProjectId);

        client.post()
                .uri(AutoValidationProjectRoute.PATH_CREATE_PROJECT)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(createProjectRequest), CreateProjectRequest.class)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void shouldSuccessfullyStartProject() {
        doReturn(Mono.empty())
                .when(projectAutoValidationService)
                .startProject(any(UUID.class));

        client.post()
                .uri(AutoValidationProjectRoute.PATH_START_PROJECT, UUID.randomUUID())
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

    @Test
    public void testStartProjectFailsWithBadUuid() {
        client.post()
                .uri(AutoValidationProjectRoute.PATH_START_PROJECT, NOT_A_UUID)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldFailOnStartingProject() {
        doReturn(Mono.error(new BasicApiResponseException("Got exception while starting project", HttpStatus.BAD_GATEWAY)))
                .when(projectAutoValidationService)
                .startProject(any(UUID.class));

        client.post()
                .uri(AutoValidationProjectRoute.PATH_START_PROJECT, UUID.randomUUID())
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody().isEmpty();
    }

    @Test
    void shouldStartProject() {
        UUID targetProjectId = UUID.randomUUID();

        doReturn(Mono.just(targetProjectId))
                .when(projectAutoValidationService)
                .startProject(any(UUID.class));

        client.post()
                .uri(AutoValidationProjectRoute.PATH_START_PROJECT, targetProjectId)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UUID.class).isEqualTo(targetProjectId);
    }


    @Test
    public void testStartNodeFailsWithBadProjectUuid() {
        client.post()
                .uri(AutoValidationProjectRoute.PATH_START_NODE, NOT_A_UUID, UUID.randomUUID())
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testStartNodeFailsWithBadNodeUuid() {
        client.post()
                .uri(AutoValidationProjectRoute.PATH_START_NODE, UUID.randomUUID(), NOT_A_UUID)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldFailOnStartingNode() {
        doReturn(Mono.error(new BasicApiResponseException("Got exception while starting node", HttpStatus.BAD_GATEWAY)))
                .when(projectAutoValidationService)
                .startNode(any(UUID.class), any(UUID.class));

        client.post()
                .uri(AutoValidationProjectRoute.PATH_START_NODE, UUID.randomUUID(), UUID.randomUUID())
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody().isEmpty();
    }

    @Test
    void shouldStartNode() {
        UUID targetNodeId = UUID.randomUUID();

        doReturn(Mono.just(targetNodeId))
                .when(projectAutoValidationService)
                .startNode(any(UUID.class), any(UUID.class));

        client.post()
                .uri(AutoValidationProjectRoute.PATH_START_NODE, UUID.randomUUID(), targetNodeId)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UUID.class).isEqualTo(targetNodeId);
    }

    @Test
    void shouldSuccessfullyCreateReport() {
        UUID sourceProjectId = UUID.randomUUID();
        doReturn(Mono.just(sourceProjectId))
                .when(projectAutoValidationService)
                .createReportWithTemplate(any(UUID.class));

        CreateReportRequest createReportRequest = new CreateReportRequest();
        createReportRequest.setRootProjectId(sourceProjectId);
        createReportRequest.setCreatedBy("service");

        client.post()
                .uri(AutoValidationProjectRoute.PATH_CREATE_REPORT_FROM_TEMPLATE, sourceProjectId)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(createReportRequest), CreateReportRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UUID.class).isEqualTo(sourceProjectId);
    }

    @Test
    public void testCreateReportFailsWithBadUuid() {
        client.post()
                .uri(AutoValidationProjectRoute.PATH_CREATE_REPORT_FROM_TEMPLATE, NOT_A_UUID)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldFailOnCreatingReport() {
        doReturn(Mono.error(new BasicApiResponseException("Got exception while creating report", HttpStatus.BAD_GATEWAY)))
                .when(projectAutoValidationService)
                .createReportWithTemplate(any(UUID.class));

        CreateReportRequest createReportRequest = new CreateReportRequest();
        createReportRequest.setRootProjectId(UUID.randomUUID());
        createReportRequest.setCreatedBy("service");

        client.post()
                .uri(AutoValidationProjectRoute.PATH_CREATE_REPORT_FROM_TEMPLATE, UUID.randomUUID())
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(createReportRequest), CreateReportRequest.class)
                .exchange()
                .expectStatus().is5xxServerError().expectBody(BasicApiResponseException.class);
    }

    @Test
    void shouldSuccessfullyGetStatus() {
        ProjectRunStatus projectRunStatus = FileUtils.fromJson(
                FileUtils.readResource("json/api-validation-project-status-response.json"), ProjectRunStatus.class);

        doReturn(Mono.just(projectRunStatus))
                .when(projectAutoValidationService)
                .getStatus(any(UUID.class));

        client.get()
                .uri(AutoValidationProjectRoute.PATH_GET_PROJECT_STATUS, UUID.randomUUID())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProjectRunStatus.class).isEqualTo(projectRunStatus);
    }

    @Test
    public void testGetStatusFailsWithBadUuid() {
        client.get()
                .uri(AutoValidationProjectRoute.PATH_GET_PROJECT_STATUS, NOT_A_UUID)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void shouldFailOnGettingStatus() {
        doReturn(Mono.error(new BasicApiResponseException("Got exception while getting status", HttpStatus.BAD_GATEWAY)))
                .when(projectAutoValidationService)
                .getStatus(any(UUID.class));

        client.get()
                .uri(AutoValidationProjectRoute.PATH_GET_PROJECT_STATUS, UUID.randomUUID())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(BasicApiResponseException.class);
    }

    @Test
    public void shouldSuccessfullyCreateAndGetReport() {
        String response = FileUtils.readResource("json/api-report-communicator-json-report.json");
        doReturn(Mono.just(response))
                .when(projectAutoValidationService)
                .createAndGetReport(any(UUID.class));

        client.post()
                .uri(AutoValidationProjectRoute.PATH_CREATE_AND_GET_REPORT, UUID.randomUUID())
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo(response);
    }

    @Test
    public void testCreateAndGetReportFailsWithBadUuid() {
        client.post()
                .uri(AutoValidationProjectRoute.PATH_CREATE_AND_GET_REPORT, NOT_A_UUID)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void shouldSuccessfullyCreateAndStartProject() {
        final UUID sourceProjectId = UUID.randomUUID();
        final ProjectShortInfoDto sourceProjectInfo = new ProjectShortInfoDto()
                .setId(sourceProjectId);

        doReturn(Mono.just(sourceProjectInfo))
                .when(projectAutoValidationService)
                .createAndStartProject(any(CreateProjectRequest.class));

        final CreateProjectRequest createProjectRequest = new CreateProjectRequest()
                .setName("Target project")
                .setSourceProjectId(sourceProjectId);

        client.post()
                .uri(AutoValidationProjectRoute.PATH_CREATE_AND_START_PROJECT)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(createProjectRequest), CreateProjectRequest.class)
                .exchange()
                .expectStatus().isOk().expectBody(ProjectShortInfoDto.class).isEqualTo(sourceProjectInfo);
    }

    @Test
    void shouldSuccessfullySaveSettings() {
        JsonNode jsonNode = FileUtils.fromJson(
                FileUtils.readResource("json/api-validation-save-node-response.json"), JsonNode.class);
        UUID projectId = UUID.randomUUID();
        UUID nodeId = UUID.randomUUID();
        doReturn(Mono.just(jsonNode))
                .when(projectAutoValidationService)
                .saveSettings(any(UUID.class), any(UUID.class), any(String.class));
        String testBody = "{}";
        client.put()
                .uri(AutoValidationProjectRoute.PATH_SAVE_SETTINGS_FOR_NODE, projectId, nodeId)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(testBody), String.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(JsonNode.class).isEqualTo(jsonNode);
    }

    @Test
    public void testSaveSettingsFailsWithBadProjectUuid() {
        client.put()
                .uri(AutoValidationProjectRoute.PATH_SAVE_SETTINGS_FOR_NODE, NOT_A_UUID, UUID.randomUUID())
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testSaveSettingsFailsWithBadNodeUuid() {
        client.put()
                .uri(AutoValidationProjectRoute.PATH_SAVE_SETTINGS_FOR_NODE, UUID.randomUUID(), NOT_A_UUID)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldFailOnSaveSettings_404() {
        JsonNode jsonNode = FileUtils.fromJson(
                FileUtils.readResource("json/api-validation-save-node-response.json"), JsonNode.class);
        UUID projectId = UUID.randomUUID();
        UUID nodeId = UUID.randomUUID();
        doReturn(Mono.empty())
                .when(projectAutoValidationService)
                .saveSettings(any(UUID.class), any(UUID.class), any(String.class));

        client.put()
                .uri(AutoValidationProjectRoute.PATH_SAVE_SETTINGS_FOR_NODE, projectId, nodeId)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldFailOnSaveSettings_400() {
        String projectId = UUID.randomUUID().toString();
        String nodeId = "wrong-uuid";
        client.put()
                .uri(AutoValidationProjectRoute.PATH_SAVE_SETTINGS_FOR_NODE, projectId, nodeId)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class).isEqualTo("Wrong format of uuid for variable= nodeId, value= wrong-uuid");
    }

    @Test
    public void shouldSuccessfullyUploadCsv() {
        String result = "{}";

        final UUID projectId = UUID.randomUUID();
        final UUID nodeId = UUID.randomUUID();

        doReturn(Mono.just(result))
                .when(projectAutoValidationService)
                .uploadCsv(
                        any(UUID.class),
                        any(UUID.class),
                        any(Map.class)
                );

        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("some.csv", "content", TEXT_PLAIN);

        client.post()
                .uri(AutoValidationProjectRoute.PATH_SAVE_CSV_FOR_DATASOURCE, projectId, nodeId)
                .contentType(MULTIPART_FORM_DATA)
                .bodyValue(bodyBuilder.build())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo(result);
    }

}