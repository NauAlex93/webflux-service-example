package ru.webfluxExample.ds.service;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.webfluxExample.ds.client.SdsAuthServerClient;
import ru.webfluxExample.ds.dto.AccessToken;
import ru.webfluxExample.ds.exceptions.BasicApiResponseException;

@Profile("!local")
@Service
public class AccessTokenProviderImpl implements AccessTokenProvider {

    private final WebClient webClient;
    private final String basicAuthHeader;
    private final SdsAuthServerClient sdsAuthServerClient;

    public AccessTokenProviderImpl(
            @Value("${webclient.auth.basic.username}") String userName,
            @Value("${webclient.auth.basic.password}") String password,
            WebClient webClient,
            SdsAuthServerClient sdsAuthServerClient
    ) {
        this.basicAuthHeader = getBasicAuthToken(userName, password);
        this.webClient = webClient;
        this.sdsAuthServerClient = sdsAuthServerClient;
    }

    @Override
    public Mono<OAuth2AccessToken> getAccessToken() {
        return webClient.post()
                .uri(sdsAuthServerClient.getServiceTokenUri())
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeader)
                .exchangeToMono(response -> {
                    if (!response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(String.class).flatMap(errorBody ->
                                Mono.error(new BasicApiResponseException(errorBody, response.statusCode())));
                    }
                    return response.bodyToMono(OAuth2AccessToken.class);
                });
    }

    @Override
    public Mono<AccessToken> getAccessTokenInfo(String token) {
        return webClient.post()
                .uri(sdsAuthServerClient.checkTokenUri(token))
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeader)
                .exchangeToMono(response -> {
                    if (!response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(String.class).flatMap(errorBody ->
                                Mono.error(new BasicApiResponseException(errorBody, response.statusCode())));
                    }
                    return response.bodyToMono(AccessToken.class);
                });
    }

    private String getBasicAuthToken(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }
}