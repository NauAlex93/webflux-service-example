package ru.webfluxExample.ds.service;

import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Mono;
import ru.webfluxExample.ds.dto.CreateReportRequest;
import ru.webfluxExample.ds.dto.ProjectShortInfoDto;
import ru.webfluxExample.ds.project.manager.api.dto.project.ProjectNodeInfo;
import ru.webfluxExample.ds.project.manager.api.dto.project.SaveAsProjectRequest;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public interface WebService {

    /**
     * Вызывает API webfluxExample.DS для создания нового проекта из шаблона
     *
     * @param projectRequest DTO создаваемого проекта
     * @param userGroupId    ID проектной области пользователя
     * @param token          токен аутентификации SDS
     * @return UUID созданного проекта
     */
    Mono<ProjectShortInfoDto> saveProjectFromTemplateRequest(
            @NotNull SaveAsProjectRequest projectRequest,
            @NotNull UUID userGroupId,
            String token
    );

    /**
     * Вызывает API webfluxExample.DS для запуска проекта
     *
     * @param projectId id запускаемого проекта
     * @param token     токен аутентификации SDS
     * @return UUID запускаемого проекта
     */
    Mono<UUID> startProject(@NotNull UUID projectId, String token);

    /**
     * Вызывает API webfluxExample.DS для запуска ноды
     *
     * @param projectId id проекта
     * @param nodeId    id запускаемой ноды
     * @param token     токен аутентификации SDS
     * @return UUID запускаемой ноды
     */
    Mono<UUID> startNode(@NotNull UUID projectId, @NotNull UUID nodeId, String token);

    /**
     * Вызывает API webfluxExample.DS для создания отчёта по выполнению проекта с дефолтным шаблоном
     *
     * @param createReportRequest запрос на создание отчёта с информацией об авторе проекта и id проекта
     * @param token               токен аутентификации SDS
     * @return id созданного отчёта
     */
    Mono<UUID> createReportWithTemplate(CreateReportRequest createReportRequest, String token);

    /**
     * Вызывает API webfluxExample.DS для получения отчёта о выполнении проекта в формате json
     *
     * @param reportId id отчёта
     * @param token    токен аутентификации SDS
     * @return отчёт в формате json
     */
    Mono<String> getReport(@NotNull UUID reportId, String token);

    /**
     * Вызывает API webfluxExample.DS для получения информации о статусах всех нод проекта
     *
     * @param projectId id проекта
     * @param token     токен аутентификации SDS
     * @return данные по всем нодам проекта в виде JsonNode объекта
     */
    Mono<List<ProjectNodeInfo>> getProjectNodesInfo(@NotNull UUID projectId, String token);

    /**
     * Вызываем API webfluxExample.DS project-manager-service для обновления настроек ноды(всех кроме ноды типа dataSource)
     *
     * @param token        токен пользователя для аунтефикации в webfluxExample.DS
     * @param projectId    UUID проекта в котором будем сохранять настройки ноды
     * @param nodeId       UUID нода в которой будем сохранять настройки
     * @param settingsJson сами настройки которые будем сохранять в ноде
     * @return статус проекта и всех его нод
     */
    Mono<JsonNode> saveSettingsRequest(
            @NotNull String token,
            @NotNull UUID projectId,
            @NotNull UUID nodeId,
            @NotNull String settingsJson
    );

    /**
     * Вызываем API webfluxExample.DS data-source-service для загрузки CSV-файла
     *
     * @param projectId            UUID проекта в котором будем сохранять настройки datasource
     * @param nodeId               UUID datasource в который добавляется CSV-файл
     * @param resumableChunkNumber колличество chunk (таблиц)
     * @param resumableChunkSize   размер chunk
     * @param resumableTotalChunks всего chunk
     * @param resumableIdentifier  идентификатор файла
     * @param resumableFilename    имя файла
     * @param csvFile              файл CSV для datasource (в виде массива)
     * @return статус datasource
     */
    Mono<String> uploadCsvRequest(
            @NotNull String token,
            @NotNull UUID projectId,
            @NotNull UUID nodeId,
            @NotNull Integer resumableChunkNumber,
            @NotNull Integer resumableChunkSize,
            @NotNull Integer resumableTotalChunks,
            @NotNull String resumableIdentifier,
            @NotNull String resumableFilename,
            @NotNull byte[] csvFile
    );
}
