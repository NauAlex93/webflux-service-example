package ru.webfluxExample.ds.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = false)
public class BasicApiResponseException extends RuntimeException {

    private final String responseBody;

    private final HttpStatus httpStatus;
}
