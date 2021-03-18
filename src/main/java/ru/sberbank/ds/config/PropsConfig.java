package ru.webfluxExample.ds.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ru.webfluxExample.ds.props.mlr.MlrAuthMeTokensProperties;
import ru.webfluxExample.ds.props.mlr.MlrAuthUserServiceProperties;
import ru.webfluxExample.ds.props.mlr.MlrServerAuthProperties;
import ru.webfluxExample.ds.props.mlr.MlrServerProperties;
import ru.webfluxExample.ds.props.sds.SdsAuthServerProperties;
import ru.webfluxExample.ds.props.sds.SdsDataSourceProperties;
import ru.webfluxExample.ds.props.sds.SdsJobRunnerProperties;
import ru.webfluxExample.ds.props.sds.SdsProjectManagerProperties;
import ru.webfluxExample.ds.props.sds.SdsReportCommunicatorProperties;
import ru.webfluxExample.ds.props.template.MethodicProperties;

@Configuration
@EnableConfigurationProperties({
        MlrAuthUserServiceProperties.class,
        MlrAuthMeTokensProperties.class,
        MlrServerProperties.class,
        MlrServerAuthProperties.class,
        MethodicProperties.class,
        SdsAuthServerProperties.class,
        SdsProjectManagerProperties.class,
        SdsJobRunnerProperties.class,
        SdsReportCommunicatorProperties.class,
        SdsDataSourceProperties.class
})
public class PropsConfig {
}
