package ru.webfluxExample.ds.rest;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.webfluxExample.ds.dto.request.SelectTemplateRequest;
import ru.webfluxExample.ds.service.PreValidationService;

@Slf4j
@RequiredArgsConstructor
@Component
public class TemplateSelectorHandler {

    private final PreValidationService preValidationService;

    public @NotNull Mono<ServerResponse> selectTemplate(ServerRequest request) {
        return request.bodyToMono(SelectTemplateRequest.class)
                .flatMap(selectTemplateRequest -> {
                    logRequest(request);
                    log.info("Request body {}", selectTemplateRequest);
                    return preValidationService.selectTemplate(selectTemplateRequest);
                })
                .flatMap(targetProjectId -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(targetProjectId)
                ).switchIfEmpty(ServerResponse.notFound().build())
                .doOnError(throwable -> log.error("Internal server error", throwable));
    }

    private void logRequest(ServerRequest request) {
        log.info("Request: {} {}", request.method(), request.uri());
        log.info("Request headers {}", request.headers());
    }
}
