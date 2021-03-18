package ru.webfluxExample.ds.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.SocketUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import ru.webfluxExample.ds.util.FileUtils;

import java.io.IOException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractWebClientTest {

    protected final MockWebServer mockWebServer = new MockWebServer();

    protected final int port = SocketUtils.findAvailableTcpPort(1025, 65535);

    protected final WebClient webClient = WebClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .clientConnector(new ReactorClientHttpConnector(HttpClient.create()))
            .build();
    
    protected final InstanceInfo instanceInfo = new InstanceInfo(
            "id",
            null,
            null,
            null,
            null,
            new InstanceInfo.PortWrapper(true, port),
            new InstanceInfo.PortWrapper(true, port),
            "http://localhost:" + port,
            null,
            null,
            null,
            null,
            null,
            1,
            null,
            "localhost",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
    );
    
    protected final EurekaClient discoveryClient = Mockito.mock(EurekaClient.class);

    @BeforeAll
    protected void setUp() throws IOException {
        mockWebServer.start(port);
    }

    @AfterAll
    protected void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    protected <T> T enqueue(final String path, TypeReference<T> type) {
        String expected = FileUtils.readResource(path);
        T t = FileUtils.fromJson(expected, type);
        mockWebServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(expected)
        );
        return t;
    }

    protected <T> T enqueue(final String path, Class<T> type) {
        String expected = FileUtils.readResource(path);
        T t = FileUtils.fromJson(expected, type);
        mockWebServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(expected)
        );
        return t;
    }

    protected JsonNode enqueue(final String path) {
        JsonNode expected = FileUtils.createJsonNode(path);
        mockWebServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(expected.toString())
        );
        return expected;
    }

    protected void enqueueError() {
        mockWebServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("some error")
                .setResponseCode(500)
        );
    }
}
