package ru.webfluxExample.ds.exceptions.mlr;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MlrModelEntityNotFoundException extends RuntimeException {
    private final long modelId;
}
