package ru.webfluxExample.ds.service;

import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.webfluxExample.ds.dto.mlr.response.UnstableEntitiesResponse;
import ru.webfluxExample.ds.exceptions.BasicApiResponseException;
import ru.webfluxExample.ds.props.mlr.MlrServerProperties;
import ru.webfluxExample.ds.service.stub.MlrWebServiceStub;

class MlrWebServiceTest extends AbstractWebClientTest {

    private final MlrServerProperties serverProperties = new MlrServerProperties(
            "http", "localhost", port, "/api/unstable/entities"
    );

    private final MlrWebService mlrWebService = new MlrWebServiceImpl(
            webClient, serverProperties
    );

    private final MultiValueMap<String, String> tokens = new LinkedMultiValueMap<>();

    @Test
    public void shouldReceiveUnstableEntitiesModelPromResponse() {
        final UnstableEntitiesResponse expectedResponse = enqueue("json/mlr/unstable-entities-model-prom-response.json", UnstableEntitiesResponse.class);

        final Mono<UnstableEntitiesResponse> response = mlrWebService.executeApiUnstableEntitiesRequest(
                1933, tokens
        );

        StepVerifier.create(response)
                .expectNextMatches(unstableEntitiesResponse -> unstableEntitiesResponse.getEntities().get(0).getStatus().equalsIgnoreCase("MODEL_PROM"))
                .expectComplete()
                .verify();
    }


    @Test
    public void shouldReceiveUnstableEntitiesModelDevResponse() {

        final UnstableEntitiesResponse expectedResponse = enqueue("json/mlr/unstable-entities-model-dev-response.json", UnstableEntitiesResponse.class);

        final Mono<UnstableEntitiesResponse> response = mlrWebService.executeApiUnstableEntitiesRequest(
                49744, tokens
        );

        StepVerifier.create(response)
                .expectNextMatches(unstableEntitiesResponse -> unstableEntitiesResponse.getEntities().get(0).getStatus().equalsIgnoreCase("MODEL_DEVELOPMENT"))
                .expectComplete()
                .verify();
    }

    @Test
    public void shouldReceiveUnstableEntitiesModelDraftResponse() {

        final UnstableEntitiesResponse expectedResponse = enqueue("json/mlr/unstable-entities-model-draft-response.json", UnstableEntitiesResponse.class);

        final Mono<UnstableEntitiesResponse> response = mlrWebService.executeApiUnstableEntitiesRequest(
                49744, tokens
        );

        StepVerifier.create(response)
                .expectNextMatches(unstableEntitiesResponse -> unstableEntitiesResponse.getEntities().get(0).getStatus().equalsIgnoreCase("MODEL_DRAFT"))
                .expectComplete()
                .verify();
    }

    @Test
    public void shouldReturnErrorWhenMlrRespondsWith500() {
        enqueueError();

        final Mono<UnstableEntitiesResponse> response = mlrWebService.executeApiUnstableEntitiesRequest(
                8080, tokens
        );

        StepVerifier.create(response)
                .expectErrorMatches(ex -> ex instanceof BasicApiResponseException &&
                        ((BasicApiResponseException) ex).getResponseBody().equals("some error") &&
                        ((BasicApiResponseException) ex).getHttpStatus().is5xxServerError()
                ).verify();
    }
}