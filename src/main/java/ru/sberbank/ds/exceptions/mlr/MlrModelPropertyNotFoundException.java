package ru.webfluxExample.ds.exceptions.mlr;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MlrModelPropertyNotFoundException extends RuntimeException {
    private final String parameterName;
}
