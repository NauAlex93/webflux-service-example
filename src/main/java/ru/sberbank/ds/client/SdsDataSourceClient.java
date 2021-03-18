package ru.webfluxExample.ds.client;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import ru.webfluxExample.ds.props.sds.SdsDataSourceProperties;

import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class SdsDataSourceClient extends AbstractClient {

    private final EurekaClient discoveryClient;

    private final SdsDataSourceProperties sdsDataSourceProperties;

    public URI dataSourceUri(MultiValueMap<String, String> params, String... pathSegments) {
        final InstanceInfo serviceInstance = serviceInstance(sdsDataSourceProperties, discoveryClient);
        return UriComponentsBuilder.newInstance()
                .scheme(getScheme(sdsDataSourceProperties.isSecure()))
                .host(serviceInstance.getHostName())
                .port(getPort(sdsDataSourceProperties.isSecure(), serviceInstance))
                .path(sdsDataSourceProperties.getRoute())
                .pathSegment(pathSegments)
                .queryParams(params)
                .build()
                .toUri();
    }
}
