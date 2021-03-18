package ru.webfluxExample.ds.client;

import com.netflix.discovery.EurekaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.webfluxExample.ds.props.sds.SdsAuthServerProperties;

import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class SdsAuthServerClient extends AbstractClient {

    private final EurekaClient discoveryClient;

    private final SdsAuthServerProperties sdsAuthServerProperties;

    public URI checkTokenUri(@NotNull final String token) {
        return URI.create(String.format(
                "%s/%s?token=%s",
                getServiceUrl(sdsAuthServerProperties, discoveryClient),
                sdsAuthServerProperties.getCheckTokenRoute(),
                token
        ));
    }
    
    public URI getServiceTokenUri() {
        return URI.create(String.format(
                "%s/%s?grant_type=service_as_user",
                getServiceUrl(sdsAuthServerProperties, discoveryClient),
                sdsAuthServerProperties.getServiceTokenRoute()
        ));
    }
}
