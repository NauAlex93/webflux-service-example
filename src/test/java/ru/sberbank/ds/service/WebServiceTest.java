package ru.webfluxExample.ds.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.shared.Application;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.webfluxExample.ds.client.SdsDataSourceClient;
import ru.webfluxExample.ds.client.SdsJobRunnerClient;
import ru.webfluxExample.ds.client.SdsProjectManagerClient;
import ru.webfluxExample.ds.client.SdsReportCommunicatorClient;
import ru.webfluxExample.ds.dto.CreateProjectRequest;
import ru.webfluxExample.ds.dto.CreateReportRequest;
import ru.webfluxExample.ds.dto.ProjectShortInfoDto;
import ru.webfluxExample.ds.exceptions.BasicApiResponseException;
import ru.webfluxExample.ds.project.manager.api.dto.project.ProjectNodeInfo;
import ru.webfluxExample.ds.project.manager.api.dto.project.SaveAsProjectRequest;
import ru.webfluxExample.ds.props.sds.SdsDataSourceProperties;
import ru.webfluxExample.ds.props.sds.SdsJobRunnerProperties;
import ru.webfluxExample.ds.props.sds.SdsProjectManagerProperties;
import ru.webfluxExample.ds.props.sds.SdsReportCommunicatorProperties;
import ru.webfluxExample.ds.util.FileUtils;
import ru.webfluxExample.ds.util.NodeMapper;
import ru.webfluxExample.ds.util.ProjectMapper;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.UUID.fromString;

class WebServiceTest extends AbstractWebClientTest {

    private static final String ACCESS_TOKEN = "accessToken";

    private static final String SOURCE_PROJECT_ID = "00000000-0000-0000-0000-000000000001";
    private static final String USER_GROUP_ID = "00000000-0000-0000-0000-000000000002";
    private static final String SOURCE_NODE_ID = "00000000-0000-0000-0000-000000000001";

    private final ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);
    private final NodeMapper nodeMapper = Mappers.getMapper(NodeMapper.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final SdsProjectManagerProperties sdsProjectManagerProperties =
            new SdsProjectManagerProperties(
                    "project-manager", false, "internal/project",
                    "internal/nodes/project",
                    "internal/events/projcet"
            );

    private final SdsProjectManagerClient sdsProjectManagerClient =
            new SdsProjectManagerClient(discoveryClient, sdsProjectManagerProperties);

    private final SdsJobRunnerProperties sdsJobRunnerProperties =
            new SdsJobRunnerProperties(
                    "job-runner", false, "internal/submit"
            );

    private final SdsJobRunnerClient sdsJobRunnerClient =
            new SdsJobRunnerClient(discoveryClient, sdsJobRunnerProperties);

    private final SdsReportCommunicatorProperties sdsReportCommunicatorProperties =
            new SdsReportCommunicatorProperties(
                    "report-communicator", false,
                    "reports", "reports/default"
            );

    private final SdsReportCommunicatorClient sdsReportCommunicatorClient =
            new SdsReportCommunicatorClient(discoveryClient, sdsReportCommunicatorProperties);

    private final SdsDataSourceProperties sdsDataSourceProperties =
            new SdsDataSourceProperties(
                    "data-source", false, "api"
            );

    private final SdsDataSourceClient sdsDataSourceClient =
            new SdsDataSourceClient(
                    discoveryClient, sdsDataSourceProperties
            );

    private final WebService webService = new WebServiceImpl(
            webClient, projectMapper, nodeMapper,
            sdsProjectManagerClient, sdsJobRunnerClient, sdsReportCommunicatorClient,
            sdsDataSourceClient
    );

    @BeforeEach
    public void beforeEach() {
        Application pmApp = new Application(sdsProjectManagerProperties.getService(),
                Collections.singletonList(instanceInfo));
        Mockito.when(discoveryClient.getApplication(sdsProjectManagerProperties.getService()))
                .thenReturn(pmApp);
        Application jrApp = new Application(sdsJobRunnerProperties.getService(),
                Collections.singletonList(instanceInfo));
        Mockito.when(discoveryClient.getApplication(sdsJobRunnerProperties.getService()))
                .thenReturn(jrApp);
        Application rcApp = new Application(sdsReportCommunicatorProperties.getService(),
                Collections.singletonList(instanceInfo));
        Mockito.when(discoveryClient.getApplication(sdsReportCommunicatorProperties.getService()))
                .thenReturn(rcApp);
        Application dsApp = new Application(sdsDataSourceProperties.getService(),
                Collections.singletonList(instanceInfo));
        Mockito.when(discoveryClient.getApplication(sdsDataSourceProperties.getService()))
                .thenReturn(dsApp);
    }


    @Test
    public void shouldSuccessfullySaveProjectFromTemplate() {
        enqueue("json/api-save-project-response-without-nodes.json");

        CreateProjectRequest createProjectRequest = new CreateProjectRequest()
                .setName("Target project")
                .setSourceProjectId(UUID.fromString("d87640f1-35b5-4af2-8667-ce515236862e"));

        final SaveAsProjectRequest projectRequest = new SaveAsProjectRequest();
        projectRequest.setName(createProjectRequest.getName());
        projectRequest.setSourceId(createProjectRequest.getSourceProjectId());

        Mono<UUID> result = webService
                .saveProjectFromTemplateRequest(projectRequest, fromString(USER_GROUP_ID), ACCESS_TOKEN)
                .map(ProjectShortInfoDto::getId);

        StepVerifier
                .create(result)
                .expectNext(createProjectRequest.getSourceProjectId())
                .expectComplete().verify();
    }

    @Test
    public void shouldFailWithInternalServerError() {
        enqueueError();

        UUID sourceProjectId = UUID.randomUUID();
        CreateProjectRequest createProjectRequest = new CreateProjectRequest()
                .setName("Target project")
                .setSourceProjectId(sourceProjectId);

        final SaveAsProjectRequest projectRequest = new SaveAsProjectRequest();
        projectRequest.setName(createProjectRequest.getName());
        projectRequest.setSourceId(createProjectRequest.getSourceProjectId());

        Mono<UUID> result = webService
                .saveProjectFromTemplateRequest(projectRequest, fromString(USER_GROUP_ID), ACCESS_TOKEN)
                .map(ProjectShortInfoDto::getId);

        StepVerifier
                .create(result)
                .expectErrorMatches(throwable -> throwable instanceof BasicApiResponseException &&
                        ((BasicApiResponseException) throwable).getResponseBody().equals("some error") &&
                        ((BasicApiResponseException) throwable).getHttpStatus().is5xxServerError()
                ).verify();
    }

    @Test
    public void shouldSuccessfullyExecuteStartProjectRequest() {
        mockWebServer.enqueue(new MockResponse()
                .setBody(String.valueOf(ResponseEntity.ok().build()))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Mono<UUID> emptyResult = webService.startProject(fromString(SOURCE_PROJECT_ID), ACCESS_TOKEN);

        StepVerifier
                .create(emptyResult)
                .expectNext(UUID.fromString(SOURCE_PROJECT_ID))
                .expectComplete().verify();
    }

    @Test
    public void shouldFailOnStartProjectRequest() {
        enqueueError();

        Mono<UUID> emptyResult = webService.startProject(fromString(SOURCE_PROJECT_ID), ACCESS_TOKEN);

        StepVerifier
                .create(emptyResult)
                .expectErrorMatches(throwable -> throwable instanceof BasicApiResponseException &&
                        ((BasicApiResponseException) throwable).getResponseBody().equals("some error") &&
                        ((BasicApiResponseException) throwable).getHttpStatus().is5xxServerError()
                ).verify();
    }

    @Test
    public void shouldSuccessfullyExecuteStartNodeRequest() {
        mockWebServer.enqueue(new MockResponse()
                .setBody(String.valueOf(ResponseEntity.ok().build()))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Mono<UUID> emptyResult = webService.startNode(fromString(SOURCE_PROJECT_ID), fromString(SOURCE_NODE_ID), ACCESS_TOKEN);

        StepVerifier
                .create(emptyResult)
                .expectNext(UUID.fromString(SOURCE_NODE_ID))
                .expectComplete().verify();
    }

    @Test
    public void shouldFailOnStartNodeRequest() {
        enqueueError();

        Mono<UUID> emptyResult = webService.startNode(fromString(SOURCE_PROJECT_ID), fromString(SOURCE_NODE_ID), ACCESS_TOKEN);

        StepVerifier
                .create(emptyResult)
                .expectErrorMatches(throwable -> throwable instanceof BasicApiResponseException &&
                        ((BasicApiResponseException) throwable).getResponseBody().equals("some error") &&
                        ((BasicApiResponseException) throwable).getHttpStatus().is5xxServerError()
                ).verify();
    }

    @Test
    public void shouldSuccessfullyCreateReportWithTemplate() {
        enqueue("json/api-report-communicator-create-report-template-response.json");

        UUID reportId = UUID.fromString("60414205-f722-4442-b72f-041d514f27f7");

        CreateReportRequest reportCreationRequest = new CreateReportRequest();
        reportCreationRequest.setRootProjectId(reportId);
        reportCreationRequest.setCreatedBy("psi_test1");

        Mono<UUID> result = webService
                .createReportWithTemplate(reportCreationRequest, ACCESS_TOKEN);

        StepVerifier
                .create(result)
                .expectNext(reportId)
                .expectComplete().verify();
    }

    @Test
    public void shouldFailCreateReportWithTemplate() {
        enqueueError();

        final CreateReportRequest reportCreationRequest = new CreateReportRequest();
        reportCreationRequest.setRootProjectId(UUID.fromString("60414205-f722-4442-b72f-041d514f27f7"));
        reportCreationRequest.setCreatedBy("psi_test1");

        Mono<UUID> result = webService.createReportWithTemplate(reportCreationRequest, ACCESS_TOKEN);

        StepVerifier
                .create(result)
                .expectErrorMatches(throwable -> throwable instanceof BasicApiResponseException &&
                        ((BasicApiResponseException) throwable).getResponseBody().equals("some error") &&
                        ((BasicApiResponseException) throwable).getHttpStatus().is5xxServerError())
                .verify();
    }


    @Test
    public void shouldSuccessfullyGetJsonReport() {
        JsonNode expectedResponse = enqueue("json/api-report-communicator-json-report.json");

        Mono<String> result = webService
                .getReport(fromString("ef19829f-2e92-46fe-9bb8-0a580464ed62"), ACCESS_TOKEN);

        StepVerifier
                .create(result)
                .expectNext(expectedResponse.toString())
                .expectComplete().verify();
    }

    @Test
    public void shouldFailGetJsonReport() {
        enqueueError();

        Mono<String> result = webService
                .getReport(fromString("ef19829f-2e92-46fe-9bb8-0a580464ed62"), ACCESS_TOKEN);

        StepVerifier
                .create(result)
                .expectErrorMatches(throwable -> throwable instanceof BasicApiResponseException &&
                        ((BasicApiResponseException) throwable).getResponseBody().equals("some error") &&
                        ((BasicApiResponseException) throwable).getHttpStatus().is5xxServerError())
                .verify();
    }

    @Test
    public void shouldSuccessfullyGetProjectNodesInfo() throws JsonProcessingException {
        enqueue("json/project-manager-nodes-info-response.json");

        final Mono<List<ProjectNodeInfo>> result = webService
                .getProjectNodesInfo(fromString(SOURCE_PROJECT_ID), ACCESS_TOKEN);
        final List<ProjectNodeInfo> projectNodeInfoList =
                objectMapper.readValue(
                        FileUtils.readResource("json/project-manager-nodes-info-response.json"),
                        new TypeReference<List<ProjectNodeInfo>>() {
                        });

        StepVerifier
                .create(result)
                .expectNext(projectNodeInfoList)
                .expectComplete().verify();
    }

    @Test
    public void shouldFailGetProjectNodesInfo() {
        enqueueError();

        Mono<List<ProjectNodeInfo>> jsonNode = webService.getProjectNodesInfo(fromString(SOURCE_PROJECT_ID), ACCESS_TOKEN);

        StepVerifier
                .create(jsonNode)
                .expectErrorMatches(throwable -> throwable instanceof BasicApiResponseException &&
                        ((BasicApiResponseException) throwable).getResponseBody().equals("some error") &&
                        ((BasicApiResponseException) throwable).getHttpStatus().is5xxServerError()
                ).verify();
    }

    @Test
    public void shouldSuccessfullySaveSettings() {
        String settingsBody = "{}";
        enqueue("json/api-validation-save-node-response.json");

        Mono<JsonNode> result = webService.saveSettingsRequest(ACCESS_TOKEN,
                fromString(SOURCE_PROJECT_ID),
                fromString(SOURCE_NODE_ID),
                settingsBody
        );
        JsonNode jsonNode = FileUtils.createJsonNode("json/api-validation-save-node-response.json");
        StepVerifier
                .create(result)
                .expectNext(jsonNode)
                .expectComplete().verify();
    }

    @Test
    public void shouldFailSaveSettings_500() {
        enqueueError();
        String settingsBody = "{}";
        Mono<JsonNode> result = webService.saveSettingsRequest(ACCESS_TOKEN, fromString(SOURCE_PROJECT_ID), fromString(SOURCE_NODE_ID), settingsBody);

        StepVerifier
                .create(result)
                .expectErrorMatches(throwable -> throwable instanceof BasicApiResponseException &&
                        ((BasicApiResponseException) throwable).getResponseBody().equals("some error") &&
                        ((BasicApiResponseException) throwable).getHttpStatus().is5xxServerError()
                ).verify();
    }

    @Test
    public void shouldSuccessfullyUploadCsv() { //todo
        final byte[] csvBody = "1".getBytes();
        final String responseBody = "/data/core/shsdh/app/tmp/ci02132621-edevlg-sdsdevelop5/b5b763b7-5729-4b93-aea7-e28a0bd94f201.csv";

        mockWebServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(responseBody)
        );

        Mono<String> result = webService.uploadCsvRequest(
                ACCESS_TOKEN,
                fromString(SOURCE_PROJECT_ID),
                fromString(SOURCE_NODE_ID),
                1,
                1,
                1,
                "some identifier",
                "some.csv",
                csvBody
        );

        StepVerifier
                .create(result)
                .expectNext(responseBody)
                .expectComplete()
                .verify();
    }

    @Test
    public void shouldFailUploadCsv_500() {
        enqueueError();
        final byte[] csvBody = "1".getBytes();
        Mono<String> result = webService.uploadCsvRequest(
                ACCESS_TOKEN,
                fromString(SOURCE_PROJECT_ID),
                fromString(SOURCE_NODE_ID),
                1,
                1,
                1,
                "some identifier",
                "some.csv",
                csvBody
        );

        StepVerifier
                .create(result)
                .expectErrorMatches(throwable -> throwable instanceof BasicApiResponseException &&
                        ((BasicApiResponseException) throwable).getResponseBody().equals("some error") &&
                        ((BasicApiResponseException) throwable).getHttpStatus().is5xxServerError()
                ).verify();
    }
}