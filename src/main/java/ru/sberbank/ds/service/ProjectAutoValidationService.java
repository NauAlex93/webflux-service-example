package ru.webfluxExample.ds.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
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
import ru.webfluxExample.ds.util.NodeMapper;
import ru.webfluxExample.ds.util.ProjectMapper;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class ProjectAutoValidationService {

    private final AccessTokenProvider accessTokenProvider;
    private final WebService webService;
    private final ProjectMapper projectMapper;
    private final NodeMapper nodeMapper;

    private final UUID sourceProjectId;
    private final UUID userGroupId;
    private final String clientId;
    private final AtomicInteger counter = new AtomicInteger(0);

    public ProjectAutoValidationService(@Value("${webclient.source-project-id}") String sourceProjectId,
                                        @Value("${webclient.user-group-id}") String userGroupId,
                                        @Value("${webclient.auth.basic.username}") String clientId,
                                        AccessTokenProvider accessTokenProvider,
                                        WebService webService,
                                        ProjectMapper projectMapper,
                                        NodeMapper nodeMapper) {
        this.sourceProjectId = UUID.fromString(sourceProjectId);
        this.userGroupId = UUID.fromString(userGroupId);
        this.clientId = clientId;
        this.accessTokenProvider = accessTokenProvider;
        this.webService = webService;
        this.projectMapper = projectMapper;
        this.nodeMapper = nodeMapper;
    }

    /**
     * Создаёт проект из шаблона и затем запускает его
     *
     * @param createProjectRequest запрос на создание проекта с информацией о новом проекте
     * @return JSON информация о созданном проекте
     */
    public Mono<ProjectShortInfoDto> createAndStartProject(@NotNull CreateProjectRequest createProjectRequest) {
        SaveAsProjectRequest projectRequest = buildProjectDto(createProjectRequest);

        return accessTokenProvider.getAccessToken()
                .flatMap(token -> {
                            UUID actualUserGroupId = createProjectRequest.getUserGroupId() != null ? createProjectRequest.getUserGroupId() : userGroupId;
                            log.info("Creating new project {} with userGroupId {}", projectRequest, actualUserGroupId);
                            return webService.saveProjectFromTemplateRequest(projectRequest, actualUserGroupId, token.getValue())
                                    .flatMap(projectInfo -> {
                                        webService.startProject(projectInfo.getId(), token.getValue());
                                        return Mono.just(projectInfo);
                                    });
                        }
                )
                .doOnSuccess(projectInfo -> log.info("Successfully created created and started project with UUID {}", projectInfo.getId()));
    }

    /**
     * Получает статус выполнения проекта и статусы всех его нод
     *
     * @param targetProjectId id проекта
     * @return статус проекта и всех его нод
     */
    public Mono<ProjectRunStatus> getStatus(@NotNull UUID targetProjectId) {
        ProjectRunStatus projectRunStatus = new ProjectRunStatus();
        projectRunStatus.setProjectId(targetProjectId);

        return accessTokenProvider.getAccessToken()
                .flatMap(token -> {
                            log.info("Calling project manager to get project status {}", targetProjectId);
                            return webService.getProjectNodesInfo(targetProjectId, token.getValue())
                                    .map(projectNodeInfoList -> processNodesStatus(projectNodeInfoList, projectRunStatus));
                        }
                )
                .doOnSuccess(projectStatus -> log.info("Successfully received project status {}", projectStatus));
    }

    /**
     * Создаёт отчёт о выполнении проекта с дефолтным шаблоном и получает его в формате json
     *
     * @param targetProjectId id проекта
     * @return отчёт в формате json
     */
    public Mono<String> createAndGetReport(@NotNull UUID targetProjectId) {
        return accessTokenProvider.getAccessToken()
                .log()
                .flatMap(token -> {
                            log.info("Calling report communicator to create report for project {} and clientId {}", targetProjectId, clientId);
                            return webService.createReportWithTemplate(new CreateReportRequest(targetProjectId, clientId), token.getValue())
                                    .flatMap(reportId -> webService.getReport(reportId, token.getValue()));
                        }
                )
                .doOnSuccess(report -> log.info("Successfully received report {}", report));
    }

    /**
     * Создаёт новый проект из шаблона по id основного проекта
     *
     * @param createProjectRequest запрос на создание проекта с информацией о новом проекте
     * @return JSON информация о созданном проекте
     */
    public Mono<ProjectShortInfoDto> createProject(@NotNull CreateProjectRequest createProjectRequest) {
        SaveAsProjectRequest projectRequest = buildProjectDto(createProjectRequest);

        return accessTokenProvider.getAccessToken()
                .flatMap(token -> {
                            UUID actualUserGroupId = createProjectRequest.getUserGroupId() != null ? createProjectRequest.getUserGroupId() : userGroupId;
                            log.info("Calling project manager service to create project {} and userGroupId {}", projectRequest, actualUserGroupId);
                            return webService.saveProjectFromTemplateRequest(projectRequest, actualUserGroupId, token.getValue());
                        }
                )
                .doOnSuccess(projectInfo -> log.info("Successfully created project with uuid {}", projectInfo.getId()));
    }

    /**
     * Запускает проект
     *
     * @param targetProjectId id проекта
     * @return UUID проекта
     */
    public Mono<UUID> startProject(@NotNull UUID targetProjectId) {
        return accessTokenProvider.getAccessToken()
                .log()
                .flatMap(token -> {
                    log.info("Calling job runner service to start project {}", targetProjectId);
                    return webService.startProject(targetProjectId, token.getValue());
                })
                .doOnSuccess(emptyString -> log.info("Project {} successfully started", targetProjectId));
    }

    /**
     * Запускает ноду
     *
     * @param targetProjectId id проекта
     * @param targetNodeId id ноды
     * @return UUID ноды
     */
    public Mono<UUID> startNode(@NotNull UUID targetProjectId, @NotNull UUID targetNodeId) {
        return accessTokenProvider.getAccessToken()
                .log()
                .flatMap(token -> {
                    log.info("Calling job runner service to start project {} node {}", targetProjectId, targetNodeId);
                    return webService.startNode(targetProjectId, targetNodeId, token.getValue());
                })
                .doOnSuccess(emptyString -> log.info("Node {} successfully started", targetNodeId));
    }

    /**
     * Создаёт отчёт с дефолтным шаблоном для соответствующего проекта
     *
     * @param targetProjectId id проекта
     * @return id созданного отчёта
     */
    public Mono<UUID> createReportWithTemplate(@NotNull UUID targetProjectId) {
        return accessTokenProvider.getAccessToken()
                .flatMap(token -> {
                    log.info("Calling report communicator service to create report for project {}", targetProjectId);
                    return webService.createReportWithTemplate(new CreateReportRequest(targetProjectId, clientId), token.getValue());
                })
                .doOnSuccess(reportId -> log.info("Report successfully created {}", reportId));
    }

    /**
     * Получает отчёт о выполнении проекта в формате json
     *
     * @param reportId id отчёта
     * @return отчёт в формате json
     */
    public Mono<String> getReport(@NotNull UUID reportId) {
        return accessTokenProvider.getAccessToken()
                .flatMap(token -> {
                            log.info("Calling report communicator to get report with id {}", reportId);
                            return webService.getReport((reportId), token.getValue());
                        }
                )
                .doOnSuccess(report -> log.info("Report received {}", report));

    }

    /**
     * Сохраняем настройки ноды
     *
     * @param projectId    UUID проекта в котором будем сохранять настройки ноды
     * @param nodeId       UUID нода в которой будем сохранять настройки
     * @param settingsJson сами настройки которые будем сохранять в ноде
     * @return статус проекта и всех его нод
     */
    public Mono<JsonNode> saveSettings(@NotNull UUID projectId, @NotNull UUID nodeId, @NotNull String settingsJson) {
        return accessTokenProvider.getAccessToken()
                .flatMap(token -> {
                    log.info("Calling settings communicator to save settings for project with id={} and node with id={}", projectId, nodeId);
                    return webService.saveSettingsRequest(token.getValue(), projectId, nodeId, settingsJson);
                })
                .doOnSuccess(updateSettings -> log.info("Settings was successfully updated {}", updateSettings));
    }

    /**
     * Добавляем CSV к datasource
     *
     * @param projectId UUID проекта в котором будем сохранять настройки datasource
     * @param nodeId    UUID datasource в который добавляется CSV-файл
     * @param partMap   данные из блока multipart
     * @return статус загрузки
     */
    public Mono<String> uploadCsv(@NotNull UUID projectId, @NotNull UUID nodeId, @NotNull Map<String, Part> partMap) {
        return accessTokenProvider.getAccessToken()
                .flatMap(token -> {
                    log.info("Calling datasource csv communicator to save settings for project with id={} and node with id={}", projectId, nodeId);

                    if (partMap.entrySet().size() != 1) {
                        return Mono.error(new BasicApiResponseException("Expected only 1 .csv file", HttpStatus.BAD_REQUEST));
                    }

                    Part part = partMap.entrySet().stream().findFirst().get().getValue();

                    return part.content()
                            .flatMap(dataBuffer -> {
                                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(bytes);
                                DataBufferUtils.release(dataBuffer);

                                return webService.uploadCsvRequest(
                                        token.getValue(),
                                        projectId,
                                        nodeId,
                                        1,
                                        bytes.length,
                                        bytes.length,
                                        UUID.randomUUID().toString(),
                                        part.name(),
                                        bytes
                                );
                            })
                            .next();
                })
                .doOnSuccess(filePath -> log.info("File was successfully uploaded to {}", filePath));
    }

    private ProjectRunStatus processNodesStatus(List<ProjectNodeInfo> projectNodeInfoList, ProjectRunStatus projectRunStatus) {
        Set<ProjectNodeStatus> unDoneNodeStatuses = new HashSet<>();
        List<NodeDto> nodes = new ArrayList<>();

        for (ProjectNodeInfo node : projectNodeInfoList) {
            NodeDto reportNode = nodeMapper.toNodeDto(node);
            nodes.add(reportNode);

            if (reportNode.getProjectNodeStatus() != ProjectNodeStatus.DONE) {
                unDoneNodeStatuses.add(reportNode.getProjectNodeStatus());
            }
        }

        updateProjectExecutionStatus(projectRunStatus, unDoneNodeStatuses);

        projectRunStatus.setNodes(nodes);
        return projectRunStatus;
    }

    private SaveAsProjectRequest buildProjectDto(CreateProjectRequest createProjectRequest) {
        final SaveAsProjectRequest projectRequest = projectMapper.toSaveAsProjectRequest(createProjectRequest);

        if (projectRequest.getSourceId() == null) {
            projectRequest.setSourceId(this.sourceProjectId);
        }

        if (projectRequest.getName() == null) {
            projectRequest.setName(String.format("[AutoValidation] #%d From project %s", counter.incrementAndGet(), this.sourceProjectId));
        }

        return projectRequest;
    }

    private void updateProjectExecutionStatus(ProjectRunStatus projectRunStatus, Set<ProjectNodeStatus> unDoneNodeStatuses) {
        if (unDoneNodeStatuses.contains(ProjectNodeStatus.LAUNCHED)
                || unDoneNodeStatuses.contains(ProjectNodeStatus.NOT_READY)
                || unDoneNodeStatuses.contains(ProjectNodeStatus.READY)
        ) {
            projectRunStatus.setProjectExecutionStatus(ProjectExecutionStatus.IN_PROGRESS);
        } else if (unDoneNodeStatuses.contains(ProjectNodeStatus.FAILED)) {
            projectRunStatus.setProjectExecutionStatus(ProjectExecutionStatus.FAILED);
        } else {
            projectRunStatus.setProjectExecutionStatus(ProjectExecutionStatus.DONE);
        }
    }
}
