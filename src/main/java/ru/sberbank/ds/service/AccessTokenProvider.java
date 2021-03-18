package ru.webfluxExample.ds.service;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import reactor.core.publisher.Mono;
import ru.webfluxExample.ds.dto.AccessToken;

public interface AccessTokenProvider {

    /**
     * Provides service access token for internal requests
     *
     * @return Access token value
     */
    Mono<OAuth2AccessToken> getAccessToken();

    /**
     * Provides access token info for current user's token
     *
     * @param token user's token
     * @return Access token value
     */
    Mono<AccessToken> getAccessTokenInfo(String token);
}
