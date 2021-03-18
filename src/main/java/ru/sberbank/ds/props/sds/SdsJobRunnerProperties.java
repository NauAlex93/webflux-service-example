package ru.webfluxExample.ds.props.sds;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import javax.validation.constraints.NotBlank;

@Data
@ConstructorBinding
@ConfigurationProperties("webclient.jr")
public class SdsJobRunnerProperties implements SdsServiceProperties {

    @NotBlank
    private final String service;

    private final boolean isSecure;

    @NotBlank
    private final String internalSubmitRoute;
}
