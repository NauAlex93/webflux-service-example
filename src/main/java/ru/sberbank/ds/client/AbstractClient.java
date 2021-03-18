package ru.webfluxExample.ds.client;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import ru.webfluxExample.ds.props.sds.SdsServiceProperties;

public class AbstractClient {

    protected InstanceInfo serviceInstance(
            SdsServiceProperties serviceProperties,
            EurekaClient discoveryClient
    ) {
        final Application serviceApp = discoveryClient.getApplication(serviceProperties.getService());
        return serviceApp.getInstances().stream().findFirst().orElseThrow(
                () -> new IllegalStateException(
                        "Requested service unavailable " + serviceProperties.getService()
                ));
    }

    protected String getServiceUrl(
            SdsServiceProperties serviceProperties,
            EurekaClient discoveryClient
    ) {
        final InstanceInfo instance = serviceInstance(serviceProperties, discoveryClient);
        return String.format(
                "%s://%s:%d",
                getScheme(serviceProperties.isSecure()),
                instance.getHostName(),
                getPort(serviceProperties.isSecure(), instance)
        );
    }

    protected String getScheme(boolean isSecure) {
        final String scheme;
        if (isSecure) {
            scheme = "https";
        } else {
            scheme = "http";
        }
        return scheme;
    }

    protected int getPort(boolean isSecure, InstanceInfo instanceInfo) {
        final int port;
        if (isSecure) {
            port = instanceInfo.getSecurePort();
        } else {
            port = instanceInfo.getPort();
        }
        return port;
    }
}
