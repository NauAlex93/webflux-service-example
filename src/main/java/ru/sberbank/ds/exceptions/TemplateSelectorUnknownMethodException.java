package ru.webfluxExample.ds.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TemplateSelectorUnknownMethodException extends RuntimeException {
    private final String perhapsMethod;
    private final String hint;
}
