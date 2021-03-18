package ru.webfluxExample.ds.props.mlr;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ConstructorBinding
@ConfigurationProperties("webclient.mlr.auth")
public class MlrServerAuthProperties {

    @NotBlank
    private final String username;

    @NotBlank
    private final String password;

    @NotNull
    private final MlrAuthUserServiceProperties userService;

    @NotNull
    private final MlrAuthMeTokensProperties meTokens;
}
