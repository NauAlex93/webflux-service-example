package ru.webfluxExample.ds.service;

import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;
import ru.webfluxExample.ds.dto.mlr.response.UnstableEntitiesResponse;

public interface MlrWebService {
    /**
     * Calls Model Library for information about Model.
     *
     * @param modelId id of the requested model
     * @param tokens  tokens for authorization
     * @return model with parameters required for selecting template
     */
     Mono<UnstableEntitiesResponse> executeApiUnstableEntitiesRequest(
            long modelId, MultiValueMap<String, String> tokens
    );
}
