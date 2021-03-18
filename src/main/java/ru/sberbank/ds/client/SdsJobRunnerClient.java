package ru.webfluxExample.ds.client;

import com.netflix.discovery.EurekaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.webfluxExample.ds.props.sds.SdsJobRunnerProperties;

import java.net.URI;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SdsJobRunnerClient extends AbstractClient {

    private final EurekaClient discoveryClient;

    private final SdsJobRunnerProperties sdsJobRunnerProperties;

    public URI internalProjectSubmitUri(@NotNull final UUID projectId) {
        return URI.create(String.format(
                "%s/%s/%s",
                getServiceUrl(sdsJobRunnerProperties, discoveryClient),
                sdsJobRunnerProperties.getInternalSubmitRoute(),
                projectId.toString()
        ));
    }

    public URI internalNodeSubmitUri(@NotNull final UUID projectId, @NotNull final UUID nodeId) {
        return URI.create(String.format(
                "%s/%s/%s/%s",
                getServiceUrl(sdsJobRunnerProperties, discoveryClient),
                sdsJobRunnerProperties.getInternalSubmitRoute(),
                projectId.toString(),
                nodeId.toString()
        ));
    }
}
