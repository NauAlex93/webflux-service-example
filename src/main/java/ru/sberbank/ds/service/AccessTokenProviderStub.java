package ru.webfluxExample.ds.service;

import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.webfluxExample.ds.dto.AccessToken;

import java.util.Collections;
import java.util.HashMap;

@Profile("local")
@Service
public class AccessTokenProviderStub implements AccessTokenProvider {
    @Override
    public Mono<OAuth2AccessToken> getAccessToken() {
        return Mono.just(DefaultOAuth2AccessToken.valueOf(new HashMap<>()));
    }

    @Override
    public Mono<AccessToken> getAccessTokenInfo(String token) {
        AccessToken accessToken = new AccessToken();
        accessToken.setActive(true);
        accessToken.setUsername("user");
        accessToken.setAuthorities(Collections.emptyList());

        return Mono.just(accessToken);
    }
}