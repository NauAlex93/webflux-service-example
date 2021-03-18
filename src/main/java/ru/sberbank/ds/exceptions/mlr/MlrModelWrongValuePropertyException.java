package ru.webfluxExample.ds.exceptions.mlr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class MlrModelWrongValuePropertyException extends RuntimeException {
    private final String parameterName;
    private final String currentValue;
    private final String expectedValue;

    public MlrModelWrongValuePropertyException(String parameterName, String currentValue) {
        this.parameterName = parameterName;
        this.currentValue = currentValue;
        this.expectedValue = "";
    }
}
