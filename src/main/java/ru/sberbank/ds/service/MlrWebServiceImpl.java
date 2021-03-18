package ru.webfluxExample.ds.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import ru.webfluxExample.ds.dto.mlr.UnstableEntitiesPredicate;
import ru.webfluxExample.ds.dto.mlr.request.UnstableEntitiesRequest;
import ru.webfluxExample.ds.dto.mlr.response.UnstableEntitiesResponse;
import ru.webfluxExample.ds.exceptions.BasicApiResponseException;
import ru.webfluxExample.ds.props.mlr.MlrServerProperties;

import java.net.URI;

@Profile("!local")
@Slf4j
@Service
public class MlrWebServiceImpl implements MlrWebService{

    private final WebClient mlrWebClient;

    private final URI unstableEntitiesUri;

    public MlrWebServiceImpl(WebClient mlrWebClient, MlrServerProperties serverProperties) {
        this.mlrWebClient = mlrWebClient;
        this.unstableEntitiesUri = UriComponentsBuilder.newInstance()
                .scheme(serverProperties.getScheme())
                .host(serverProperties.getHost())
                .port(serverProperties.getPort())
                .path(serverProperties.getUnstableEntitiesRoute())
                .build().toUri();
    }

    /**
     * Calls Model Library for information about Model.
     *
     * @param modelId id of the requested model
     * @param tokens  tokens for authorization
     * @return model with parameters required for selecting template
     */
    @Override
    public Mono<UnstableEntitiesResponse> executeApiUnstableEntitiesRequest(
            long modelId, MultiValueMap<String, String> tokens
    ) {
        return mlrWebClient.post()
                .uri(unstableEntitiesUri)
                .header(HttpHeaders.AUTHORIZATION, tokens.getFirst("AUTH_TOKEN"))
                .header("webfluxExamplePDI", tokens.getFirst("webfluxExamplePDI"))
                .cookies(cookies -> cookies.addAll(tokens))
                .bodyValue(new UnstableEntitiesRequest(
                        "MODEL",
                        new UnstableEntitiesPredicate(
                                "COMMA_LIST",
                                "MODEL_ID",
                                String.valueOf(modelId)
                        )
                )).exchange()
                .flatMap(response -> {
                    if (!response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(String.class).flatMap(
                                errorBody -> Mono.error(
                                        new BasicApiResponseException(errorBody, response.statusCode())
                                )
                        );
                    }
                    return response.bodyToMono(UnstableEntitiesResponse.class);
                }).log();
    }
}
