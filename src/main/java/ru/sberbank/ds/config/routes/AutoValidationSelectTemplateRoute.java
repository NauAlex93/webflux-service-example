package ru.webfluxExample.ds.config.routes;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.webfluxExample.ds.dto.request.SelectTemplateRequest;
import ru.webfluxExample.ds.rest.TemplateSelectorHandler;

@Configuration
public class AutoValidationSelectTemplateRoute {

    public static final  String PATH_SELECT_TEMPLATE = "/api/template/select";

    @RouterOperations({
            @RouterOperation(
                    path = PATH_SELECT_TEMPLATE,
                    operation = @Operation(operationId = "selectTemplate",
                            summary = "select template for validation",
                            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = SelectTemplateRequest.class))),
                            responses = {@ApiResponse(responseCode = "200", description = "OK",
                                    content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "400", description = "Bad request"),
                                    @ApiResponse(responseCode = "404", description = "Template not found")}))
    })
    @Bean
    public RouterFunction<ServerResponse> autoValidationSelectTemplateRoutes(TemplateSelectorHandler templateSelectorHandler) {
        return RouterFunctions.route()
                .POST(PATH_SELECT_TEMPLATE, templateSelectorHandler::selectTemplate)
                .build();
    }

}
