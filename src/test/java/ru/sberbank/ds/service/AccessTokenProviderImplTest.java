package ru.webfluxExample.ds.service;

import com.netflix.discovery.shared.Application;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.webfluxExample.ds.client.SdsAuthServerClient;
import ru.webfluxExample.ds.dto.AccessToken;
import ru.webfluxExample.ds.exceptions.BasicApiResponseException;
import ru.webfluxExample.ds.props.sds.SdsAuthServerProperties;
import ru.webfluxExample.ds.util.FileUtils;

import java.util.Collections;

class AccessTokenProviderImplTest extends AbstractWebClientTest {

    private final SdsAuthServerProperties sdsAuthServerProperties = new SdsAuthServerProperties(
            "auth-server", false,"check_token", "get_token"
    );

    private final SdsAuthServerClient sdsAuthServerClient = new SdsAuthServerClient(
            discoveryClient, sdsAuthServerProperties
    );

    private final AccessTokenProvider accessTokenProvider = new AccessTokenProviderImpl(
            "service", "servicesecret",
            webClient, sdsAuthServerClient);

    @BeforeEach
    void beforeEach() {
        Application app = new Application(sdsAuthServerProperties.getService(),
                Collections.singletonList(instanceInfo));
        Mockito.when(discoveryClient.getApplication(sdsAuthServerProperties.getService()))
                .thenReturn(app);
    }

    @Test
    void getAccessToken() {
        enqueue("json/api-auth-server-token-response.json");

        Mono<OAuth2AccessToken> response = accessTokenProvider.getAccessToken();

        StepVerifier.create(response)
                .expectNext(new DefaultOAuth2AccessToken("accessToken"))
                .expectComplete()
                .verify();
    }

    @Test
    void checkAccessToken() {
        enqueue("json/api-auth-server-check-token-response.json");
        AccessToken accessToken = FileUtils.fromJson(
                FileUtils.readResource("json/api-auth-server-check-token-response.json"),
                AccessToken.class
        );

        Mono<AccessToken> response = accessTokenProvider.getAccessTokenInfo("accessToken");

        StepVerifier.create(response)
                .expectNext(accessToken)
                .expectComplete()
                .verify();
    }

    @Test
    void shouldFailWithInvalidTokenCheck() {
        enqueueError();

        Mono<AccessToken> response = accessTokenProvider.getAccessTokenInfo("ignored");

        StepVerifier.create(response)
                .expectErrorMatches(ex -> ex instanceof BasicApiResponseException &&
                        ((BasicApiResponseException) ex).getResponseBody().equals("some error") &&
                        ((BasicApiResponseException) ex).getHttpStatus().is5xxServerError()
                ).verify();
    }
}