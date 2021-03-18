package ru.webfluxExample.ds.dto.mlr;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UnstableEntitiesPredicate {

    private final String type;

    private final String paramId;

    private final String value;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public UnstableEntitiesPredicate(
            @JsonProperty("$type") String type,
            @JsonProperty("paramId") String paramId,
            @JsonProperty("value") String value
    ) {
        this.type = type;
        this.paramId = paramId;
        this.value = value;
    }
}
