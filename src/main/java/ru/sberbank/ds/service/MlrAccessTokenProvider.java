package ru.webfluxExample.ds.service;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import ru.webfluxExample.ds.dto.mlr.response.MeTokensResponse;
import ru.webfluxExample.ds.dto.mlr.UserFindRequest;
import ru.webfluxExample.ds.dto.mlr.response.UserFindResponse;
import ru.webfluxExample.ds.exceptions.BasicApiResponseException;
import ru.webfluxExample.ds.exceptions.UserServiceLoginsNotFoundException;
import ru.webfluxExample.ds.exceptions.UserServiceUsersNotFoundException;
import ru.webfluxExample.ds.props.mlr.MlrServerAuthProperties;

import java.net.URI;

/**
 * Provides access tokens for authorizing when connecting to MLR.
 */
@Profile("!local")
@Slf4j
@Service
public class MlrAccessTokenProvider {

    private final WebClient webClient;

    private final WebClient mlrWebClient;

    private final String username;

    private final String password;

    private final URI userFindUri;

    private final URI meTokensUri;

    public MlrAccessTokenProvider(
            WebClient webClient,
            WebClient mlrWebClient,
            MlrServerAuthProperties mlrServerAuthProperties
    ) {
        this.webClient = webClient;
        this.mlrWebClient = mlrWebClient;
        this.username = mlrServerAuthProperties.getUsername();
        this.password = mlrServerAuthProperties.getPassword();
        this.userFindUri = UriComponentsBuilder.newInstance()
                .scheme(mlrServerAuthProperties.getUserService().getScheme())
                .host(mlrServerAuthProperties.getUserService().getHost())
                .port(mlrServerAuthProperties.getUserService().getPort())
                .path(mlrServerAuthProperties.getUserService().getUserFindRoute())
                .build().toUri();
        this.meTokensUri = UriComponentsBuilder.newInstance()
                .scheme(mlrServerAuthProperties.getMeTokens().getScheme())
                .host(mlrServerAuthProperties.getMeTokens().getHost())
                .port(mlrServerAuthProperties.getMeTokens().getPort())
                .path(mlrServerAuthProperties.getMeTokens().getMeTokensRoute())
                .build().toUri();
    }

    /**
     * Calls user-service, retrieves user information by ipaLogin,
     * then calls me/tokens to retrieve auth and refresh tokens
     * by using webfluxExample PDI.
     *
     * @param userIpaLogin user's ipaLogin
     * @return auth and refresh tokens
     */
    public Mono<MultiValueMap<String, String>> getAuthTokensByIpaLogin(@NotNull final String userIpaLogin) {
        return getUserByIpaLogin(userIpaLogin)
                .flatMap(userFindResponse -> userFindResponse
                        .getUsers()
                        .stream()
                        .findFirst()
                        .map(user -> user
                                .getLogins()
                                .stream()
                                .filter(login -> login.getType().equals("SudirLogin"))
                                .findFirst()
                                .map(login -> getTokensBywebfluxExamplePdi(login.getUsername()))
                                .orElseGet(() -> Mono.error(new UserServiceLoginsNotFoundException(userIpaLogin))))
                        .orElseGet(() -> Mono.error(new UserServiceUsersNotFoundException(userIpaLogin))))
                .log("step: get auth tokens");
    }

    /**
     * Calls user-service to retrieve information about user by his IpaLogin.
     *
     * @param userIpaLogin user's ipaLogin
     * @return user's information about logins
     */
    public Mono<UserFindResponse> getUserByIpaLogin(@NotNull final String userIpaLogin) {
        return webClient.post()
                .uri(userFindUri)
                .bodyValue(new UserFindRequest(userIpaLogin))
                .headers(headers -> headers.setBasicAuth(username, password))
                .exchange()
                .flatMap(response -> {
                    if (!response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(String.class).flatMap(errorBody ->
                                Mono.error(new BasicApiResponseException(errorBody, response.statusCode())));
                    }
                    return response.bodyToMono(UserFindResponse.class);
                })
                .log("step: get user from /user/find");
    }

    /**
     * Retrieves auth and refresh tokens from me/tokens by user's webfluxExamplePdi.
     *
     * @param webfluxExamplePdi user's webfluxExamplePDI
     * @return auth and refresh tokens
     */
    public Mono<MultiValueMap<String, String>> getTokensBywebfluxExamplePdi(@NotNull final String webfluxExamplePdi) {
        return mlrWebClient.get()
                .uri(meTokensUri)
                .header("webfluxExamplepdi", webfluxExamplePdi)
                .exchange()
                .flatMap(response -> {
                    if (!response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(String.class).flatMap(errorBody ->
                                Mono.error(new BasicApiResponseException(errorBody,
                                        response.statusCode())));
                    }
                    return response.bodyToMono(MeTokensResponse.class).flatMap(meTokensResponse -> {
                        MultiValueMap<String, String> tokens = new LinkedMultiValueMap<>();
                        tokens.add("authToken", meTokensResponse.getAuthToken());
                        tokens.add("refreshToken", meTokensResponse.getRefreshToken());
                        tokens.add("webfluxExamplepdi", webfluxExamplePdi);
                        return Mono.just(tokens);
                    });
                })
                .log("step: get tokens from me/tokens");
    }
}
