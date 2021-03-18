package ru.webfluxExample.ds.props.sds;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import javax.validation.constraints.NotBlank;

@Data
@ConstructorBinding
@ConfigurationProperties("webclient.rc")
public class SdsReportCommunicatorProperties implements SdsServiceProperties {

    @NotBlank
    private final String service;

    private final boolean isSecure;

    @NotBlank
    private final String reportsRoute;

    @NotBlank
    private final String reportsDefaultRoute;
}
