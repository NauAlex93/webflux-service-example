package ru.webfluxExample.ds.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.webfluxExample.ds.dto.mlr.response.UserFindResponse;
import ru.webfluxExample.ds.exceptions.BasicApiResponseException;
import ru.webfluxExample.ds.exceptions.UserServiceLoginsNotFoundException;
import ru.webfluxExample.ds.exceptions.UserServiceUsersNotFoundException;
import ru.webfluxExample.ds.props.mlr.MlrAuthMeTokensProperties;
import ru.webfluxExample.ds.props.mlr.MlrAuthUserServiceProperties;
import ru.webfluxExample.ds.props.mlr.MlrServerAuthProperties;

class MlrAccessTokenProviderTest extends AbstractWebClientTest {

    private final MlrServerAuthProperties authProperties = new MlrServerAuthProperties(
            "mlrUsr", "mlrPsw",
            new MlrAuthUserServiceProperties(
                    "http", "localhost", port, "/user/find"
            ),
            new MlrAuthMeTokensProperties(
                    "http", "localhost", port, "/me/tokens"
            )
    );

    private final MlrAccessTokenProvider mlrAccessTokenProvider =
            new MlrAccessTokenProvider(webClient, webClient, authProperties);

    @Test
    public void shouldGetUserInformationFromUserService() {
        final JsonNode expectedResponse = enqueue("json/mlr/user-find-response.json");

        final Mono<UserFindResponse> response = mlrAccessTokenProvider
                .getUserByIpaLogin("someIpaLogin");

        StepVerifier.create(response)
                .expectNextMatches(actualResponse ->
                        actualResponse.getUsers().size() == 1 &&
                                actualResponse.getUsers().get(0).getLogins().size() == 2 &&
                                actualResponse.getUsers().get(0).getLogins().get(0).getUsername().equals(
                                        expectedResponse.findValue("username").textValue()
                                ) &&
                                actualResponse.getUsers().get(0).getLogins().get(0).getType().equals(
                                        expectedResponse.findValue("$type").textValue()
                                )
                ).expectComplete()
                .verify();
    }

    @Test
    public void shouldFailIfUserInformationReturns500() {
        enqueueError();

        Mono<UserFindResponse> response = mlrAccessTokenProvider
                .getUserByIpaLogin("ignored");

        StepVerifier.create(response)
                .expectErrorMatches(ex -> ex instanceof BasicApiResponseException &&
                        ((BasicApiResponseException) ex).getHttpStatus().is5xxServerError() &&
                        ((BasicApiResponseException) ex).getResponseBody().equals("some error")
                ).verify();
    }

    @Test
    public void shouldGetTokensFromMeTokens() {
        enqueue("json/mlr/me-tokens-response.json");

        Mono<MultiValueMap<String, String>> response = mlrAccessTokenProvider
                .getTokensBywebfluxExamplePdi("somewebfluxExamplePdi");

        StepVerifier.create(response)
                .expectNextMatches(tokens -> tokens.containsKey("authToken") &&
                        tokens.containsKey("refreshToken")
                ).expectComplete()
                .verify();
    }

    @Test
    public void shouldRetrieveTokensFromUserServiceAndMeTokens() {
        enqueue("json/mlr/user-find-response.json");
        enqueue("json/mlr/me-tokens-response.json");

        Mono<MultiValueMap<String, String>> response = mlrAccessTokenProvider
                .getAuthTokensByIpaLogin("someIpaLogin");

        StepVerifier.create(response)
                .expectNextMatches(tokens -> tokens.containsKey("authToken") &&
                        tokens.containsKey("refreshToken")
                ).expectComplete()
                .verify();
    }

    @Test
    public void shouldFailIfNoUserWasFound() {
        enqueue("json/mlr/user-find-no-user-response.json");

        Mono<MultiValueMap<String, String>> response = mlrAccessTokenProvider
                .getAuthTokensByIpaLogin("someIpaLogin");

        StepVerifier.create(response)
                .expectErrorMatches(ex -> ex instanceof UserServiceUsersNotFoundException)
                .verify();
    }

    @Test
    public void shouldFailIfNoLoginsWasFound() {
        enqueue("json/mlr/user-find-no-login-response.json");

        Mono<MultiValueMap<String, String>> response = mlrAccessTokenProvider
                .getAuthTokensByIpaLogin("someIpaLogin");

        StepVerifier.create(response)
                .expectErrorMatches(ex -> ex instanceof UserServiceLoginsNotFoundException)
                .verify();
    }
}