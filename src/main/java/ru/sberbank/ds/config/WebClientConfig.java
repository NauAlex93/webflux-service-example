package ru.webfluxExample.ds.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@Profile("!local")
public class WebClientConfig {

    @Value("${server.ssl.key-store}")
    String keyStorePath;

    @Value("${server.ssl.key-store-password}")
    String keyStorePass;

    @Value("${server.ssl.key-alias}")
    String keyStoreAlias;

    /**
     * WebClient for connecting to SDS and Model Library
     * without client-side certificate.
     *
     * @return webClient without client-side ssl
     * @throws SSLException if fails to create SslContext
     */
    @Bean
    public WebClient webClient() throws SSLException {
        SslContext context = SslContextBuilder.forClient()
                .build();

        HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(context));

        return WebClient
                .builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(logRequest())
                .build();
    }

    /**
     * WebClient for connecting to Model Library with client-side
     * certificate configuration.
     *
     * @return webClient with client-side ssl
     */
    @Bean
    public WebClient mlrWebClient() {
        HttpClient httpClient = HttpClient.create()
                .secure(t -> t.sslContext(clientCertificateSslContext()));

        return WebClient
                .builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(logRequest())
                .build();
    }

    private SslContext clientCertificateSslContext() {
        try (FileInputStream keyStoreInputStream = new FileInputStream(ResourceUtils.getFile(keyStorePath))) {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(keyStoreInputStream, keyStorePass.toCharArray());
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyStoreAlias, keyStorePass.toCharArray());
            Certificate[] certChain = keyStore.getCertificateChain(keyStoreAlias);
            X509Certificate[] x509CertificateChain = Arrays.stream(certChain)
                    .map(certificate -> (X509Certificate) certificate)
                    .collect(Collectors.toList())
                    .toArray(new X509Certificate[certChain.length]);
            return SslContextBuilder.forClient()
                    .keyManager(privateKey, keyStorePass, x509CertificateChain)
                    .build();
        } catch (Exception e) {
            log.error("Error creating 2-Way TLS WebClient. Check key-store: {}", keyStorePath, e);
            throw new IllegalStateException(e);
        }
    }

    // This method returns filter function which will log request data
    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            log.info("Request body: {}", clientRequest.body());
            log.info("Request headers");
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            log.info("Request cookies");
            clientRequest.cookies().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return Mono.just(clientRequest);
        });
    }
}
