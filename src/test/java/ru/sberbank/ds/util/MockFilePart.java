package ru.webfluxExample.ds.util;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * The utility class for working with mock for FilePart.
 */
public class MockFilePart implements FilePart {

    private final String filename;
    private final String name;
    private final HttpHeaders headers;
    private final Flux<DataBuffer> content;

    public MockFilePart() {
        this("filename", "name", HttpHeaders.EMPTY, "content");
    }

    public MockFilePart(String filename, String name, HttpHeaders headers, String content) {
        this.filename = filename;
        this.name = name;
        this.headers = headers;
        this.content = DataBufferUtils.read(
                new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8)),
                new DefaultDataBufferFactory(),
                1024
        );
    }

    @Override
    public String filename() {
        return filename;
    }

    @Override
    public Mono<Void> transferTo(Path dest) {
        return null;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public HttpHeaders headers() {
        return headers;
    }

    @Override
    public Flux<DataBuffer> content() {
        return content;
    }
}
