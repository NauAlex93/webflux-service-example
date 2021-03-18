package ru.webfluxExample.ds.config.routes;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.webfluxExample.ds.rest.AutoValidationHandler;

import java.util.UUID;

@Configuration
public class AutoValidationReportRoute {

    public static final  String PATH_GET_REPORT = "/api/report/{reportId}";

    @RouterOperations({
            @RouterOperation(
                    path = PATH_GET_REPORT,
                    operation = @Operation(operationId = "getReport",
                            summary = "Receive report in json",
                            parameters = @Parameter(name = "reportId", in = ParameterIn.PATH, schema = @Schema(implementation = UUID.class)),
                            responses = {@ApiResponse(responseCode = "200", description = "OK",
                                    content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "400", description = "Bad request"),
                                    @ApiResponse(responseCode = "404", description = "Report not found")}))
    })
    @Bean
    public RouterFunction<ServerResponse> autoValidationReportRoutes(AutoValidationHandler autoValidationHandler) {
        return RouterFunctions.route()
                .GET(PATH_GET_REPORT, autoValidationHandler::getReport)
                .build();
    }

}
