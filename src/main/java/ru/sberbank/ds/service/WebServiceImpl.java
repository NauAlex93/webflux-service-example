package ru.webfluxExample.ds.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.webfluxExample.ds.client.SdsDataSourceClient;
import ru.webfluxExample.ds.client.SdsJobRunnerClient;
import ru.webfluxExample.ds.client.SdsProjectManagerClient;
import ru.webfluxExample.ds.client.SdsReportCommunicatorClient;
import ru.webfluxExample.ds.dto.CreateReportRequest;
import ru.webfluxExample.ds.dto.ProjectShortInfoDto;
import ru.webfluxExample.ds.exceptions.BasicApiResponseException;
import ru.webfluxExample.ds.project.manager.api.dto.project.ProjectDto;
import ru.webfluxExample.ds.project.manager.api.dto.project.ProjectNodeInfo;
import ru.webfluxExample.ds.project.manager.api.dto.project.SaveAsProjectRequest;
import ru.webfluxExample.ds.util.NodeMapper;
import ru.webfluxExample.ds.util.ProjectMapper;

import java.util.List;
import java.util.UUID;

@Profile("!local")
@Slf4j
@RequiredArgsConstructor
@Service
public class WebServiceImpl implements WebService {

    private final WebClient webClient;
    private final ProjectMapper projectMapper;
    private final NodeMapper nodeMapper;

    private final SdsProjectManagerClient sdsProjectManagerClient;
    private final SdsJobRunnerClient sdsJobRunnerClient;
    private final SdsReportCommunicatorClient sdsReportCommunicatorClient;
    private final SdsDataSourceClient sdsDataSourceClient;

    public Mono<ProjectShortInfoDto> saveProjectFromTemplateRequest(
            @NotNull final SaveAsProjectRequest projectRequest,
            @NotNull final UUID userGroupId,
            @NotNull final String token
    ) {
        return webClient.post()
                .uri(sdsProjectManagerClient.internalProjectUri(userGroupId))
                .headers(headers -> headers.setBearerAuth(token))
                .bodyValue(projectRequest)
                .exchangeToMono(response -> {
                    if (!response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(String.class).flatMap(errorBody ->
                                Mono.error(new BasicApiResponseException(errorBody, response.statusCode())));
                    }
                    return response.bodyToMono(ProjectDto.class);
                })
                .map(projectMapper::toProjectInfoDto)
                .flatMap(projectInfo -> {
                    if (!projectInfo.getNodes().isEmpty()) {
                        return getProjectNodesInfo(projectInfo.getId(), token)
                                .map(nodeMapper::toNodeSettingsMap)
                                .map(settingsMap -> {
                                    if (settingsMap != null) {
                                        projectInfo.getNodes().forEach(nodeInfo -> nodeInfo.setSettings(settingsMap.get(nodeInfo.getId())));
                                    }
                                    return projectInfo;
                                });
                    }
                    return Mono.just(projectInfo);
                });
    }

    public Mono<UUID> startProject(@NotNull UUID projectId, String token) {
        return webClient.get()
                .uri(sdsJobRunnerClient.internalProjectSubmitUri(projectId))
                .headers(headers -> headers.setBearerAuth(token))
                .exchangeToMono(response -> response.toEntity(String.class))
                .flatMap(response -> {
                    HttpStatus responseStatus = response.getStatusCode();
                    if (!response.getStatusCode().is2xxSuccessful()) {
                        return Mono.error(new BasicApiResponseException(response.getBody(), responseStatus));
                    }
                    return Mono.just(projectId);
                });
    }

    public Mono<UUID> startNode(@NotNull UUID projectId, @NotNull UUID nodeId, String token) {
        return webClient.get()
                .uri(sdsJobRunnerClient.internalNodeSubmitUri(projectId, nodeId))
                .headers(headers -> headers.setBearerAuth(token))
                .exchangeToMono(response -> response.toEntity(String.class))
                .flatMap(response -> {
                    HttpStatus responseStatus = response.getStatusCode();
                    if (!response.getStatusCode().is2xxSuccessful()) {
                        return Mono.error(new BasicApiResponseException(response.getBody(), responseStatus));
                    }
                    return Mono.just(nodeId);
                });
    }

    public Mono<UUID> createReportWithTemplate(CreateReportRequest createReportRequest, String token) {
        return webClient.post()
                .uri(sdsReportCommunicatorClient.reportsDefaultUri())
                .headers(headers -> headers.setBearerAuth(token))
                .bodyValue(createReportRequest)
                .exchangeToMono(response -> {
                    if (!response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(String.class).flatMap(errorBody ->
                                Mono.error(new BasicApiResponseException(errorBody, response.statusCode())));
                    }
                    return response.bodyToMono(JsonNode.class);
                })
                .map(jsonNode -> UUID.fromString(jsonNode.get("id").asText()));
    }

    public Mono<String> getReport(@NotNull UUID reportId, String token) {
        return webClient.get()
                .uri(sdsReportCommunicatorClient.reportsUri(reportId))
                .headers(headers -> headers.setBearerAuth(token))
                .exchangeToMono(response -> {
                    if (!response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(String.class).flatMap(errorBody ->
                                Mono.error(new BasicApiResponseException(errorBody,
                                        response.statusCode())));
                    }
                    return response.bodyToMono(String.class);
                });
    }

    public Mono<List<ProjectNodeInfo>> getProjectNodesInfo(@NotNull UUID projectId, String token) {
        return webClient.get()
                .uri(sdsProjectManagerClient.internalNodesProjectUri(projectId))
                .headers(headers -> headers.setBearerAuth(token))
                .exchangeToMono(response -> {
                    if (!response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(String.class).flatMap(errorBody ->
                                Mono.error(new BasicApiResponseException(errorBody, response.statusCode())));
                    }
                    return response.bodyToMono(new ParameterizedTypeReference<List<ProjectNodeInfo>>() {
                    });
                });
    }

    public Mono<JsonNode> saveSettingsRequest(@NotNull String token, @NotNull UUID projectId, @NotNull UUID nodeId, @NotNull String settingsJson) {
        return webClient.put()
                .uri(sdsProjectManagerClient.internalEventsProjectUri(projectId, nodeId))
                .bodyValue(settingsJson)
                .headers(headers -> headers.setBearerAuth(token))
                .exchangeToMono(response -> {
                    if (!response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(String.class).flatMap(errorBody ->
                                Mono.error(new BasicApiResponseException(errorBody, response.statusCode())));
                    }
                    return response.bodyToMono(JsonNode.class);
                });
    }

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
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("resumableChunkNumber", String.valueOf(resumableChunkNumber));
        params.add("resumableChunkSize", String.valueOf(resumableChunkSize));
        params.add("resumableTotalChunks", String.valueOf(resumableTotalChunks));
        params.add("resumableIdentifier", resumableIdentifier);
        params.add("resumableFilename", resumableFilename);

        return webClient.put()
                .uri(sdsDataSourceClient.dataSourceUri(params, projectId.toString(), "node", nodeId.toString(), "settings"))
                .bodyValue(csvFile)
                .headers(headers -> headers.setBearerAuth(token))
                .exchangeToMono(response -> {
                    if (!response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(String.class).flatMap(errorBody ->
                                Mono.error(new BasicApiResponseException(errorBody, response.statusCode())));
                    }
                    return response.bodyToMono(String.class);
                });
    }
}
