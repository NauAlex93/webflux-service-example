package ru.webfluxExample.ds.client;

import com.netflix.discovery.EurekaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.webfluxExample.ds.props.sds.SdsProjectManagerProperties;

import java.net.URI;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SdsProjectManagerClient extends AbstractClient {

    private final EurekaClient discoveryClient;

    private final SdsProjectManagerProperties sdsProjectManagerProperties;

    public URI internalProjectUri(@NotNull final UUID userGroupId) {
        return URI.create(String.format(
                "%s/%s?userGroupId=%s",
                getServiceUrl(sdsProjectManagerProperties, discoveryClient),
                sdsProjectManagerProperties.getInternalProjectRoute(),
                userGroupId.toString()
        ));
    }

    public URI internalNodesProjectUri(@NotNull final UUID projectId) {
        return URI.create(String.format(
                "%s/%s/%s/node",
                getServiceUrl(sdsProjectManagerProperties, discoveryClient),
                sdsProjectManagerProperties.getInternalNodesProjectRoute(),
                projectId.toString()
        ));
    }

    public URI internalEventsProjectUri(
            @NotNull final UUID projectId,
            @NotNull final UUID nodeId
    ) {
        return URI.create(String.format(
                "%s/%s/%s/node/%s/settings",
                getServiceUrl(sdsProjectManagerProperties, discoveryClient),
                sdsProjectManagerProperties.getInternalEventsProjectRoute(),
                projectId.toString(),
                nodeId.toString()
        ));
    }
}
