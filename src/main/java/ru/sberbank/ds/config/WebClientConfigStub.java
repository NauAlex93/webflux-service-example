package ru.webfluxExample.ds.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Profile("local")
@Configuration
public class WebClientConfigStub {

    @Bean
    WebClient webClient() {
        return WebClient.create();
    }

    @Bean
    WebClient mlrWebClient() {
        return WebClient.create();
    }

}