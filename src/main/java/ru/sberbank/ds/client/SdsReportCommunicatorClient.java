package ru.webfluxExample.ds.client;

import com.netflix.discovery.EurekaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.webfluxExample.ds.props.sds.SdsReportCommunicatorProperties;

import java.net.URI;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SdsReportCommunicatorClient extends AbstractClient {

    private final EurekaClient discoveryClient;

    private final SdsReportCommunicatorProperties sdsReportCommunicatorProperties;

    public URI reportsUri(@NotNull final UUID reportId) {
        return URI.create(String.format(
                "%s/%s/%s/file",
                getServiceUrl(sdsReportCommunicatorProperties, discoveryClient),
                sdsReportCommunicatorProperties.getReportsRoute(),
                reportId.toString()
        ));
    }

    public URI reportsDefaultUri() {
        return URI.create(String.format(
                "%s/%s",
                getServiceUrl(sdsReportCommunicatorProperties, discoveryClient),
                sdsReportCommunicatorProperties.getReportsDefaultRoute()
        ));
    }
}
