package ru.webfluxExample.ds.props.mlr;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ConfigurationProperties("webclient.mlr")
@ConstructorBinding
public class MlrServerProperties {

    @NotBlank
    private final String scheme;

    @NotBlank
    private final String host;

    @Min(1025)
    @Max(65536)
    private final int port;

    @NotBlank
    private final String unstableEntitiesRoute;
}
