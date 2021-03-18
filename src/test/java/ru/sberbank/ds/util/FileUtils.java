package ru.webfluxExample.ds.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The utility class for working with files.
 *
 * @author Andrew Timokhin
 */
public final class FileUtils {

    private FileUtils() {
    }

    /**
     * Read file from resources into line
     *
     * @param pathToFile file path
     * @param charset    encoding to use, null means platform default
     * @return resulting string
     * @throws FileNotFoundException in case the file is not found
     * @throws UncheckedIOException  in case of errors while reading
     */
    public static String readResource(@NotNull String pathToFile, @Nullable Charset charset) {
        try (InputStream inputStream = FileUtils.class.getClassLoader().getResourceAsStream(pathToFile)) {
            if (inputStream == null) {
                throw new FileNotFoundException(String.format("resource %s could not be found", pathToFile));
            }

            return IOUtils.toString(inputStream, charset == null ? StandardCharsets.UTF_8 : charset);
        } catch (IOException exc) {
            throw new UncheckedIOException(exc);
        }
    }

    /**
     * Read file from resources into line
     *
     * @param pathToFile file path
     * @return resulting string
     * @throws FileNotFoundException in case the file is not found
     * @throws UncheckedIOException  in case of errors while reading
     */
    public static String readResource(@NotNull String pathToFile) {
        return readResource(pathToFile, null);
    }

    /**
     * Creating JsonNode object from json file
     *
     * @param pathToFile file path
     * @return resulting JsonNode object
     * @throws UncheckedIOException in case of mapping error
     */
    public static JsonNode createJsonNode(@NotNull String pathToFile) {
        JsonNode jsonNode;
        ObjectMapper mapper = new ObjectMapper();

        try {
            jsonNode = mapper.readTree(readResource(pathToFile));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return jsonNode;
    }

    /**
     * Deserialize json string to object
     *
     * @param  json string to deserialize
     * @param  type of result
     * @return resulting object
     * @throws UncheckedIOException in case of deserialization error
     */
    public static <T> T fromJson(@NotNull String json, @NotNull Class<T> type) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readerFor(type).readValue(json);
        } catch (IOException exc) {
            throw new UncheckedIOException(exc);
        }
    }


    /**
     * Deserialize json string to object
     *
     * @param  json string to deserialize
     * @param  type of result
     * @return resulting object
     * @throws UncheckedIOException in case of deserialization error
     */
    public static <T> T fromJson(@NotNull String json, TypeReference<T> type) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, type);
        } catch (IOException exc) {
            throw new UncheckedIOException(exc);
        }
    }
}