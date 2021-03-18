package ru.webfluxExample.ds.exceptions.mlr;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MlrModelNotInDevelopmentStatusException extends RuntimeException {
    private final String status;
}
