package ru.webfluxExample.ds.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.webfluxExample.ds.dto.CreateProjectRequest;
import ru.webfluxExample.ds.service.ProjectAutoValidationService;
import ru.webfluxExample.ds.util.UUIDUtils;

import java.util.UUID;

/**
 * Обработка рест запросов по авто-валидации.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class AutoValidationHandler {

    private final ProjectAutoValidationService projectAutoValidationService;
    private static final String PROJECT_ID = "projectId";
    private static final String REPORT_ID = "reportId";
    private static final String NODE_ID = "nodeId";
    private static final String WRONG_FORMAT_OF_UUID = "Wrong format of uuid for variable= %s, value= %s";

    public @NotNull Mono<ServerResponse> createAndStartProject(ServerRequest request) {
        return request.bodyToMono(CreateProjectRequest.class)
                .flatMap(createProjectRequest -> {
                    logRequest(request);
                    log.info("Request body {}", createProjectRequest);
                    return projectAutoValidationService.createAndStartProject(createProjectRequest);
                })
                .flatMap(targetProjectId -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(targetProjectId)
                ).switchIfEmpty(ServerResponse.notFound().build())
                .doOnError(throwable -> log.error("Internal server error", throwable));
    }

    public @NotNull Mono<ServerResponse> getStatus(ServerRequest request) {
        logRequest(request);
        if (!UUIDUtils.validateUUID(request.pathVariable(PROJECT_ID))) {
            return ServerResponse.badRequest().bodyValue(String.format(WRONG_FORMAT_OF_UUID, PROJECT_ID, request.pathVariable(PROJECT_ID)));
        }
        return projectAutoValidationService.getStatus(UUID.fromString(request.pathVariable(PROJECT_ID)))
                .flatMap(report -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(report)
                ).switchIfEmpty(ServerResponse.notFound().build());
    }

    public @NotNull Mono<ServerResponse> createAndGetReport(ServerRequest request) {
        logRequest(request);
        if (!UUIDUtils.validateUUID(request.pathVariable(PROJECT_ID))) {
            return ServerResponse.badRequest().bodyValue(String.format(WRONG_FORMAT_OF_UUID, PROJECT_ID, request.pathVariable(PROJECT_ID)));
        }
        return projectAutoValidationService.createAndGetReport(UUID.fromString(request.pathVariable(PROJECT_ID)))
                .flatMap(report -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(report)
                ).switchIfEmpty(ServerResponse.notFound().build());
    }

    public @NotNull Mono<ServerResponse> createProject(ServerRequest request) {
        return request.bodyToMono(CreateProjectRequest.class)
                .flatMap(createProjectRequest -> {
                    logRequest(request);
                    log.info("Request body {}", createProjectRequest);
                    return projectAutoValidationService.createProject(createProjectRequest);
                })
                .flatMap(targetProjectId -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(targetProjectId)
                ).switchIfEmpty(ServerResponse.notFound().build());
    }

    public @NotNull Mono<ServerResponse> startProject(ServerRequest request) {
        logRequest(request);
        if (!UUIDUtils.validateUUID(request.pathVariable(PROJECT_ID))) {
            return ServerResponse.badRequest().bodyValue(String.format(WRONG_FORMAT_OF_UUID, PROJECT_ID, request.pathVariable(PROJECT_ID)));
        }
        return projectAutoValidationService.startProject(UUID.fromString(request.pathVariable(PROJECT_ID)))
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    public @NotNull Mono<ServerResponse> startNode(ServerRequest request) {
        logRequest(request);
        String projectId = request.pathVariable(PROJECT_ID);
        if (!UUIDUtils.validateUUID(projectId)) {
            return ServerResponse.badRequest().bodyValue(String.format(WRONG_FORMAT_OF_UUID, PROJECT_ID, projectId));
        }
        String nodeId = request.pathVariable(NODE_ID);
        if (!UUIDUtils.validateUUID(nodeId)) {
            return ServerResponse.badRequest().bodyValue(String.format(WRONG_FORMAT_OF_UUID, NODE_ID, nodeId));
        }

        return projectAutoValidationService.startNode(UUID.fromString(projectId), UUID.fromString(nodeId))
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    public @NotNull Mono<ServerResponse> createReportWithTemplate(ServerRequest request) {
        logRequest(request);
        if (!UUIDUtils.validateUUID(request.pathVariable(PROJECT_ID))) {
            return ServerResponse.badRequest().bodyValue(String.format(WRONG_FORMAT_OF_UUID, PROJECT_ID, request.pathVariable(PROJECT_ID)));
        }
        return request.bodyToMono(String.class)
                .flatMap(emptyBody -> projectAutoValidationService.createReportWithTemplate(UUID.fromString(request.pathVariable(PROJECT_ID))))
                .flatMap(targetProjectId -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(targetProjectId)
                ).switchIfEmpty(ServerResponse.notFound().build());
    }

    public @NotNull Mono<ServerResponse> getReport(ServerRequest request) {
        logRequest(request);
        if (!UUIDUtils.validateUUID(request.pathVariable(REPORT_ID))) {
            return ServerResponse.badRequest().bodyValue(String.format(WRONG_FORMAT_OF_UUID, REPORT_ID, request.pathVariable(REPORT_ID)));
        }
        return projectAutoValidationService.getReport(UUID.fromString(request.pathVariable(REPORT_ID)))
                .flatMap(report -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(report)
                ).switchIfEmpty(ServerResponse.notFound().build());
    }

    public @NotNull Mono<ServerResponse> saveSettings(ServerRequest request) {
        logRequest(request);
        if (!UUIDUtils.validateUUID(request.pathVariable(PROJECT_ID))) {
            return ServerResponse.badRequest().bodyValue(String.format(WRONG_FORMAT_OF_UUID, PROJECT_ID, request.pathVariable(PROJECT_ID)));
        }
        if (!UUIDUtils.validateUUID(request.pathVariable(NODE_ID))) {
            return ServerResponse.badRequest().bodyValue(String.format(WRONG_FORMAT_OF_UUID, NODE_ID, request.pathVariable(NODE_ID)));
        }
        return request.bodyToMono(String.class).flatMap(settings -> projectAutoValidationService.saveSettings(
                UUID.fromString(request.pathVariable(PROJECT_ID)),
                UUID.fromString(request.pathVariable(NODE_ID)),
                settings))
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response)
                ).switchIfEmpty(ServerResponse.notFound().build());
    }

    public @NotNull Mono<ServerResponse> uploadCsvToDatasource(ServerRequest request) {
        logRequest(request);
        final StringBuilder errorMessage = new StringBuilder();
        if (!UUIDUtils.validateUUID(request.pathVariable(PROJECT_ID))) {
            errorMessage.append(String.format(WRONG_FORMAT_OF_UUID, PROJECT_ID, request.pathVariable(PROJECT_ID))).append(System.lineSeparator());
        }
        if (!UUIDUtils.validateUUID(request.pathVariable(NODE_ID))) {
            errorMessage.append(String.format(WRONG_FORMAT_OF_UUID, NODE_ID, request.pathVariable(NODE_ID))).append(System.lineSeparator());
        }

        if (errorMessage.length() > 0) {
            return ServerResponse.badRequest().bodyValue(errorMessage.toString());
        }

        return request.body(BodyExtractors.toMultipartData())
                .flatMap(map ->
                        projectAutoValidationService.uploadCsv(
                                UUID.fromString(request.pathVariable(PROJECT_ID)),
                                UUID.fromString(request.pathVariable(NODE_ID)),
                                map.toSingleValueMap()
                        )
                ).flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response)
                ).switchIfEmpty(ServerResponse.notFound().build());
    }

    private void logRequest(ServerRequest request) {
        log.info("Request: {} {}", request.method(), request.uri());
        log.info("Request headers {}", request.headers());
    }
}
