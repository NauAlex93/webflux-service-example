package ru.webfluxExample.ds.service.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.webfluxExample.ds.dto.mlr.response.UnstableEntitiesResponse;
import ru.webfluxExample.ds.props.mlr.MlrServerProperties;
import ru.webfluxExample.ds.service.MlrWebService;

import java.io.File;
import java.net.URI;

@Profile("local")
@Slf4j
@Service
public class MlrWebServiceStub implements MlrWebService {

    private static final String basePath = new File(".").getAbsolutePath();

    private final WebClient mlrWebClient;

    private final URI unstableEntitiesUri;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MlrWebServiceStub(WebClient mlrWebClient, MlrServerProperties serverProperties) {
        this.mlrWebClient = null;
        this.unstableEntitiesUri = null;
    }

    /**
     * Calls Model Library for information about Model.
     *
     * @param modelId id of the requested model
     * @param tokens  tokens for authorization
     * @return model with parameters required for selecting template
     */
    @Override
    @SneakyThrows
    public Mono<UnstableEntitiesResponse> executeApiUnstableEntitiesRequest(
            long modelId, MultiValueMap<String, String> tokens
    ) {
        return Mono.just(
                objectMapper.readValue(
                        new File(basePath + "/samples/mlr/unstable-entities-model-dev-response.json"),
                        UnstableEntitiesResponse.class
                )
        );
    }
}
