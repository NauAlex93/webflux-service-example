package ru.webfluxExample.ds.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.webfluxExample.ds.service.AccessTokenProvider;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final AccessTokenProvider accessTokenProvider;

    public AuthenticationManager(AccessTokenProvider accessTokenProvider) {
        this.accessTokenProvider = accessTokenProvider;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();

        return accessTokenProvider.getAccessTokenInfo(authToken).flatMap(accessTokenInfo -> {
            log.info("Received access token info " + accessTokenInfo);

            if (!accessTokenInfo.isActive()) {
                return Mono.empty();
            }

            List<SimpleGrantedAuthority> authorities = accessTokenInfo.getAuthorities().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    accessTokenInfo.getUsername(),
                    null,
                    authorities
            );

            return Mono.just(authenticationToken);
        });
    }
}
