package ru.webfluxExample.ds.service.stub;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.webfluxExample.ds.dto.CreateReportRequest;
import ru.webfluxExample.ds.dto.ProjectShortInfoDto;
import ru.webfluxExample.ds.exceptions.BasicApiResponseException;
import ru.webfluxExample.ds.project.manager.api.dto.project.ProjectNodeInfo;
import ru.webfluxExample.ds.project.manager.api.dto.project.SaveAsProjectRequest;
import ru.webfluxExample.ds.service.WebService;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Profile("local")
@Service
public class WebServiceStub implements WebService {

    private static final String basePath = new File(".").getAbsolutePath();

    private static final Map<UUID, Exception> uuidToExceptionMap = new HashMap<>();

    static {
        uuidToExceptionMap.put(
                UUID.fromString("00000000-0000-0000-0000-000000000001"),
                new BasicApiResponseException("body", HttpStatus.BAD_REQUEST)
        );
        uuidToExceptionMap.put(
                UUID.fromString("00000000-0000-0000-0000-000000000002"),
                new BasicApiResponseException("body", HttpStatus.INTERNAL_SERVER_ERROR)
        );
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    @Override
    public Mono<ProjectShortInfoDto> saveProjectFromTemplateRequest(
            @NotNull SaveAsProjectRequest projectRequest,
            @NotNull UUID userGroupId,
            String token
    ) {
        Exception exception = uuidToExceptionMap.get(projectRequest.getSourceId());
        if (exception != null) {
            return Mono.error(exception);
        } else {
            return Mono.just(
                    objectMapper.readValue(
                            new File(basePath + "/samples/json/project-short-info.json"),
                            ProjectShortInfoDto.class
                    )
            );
        }
    }

    @Override
    public Mono<UUID> startProject(@NotNull UUID projectId, String token) {
        Exception exception = uuidToExceptionMap.get(projectId);
        if (exception != null) {
            return Mono.error(exception);
        } else {
            return Mono.just(projectId);
        }
    }

    @Override
    public Mono<UUID> startNode(@NotNull UUID projectId, @NotNull UUID nodeId, String token) {
        Exception exception = uuidToExceptionMap.get(nodeId);
        if (exception != null) {
            return Mono.error(exception);
        } else {
            return Mono.just(nodeId);
        }
    }

    @Override
    public Mono<UUID> createReportWithTemplate(CreateReportRequest createReportRequest, String token) {
        Exception exception = uuidToExceptionMap.get(createReportRequest.getRootProjectId());
        if (exception != null) {
            return Mono.error(exception);
        } else {
            return Mono.just(UUID.randomUUID());
        }
    }

    @SneakyThrows
    @Override
    public Mono<String> getReport(@NotNull UUID reportId, String token) {
        Exception exception = uuidToExceptionMap.get(reportId);
        if (exception != null) {
            return Mono.error(exception);
        } else {
            return Mono.just(
                    objectMapper.readValue(
                            new File(basePath + "/samples/json/api-report-communicator-json-report.json"), String.class)
            );
        }
    }

    @SneakyThrows
    @Override
    public Mono<List<ProjectNodeInfo>> getProjectNodesInfo(@NotNull UUID projectId, String token) {
        Exception exception = uuidToExceptionMap.get(projectId);
        if (exception != null) {
            return Mono.error(exception);
        } else {
            return Mono.just(
                    objectMapper
                            .readerForListOf(ProjectNodeInfo.class)
                            .readValue(new File(basePath + "/samples/json/api-project-manager-nodes-not-ready-response.json"))
            );
        }
    }

    @SneakyThrows
    @Override
    public Mono<JsonNode> saveSettingsRequest(
            @NotNull String token,
            @NotNull UUID projectId,
            @NotNull UUID nodeId,
            @NotNull String settingsJson
    ) {
        Exception exception = uuidToExceptionMap.get(projectId);
        if (exception != null) {
            return Mono.error(exception);
        } else {
            return Mono.just(
                    objectMapper.readValue(
                            new File(basePath + "/samples/json/api-validation-save-node-response.json"), JsonNode.class)
            );
        }
    }

    @Override
    public Mono<String> uploadCsvRequest(
            @NotNull String token,
            @NotNull UUID projectId,
            @NotNull UUID nodeId,
            @NotNull Integer resumableChunkNumber,
            @NotNull Integer resumableChunkSize,
            @NotNull Integer resumableTotalChunks,
            @NotNull String resumableIdentifier,
            @NotNull String resumableFilename,
            @NotNull byte[] csvFile
    ) {
        Exception exception = uuidToExceptionMap.get(projectId);
        if (exception != null) {
            return Mono.error(exception);
        } else {
            return Mono.just("hdfs/local/data/file.csv");
        }
    }
}