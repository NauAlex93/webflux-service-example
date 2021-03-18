package ru.webfluxExample.ds.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.webfluxExample.ds.dto.CreateProjectRequest;
import ru.webfluxExample.ds.dto.CreateReportRequest;
import ru.webfluxExample.ds.dto.NodeDto;
import ru.webfluxExample.ds.dto.ProjectExecutionStatus;
import ru.webfluxExample.ds.dto.ProjectRunStatus;
import ru.webfluxExample.ds.dto.ProjectShortInfoDto;
import ru.webfluxExample.ds.exceptions.BasicApiResponseException;
import ru.webfluxExample.ds.project.manager.api.dto.project.ProjectNodeInfo;
import ru.webfluxExample.ds.project.manager.api.dto.project.ProjectNodeStatus;
import ru.webfluxExample.ds.project.manager.api.dto.project.SaveAsProjectRequest;
import ru.webfluxExample.ds.util.FileUtils;
import ru.webfluxExample.ds.util.MockFilePart;
import ru.webfluxExample.ds.util.NodeMapper;
import ru.webfluxExample.ds.util.ProjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

class ProjectAutoValidationServiceTest {

    private final AccessTokenProvider accessTokenProvider = Mockito.mock(AccessTokenProvider.class);
    private final WebService webService = Mockito.mock(WebService.class);
    private final ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);
    private final NodeMapper nodeMapper = Mappers.getMapper(NodeMapper.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ProjectAutoValidationService projectAutoValidationService = new ProjectAutoValidationService(
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            "service",
            accessTokenProvider,
            webService,
            projectMapper,
            nodeMapper
    );

    @BeforeEach
    void beforeEach() {
        DefaultOAuth2AccessToken oAuth2AccessToken = new DefaultOAuth2AccessToken("accessToken");

        doReturn(Mono.just(oAuth2AccessToken))
                .when(accessTokenProvider)
                .getAccessToken();
    }

    @Test
    public void shouldSuccessfullyCreateProject() {
        final ProjectShortInfoDto sourceProjectInfo = new ProjectShortInfoDto()
                .setId(UUID.randomUUID())
                .setNodes(new ArrayList<>())
                .setName("Target project")
                .setDescription("Target description");

        doReturn(Mono.just(sourceProjectInfo))
                .when(webService)
                .saveProjectFromTemplateRequest(any(SaveAsProjectRequest.class), any(UUID.class), any(String.class));

        final CreateProjectRequest createProjectRequest = buildProjectRequest(
                sourceProjectInfo.getId(),
                sourceProjectInfo.getName(),
                sourceProjectInfo.getDescription()
        );
        final Mono<ProjectShortInfoDto> projectInfo = projectAutoValidationService.createProject(createProjectRequest);

        StepVerifier
                .create(projectInfo)
                .expectNext(sourceProjectInfo)
                .expectComplete().verify();
    }

    @Test
    public void shouldFailOnCreatingProject() {
        doReturn(Mono.error(new BasicApiResponseException("Got exception while creating project", HttpStatus.BAD_GATEWAY)))
                .when(webService)
                .saveProjectFromTemplateRequest(any(SaveAsProjectRequest.class), any(UUID.class), any(String.class));

        CreateProjectRequest createProjectRequest = buildProjectRequest(UUID.randomUUID());

        Mono<UUID> projectId = projectAutoValidationService.createProject(createProjectRequest).flatMap(projectInfo -> Mono.just(projectInfo.getId()));

        StepVerifier
                .create(projectId)
                .expectErrorMatches(throwable -> throwable instanceof BasicApiResponseException &&
                        ((BasicApiResponseException) throwable).getResponseBody().equals("Got exception while creating project") &&
                        ((BasicApiResponseException) throwable).getHttpStatus().is5xxServerError()
                ).verify();
    }

    @Test
    public void shouldSuccessfullyStartProject() {
        UUID targetProjectId = UUID.randomUUID();

        doReturn(Mono.just(targetProjectId))
                .when(webService)
                .startProject(any(UUID.class), any(String.class));

        Mono<UUID> startProject = projectAutoValidationService.startProject(targetProjectId);

        StepVerifier
                .create(startProject)
                .expectNext(targetProjectId)
                .expectComplete().verify();
    }

    @Test
    public void shouldFailOnStartingProject() {
        doReturn(Mono.empty())
                .when(webService)
                .startProject(any(UUID.class), any(String.class));

        Mono<UUID> projectId = projectAutoValidationService.startProject(UUID.randomUUID());

        Flux<UUID> error = projectId.concatWith(
                Mono.error(new BasicApiResponseException("Got exception while starting project", HttpStatus.BAD_GATEWAY))
        );

        StepVerifier
                .create(error)
                .expectErrorMatches(throwable -> throwable instanceof BasicApiResponseException &&
                        ((BasicApiResponseException) throwable).getResponseBody().equals("Got exception while starting project") &&
                        ((BasicApiResponseException) throwable).getHttpStatus().is5xxServerError()
                ).verify();
    }

    @Test
    public void shouldSuccessfullyStartNode() {
        UUID targetNodeId = UUID.randomUUID();

        doReturn(Mono.just(targetNodeId))
                .when(webService)
                .startNode(any(UUID.class), any(UUID.class), any(String.class));

        Mono<UUID> startNode = projectAutoValidationService.startNode(UUID.randomUUID(), targetNodeId);

        StepVerifier
                .create(startNode)
                .expectNext(targetNodeId)
                .expectComplete().verify();
    }

    @Test
    public void shouldFailOnStartingNode() {
        doReturn(Mono.empty())
                .when(webService)
                .startNode(any(UUID.class), any(UUID.class), any(String.class));

        Mono<UUID> nodeId = projectAutoValidationService.startNode(UUID.randomUUID(), UUID.randomUUID());

        Flux<UUID> error = nodeId.concatWith(
                Mono.error(new BasicApiResponseException("Got exception while starting node", HttpStatus.BAD_GATEWAY))
        );

        StepVerifier
                .create(error)
                .expectErrorMatches(throwable -> throwable instanceof BasicApiResponseException &&
                        ((BasicApiResponseException) throwable).getResponseBody().equals("Got exception while starting node") &&
                        ((BasicApiResponseException) throwable).getHttpStatus().is5xxServerError()
                ).verify();
    }

    @Test
    public void shouldSuccessfullyCreateReportWithTemplate() {
        UUID targetProjectId = UUID.randomUUID();
        createReportWithTemplateResponse(targetProjectId);

        Mono<UUID> reportId = projectAutoValidationService.createReportWithTemplate(targetProjectId);

        StepVerifier
                .create(reportId)
                .expectNext(targetProjectId)
                .expectComplete().verify();
    }

    @Test
    public void shouldFailOnCreateReportWithTemplate() {
        doReturn(Mono.error(new BasicApiResponseException("Got exception while creating report", HttpStatus.BAD_GATEWAY)))
                .when(webService)
                .createReportWithTemplate(any(CreateReportRequest.class), any(String.class));

        UUID targetProjectId = UUID.randomUUID();

        Mono<UUID> reportId = projectAutoValidationService.createReportWithTemplate(targetProjectId);

        StepVerifier
                .create(reportId)
                .expectErrorMatches(throwable -> throwable instanceof BasicApiResponseException &&
                        ((BasicApiResponseException) throwable).getResponseBody().equals("Got exception while creating report") &&
                        ((BasicApiResponseException) throwable).getHttpStatus().is5xxServerError()
                ).verify();
    }

    @Test
    public void shouldReceiveInProgressStatus() throws JsonProcessingException {
        final List<ProjectNodeInfo> projectNodeInfoList =
                objectMapper.readValue(FileUtils.readResource("json/api-project-manager-nodes-not-ready-response.json"), new TypeReference<List<ProjectNodeInfo>>() {
                });

        doReturn(Mono.just(projectNodeInfoList))
                .when(webService)
                .getProjectNodesInfo(any(UUID.class), any(String.class));

        UUID sourceProjectId = UUID.randomUUID();

        Mono<ProjectRunStatus> reportId = projectAutoValidationService.getStatus(sourceProjectId);

        ProjectRunStatus projectRunStatus = new ProjectRunStatus();
        projectRunStatus.setProjectId(sourceProjectId);
        projectRunStatus.setProjectExecutionStatus(ProjectExecutionStatus.IN_PROGRESS);

        List<NodeDto> nodes = createNodes(ProjectNodeStatus.DONE, ProjectNodeStatus.DONE, ProjectNodeStatus.NOT_READY);
        projectRunStatus.setNodes(nodes);

        StepVerifier
                .create(reportId)
                .expectNext(projectRunStatus)
                .expectComplete().verify();
    }

    @Test
    public void shouldReceiveFailedStatus() throws JsonProcessingException {
        final List<ProjectNodeInfo> projectNodeInfoList =
                objectMapper.readValue(FileUtils.readResource("json/api-project-manager-nodes-failed-response.json"), new TypeReference<List<ProjectNodeInfo>>() {
                });

        doReturn(Mono.just(projectNodeInfoList))
                .when(webService)
                .getProjectNodesInfo(any(UUID.class), any(String.class));

        UUID sourceProjectId = UUID.randomUUID();

        Mono<ProjectRunStatus> reportId = projectAutoValidationService.getStatus(sourceProjectId);

        ProjectRunStatus projectRunStatus = new ProjectRunStatus();
        projectRunStatus.setProjectId(sourceProjectId);
        projectRunStatus.setProjectExecutionStatus(ProjectExecutionStatus.FAILED);

        List<NodeDto> nodes = createNodes(ProjectNodeStatus.DONE, ProjectNodeStatus.FAILED, ProjectNodeStatus.FAILED);
        projectRunStatus.setNodes(nodes);

        StepVerifier
                .create(reportId)
                .expectNext(projectRunStatus)
                .expectComplete().verify();
    }

    @Test
    public void shouldSuccessfullyGetReport() {
        String report = getReportResponse();

        Mono<String> reportId = projectAutoValidationService.getReport(UUID.randomUUID());

        StepVerifier
                .create(reportId)
                .expectNext(report)
                .expectComplete().verify();
    }

    @Test
    public void shouldFailOnGettingReport() {
        doReturn(Mono.error(new BasicApiResponseException("Got exception while getting report", HttpStatus.BAD_GATEWAY)))
                .when(webService)
                .getReport(any(UUID.class), any(String.class));

        Mono<String> reportId = projectAutoValidationService.getReport(UUID.randomUUID());

        StepVerifier
                .create(reportId)
                .expectErrorMatches(throwable -> throwable instanceof BasicApiResponseException &&
                        ((BasicApiResponseException) throwable).getResponseBody().equals("Got exception while getting report") &&
                        ((BasicApiResponseException) throwable).getHttpStatus().is5xxServerError()
                ).verify();
    }

    @Test
    public void shouldSuccessfullyCreateAndStartProject() {
        final UUID sourceProjectId = UUID.randomUUID();
        final ProjectShortInfoDto sourceProjectInfo = new ProjectShortInfoDto()
                .setId(sourceProjectId)
                .setNodes(new ArrayList<>())
                .setName("Target project")
                .setDescription("Target description");

        doReturn(Mono.just(sourceProjectInfo))
                .when(webService)
                .saveProjectFromTemplateRequest(any(SaveAsProjectRequest.class), any(UUID.class), any(String.class));

        doReturn(Mono.just(sourceProjectId))
                .when(webService)
                .startProject(any(UUID.class), any(String.class));

        final CreateProjectRequest createProjectRequest = buildProjectRequest(
                sourceProjectInfo.getId(),
                sourceProjectInfo.getName(),
                sourceProjectInfo.getDescription()
        );
        final Mono<ProjectShortInfoDto> projectInfo = projectAutoValidationService.createAndStartProject(createProjectRequest);

        StepVerifier
                .create(projectInfo)
                .expectNext(sourceProjectInfo)
                .expectComplete().verify();
    }

    @Test
    public void shouldSuccessfullyCreateAndGetReport() {
        createReportWithTemplateResponse(UUID.randomUUID());
        String report = getReportResponse();
        Mono<String> reportId = projectAutoValidationService.createAndGetReport(UUID.randomUUID());

        StepVerifier
                .create(reportId)
                .expectNext(report)
                .expectComplete().verify();
    }

    @Test
    public void shouldReceiveSaveSettingsStatus() {
        JsonNode jsonNode = FileUtils.createJsonNode("json/api-validation-save-node-response.json");

        doReturn(Mono.just(jsonNode))
                .when(webService)
                .saveSettingsRequest(any(String.class), any(UUID.class), any(UUID.class), any(String.class));

        UUID projectId = UUID.randomUUID();
        UUID nodeId = UUID.randomUUID();
        String settingsJSON = "{}";

        Mono<JsonNode> reportId = projectAutoValidationService.saveSettings(projectId, nodeId, settingsJSON);

        StepVerifier
                .create(reportId)
                .expectNext(jsonNode)
                .expectComplete().verify();
    }

    @Test
    public void shouldReceiveUploadCsvStatus() {
        final String response = "/data/core/shsdh/app/tmp/ci02132621-edevlg-sdsdevelop5/b5b763b7-5729-4b93-aea7-e28a0bd94f201.csv";

        doReturn(Mono.just(response))
                .when(webService)
                .uploadCsvRequest(
                        any(String.class),
                        any(UUID.class),
                        any(UUID.class),
                        any(Integer.class),
                        any(Integer.class),
                        any(Integer.class),
                        any(String.class),
                        any(String.class),
                        any(byte[].class)
                );

        final UUID projectId = UUID.randomUUID();
        final UUID nodeId = UUID.randomUUID();
        final String filename = "some.csv";

        final FilePart csvFile = new MockFilePart();
        final Map<String, Part> files = new HashMap();
        files.put(filename, csvFile);

        Mono<String> uploadCsvResponse = projectAutoValidationService.uploadCsv(
                projectId,
                nodeId,
                files
        );

        StepVerifier
                .create(uploadCsvResponse)
                .expectNext(response)
                .expectComplete()
                .verify();
    }

    @Test
    public void shouldFailIfNotOnlyOneFile() {
        final UUID projectId = UUID.randomUUID();
        final UUID nodeId = UUID.randomUUID();
        final String filename1 = "some1.csv";
        final String filename2 = "some2.csv";

        final FilePart csvFile = new MockFilePart();
        final Map<String, Part> files = new HashMap();
        files.put(filename1, csvFile);
        files.put(filename2, csvFile);

        Mono<String> uploadCsvResponse = projectAutoValidationService.uploadCsv(
                projectId,
                nodeId,
                files
        );

        StepVerifier
                .create(uploadCsvResponse)
                .expectErrorMatches(throwable ->
                        throwable instanceof BasicApiResponseException
                                && ((BasicApiResponseException) throwable).getResponseBody().equals("Expected only 1 .csv file")
                                && ((BasicApiResponseException) throwable).getHttpStatus().equals(HttpStatus.BAD_REQUEST)
                )
                .verify();
    }

    private void createReportWithTemplateResponse(UUID sourceProjectId) {
        doReturn(Mono.just(sourceProjectId))
                .when(webService)
                .createReportWithTemplate(any(CreateReportRequest.class), any(String.class));
    }

    private String getReportResponse() {
        String response = FileUtils.readResource("json/api-report-communicator-json-report.json");

        doReturn(Mono.just(response))
                .when(webService)
                .getReport(any(UUID.class), any(String.class));

        return response;
    }

    private CreateProjectRequest buildProjectRequest(UUID sourceProjectId) {
        CreateProjectRequest createProjectRequest = new CreateProjectRequest();
        createProjectRequest.setSourceProjectId(sourceProjectId);
        createProjectRequest.setUserGroupId(UUID.randomUUID());
        return createProjectRequest;
    }

    private CreateProjectRequest buildProjectRequest(UUID sourceProjectId, String name, String description) {
        CreateProjectRequest createProjectRequest = buildProjectRequest(sourceProjectId);
        createProjectRequest.setName(name);
        createProjectRequest.setDescription(description);
        return createProjectRequest;
    }

    private List<NodeDto> createNodes(ProjectNodeStatus firstNodeStatus, ProjectNodeStatus secondNodeStatus, ProjectNodeStatus thirdNodeStatus) {
        List<NodeDto> nodes = new ArrayList<>();
        NodeDto node1 = new NodeDto();
        node1.setNodeId(UUID.fromString("df594df6-85d2-45b4-99ee-a6aee7eec174"));
        node1.setProjectNodeStatus(firstNodeStatus);
        NodeDto node2 = new NodeDto();
        node2.setNodeId(UUID.fromString("5068fa5f-06a6-4e35-86a7-df90f2571d8b"));
        node2.setProjectNodeStatus(secondNodeStatus);
        NodeDto node3 = new NodeDto();
        node3.setNodeId(UUID.fromString("d38d628d-cf41-4f81-a2de-31bded2b10ff"));
        node3.setProjectNodeStatus(thirdNodeStatus);
        nodes.add(node1);
        nodes.add(node2);
        nodes.add(node3);

        return nodes;
    }
}