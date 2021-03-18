package ru.webfluxExample.ds.config.routes;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.webfluxExample.ds.dto.CreateProjectRequest;
import ru.webfluxExample.ds.dto.ProjectRunStatus;
import ru.webfluxExample.ds.rest.AutoValidationHandler;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;

@Configuration
public class AutoValidationProjectRoute {

    public static final String PATH_CREATE_AND_START_PROJECT = "/api/project/create-and-start";
    public static final String PATH_GET_PROJECT_STATUS = "/api/project/{projectId}";
    public static final String PATH_CREATE_AND_GET_REPORT = "/api/project/{projectId}/report/create-and-get";
    public static final String PATH_CREATE_PROJECT = "/api/project";
    public static final String PATH_START_PROJECT = "/api/project/{projectId}/start";
    public static final String PATH_START_NODE = "/api/project/{projectId}/{nodeId}/start";
    public static final String PATH_CREATE_REPORT_FROM_TEMPLATE = "/api/project/{projectId}/report";
    public static final String PATH_SAVE_SETTINGS_FOR_NODE = "/api/project/{projectId}/node/{nodeId}/settings";
    public static final String PATH_SAVE_CSV_FOR_DATASOURCE = "/api/project/{projectId}/datasource/{nodeId}/upload/csv";

    @RouterOperations({
            @RouterOperation(
                    path = PATH_CREATE_AND_START_PROJECT,
                    operation = @Operation(operationId = "createAndStartProject",
                            summary = "Create and start project from template",
                            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = CreateProjectRequest.class))),
                            responses = {@ApiResponse(responseCode = "200", description = "Successfully created",
                                    content = @Content(schema = @Schema(implementation = UUID.class))),
                                    @ApiResponse(responseCode = "400", description = "Bad request"),
                                    @ApiResponse(responseCode = "404", description = "Source project not found")})),
            @RouterOperation(
                    path = PATH_GET_PROJECT_STATUS,
                    operation = @Operation(operationId = "getStatus",
                            summary = "Get status of project and all its nodes",
                            parameters = @Parameter(name = "projectId", in = ParameterIn.PATH, schema = @Schema(implementation = UUID.class)),
                            responses = {@ApiResponse(responseCode = "200", description = "OK",
                                    content = @Content(schema = @Schema(implementation = ProjectRunStatus.class))),
                                    @ApiResponse(responseCode = "400", description = "Bad request"),
                                    @ApiResponse(responseCode = "404", description = "Source project not found")})),
            @RouterOperation(
                    path = PATH_CREATE_AND_GET_REPORT,
                    operation = @Operation(operationId = "createAndGetReport",
                            summary = "Create report with default template and receives it in json",
                            parameters = @Parameter(name = "projectId", in = ParameterIn.PATH,
                                    schema = @Schema(implementation = UUID.class), example = "cfb3a2f9-edeb-41f8-82d7-50fdab3d1561"),
                            responses = {@ApiResponse(responseCode = "200", description = "OK",
                                    content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "400", description = "Bad request"),
                                    @ApiResponse(responseCode = "404", description = "Source project not found")})),
            @RouterOperation(
                    path = PATH_CREATE_PROJECT,
                    operation = @Operation(operationId = "createProject",
                            summary = "Create project from template",
                            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = CreateProjectRequest.class))),
                            responses = {@ApiResponse(responseCode = "200", description = "Successfully created",
                                    content = @Content(schema = @Schema(implementation = UUID.class))),
                                    @ApiResponse(responseCode = "400", description = "Bad request"),
                                    @ApiResponse(responseCode = "404", description = "Source project not found")})),
            @RouterOperation(
                    path = PATH_START_PROJECT,
                    operation = @Operation(operationId = "startProject",
                            summary = "Start existed project",
                            parameters = @Parameter(name = "projectId", in = ParameterIn.PATH, schema = @Schema(implementation = UUID.class)),
                            responses = {@ApiResponse(responseCode = "200", description = "OK"),
                                    @ApiResponse(responseCode = "400", description = "Bad request"),
                                    @ApiResponse(responseCode = "404", description = "Source project not found")})),
            @RouterOperation(
                    path = PATH_CREATE_REPORT_FROM_TEMPLATE,
                    operation = @Operation(operationId = "createReport",
                            summary = "Create report with default template",
                            responses = {@ApiResponse(responseCode = "200", description = "OK",
                                    content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "400", description = "Bad request"),
                                    @ApiResponse(responseCode = "404", description = "Source project not found")})),
            @RouterOperation(
                    path = PATH_SAVE_SETTINGS_FOR_NODE,
                    operation = @Operation(operationId = "setSettings",
                            summary = "set settings for current node in json",
                            parameters = {@Parameter(name = "projectId", in = ParameterIn.PATH, schema = @Schema(implementation = UUID.class)),
                                    @Parameter(name = "nodeId", in = ParameterIn.PATH, schema = @Schema(implementation = UUID.class))},
                            responses = {@ApiResponse(responseCode = "200", description = "OK",
                                    content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "400", description = "Bad request"),
                                    @ApiResponse(responseCode = "404", description = "Node not found")})),
            @RouterOperation(
                    path = PATH_SAVE_CSV_FOR_DATASOURCE,
                    operation = @Operation(operationId = "uploadCsvToDatasource",
                            summary = "upload CSV file to datasource",
                            parameters = {@Parameter(name = "projectId", in = ParameterIn.PATH, schema = @Schema(implementation = UUID.class)),
                                    @Parameter(name = "nodeId", in = ParameterIn.PATH, schema = @Schema(implementation = UUID.class)),
                                    @Parameter(name = "resumableChunkNumber", in = ParameterIn.QUERY, schema = @Schema(implementation = int.class)),
                                    @Parameter(name = "resumableChunkSize", in = ParameterIn.QUERY, schema = @Schema(implementation = int.class)),
                                    @Parameter(name = "resumableTotalChunks", in = ParameterIn.QUERY, schema = @Schema(implementation = int.class)),
                                    @Parameter(name = "resumableIdentifier", in = ParameterIn.QUERY, schema = @Schema(implementation = String.class)),
                                    @Parameter(name = "resumableFilename", in = ParameterIn.QUERY, schema = @Schema(implementation = String.class))
                            },
                            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = FilePart.class))),
                            responses = {@ApiResponse(responseCode = "200", description = "OK",
                                    content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "400", description = "Bad request"),
                                    @ApiResponse(responseCode = "404", description = "Node not found")}))
    })
    @Bean
    public RouterFunction<ServerResponse> autoValidationProjectRoutes(AutoValidationHandler autoValidationHandler) {
        return RouterFunctions.route()
                .POST(PATH_CREATE_AND_START_PROJECT, accept(APPLICATION_JSON), autoValidationHandler::createAndStartProject)
                .GET(PATH_GET_PROJECT_STATUS, autoValidationHandler::getStatus)
                .POST(PATH_CREATE_AND_GET_REPORT, accept(APPLICATION_JSON), autoValidationHandler::createAndGetReport)
                .POST(PATH_CREATE_PROJECT, accept(APPLICATION_JSON), autoValidationHandler::createProject)
                .POST(PATH_START_PROJECT, accept(APPLICATION_JSON), autoValidationHandler::startProject)
                .POST(PATH_START_NODE, accept(APPLICATION_JSON), autoValidationHandler::startNode)
                .POST(PATH_CREATE_REPORT_FROM_TEMPLATE, accept(APPLICATION_JSON), autoValidationHandler::createReportWithTemplate)
                .PUT(PATH_SAVE_SETTINGS_FOR_NODE, autoValidationHandler::saveSettings)
                .POST(PATH_SAVE_CSV_FOR_DATASOURCE, contentType(MULTIPART_FORM_DATA).and(accept(APPLICATION_JSON)), autoValidationHandler::uploadCsvToDatasource)
                .build();
    }

}
