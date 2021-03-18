package ru.webfluxExample.ds.dto.mlr.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import ru.webfluxExample.ds.dto.mlr.UnstableEntitiesPredicate;

import java.util.Collections;
import java.util.List;

@Getter
@ToString
public class UnstableEntitiesRequest {

    private final String entityType;

    private final List<UnstableEntitiesPredicate> predicates;

    private final List<String> order;

    private final Integer limit;

    private final Integer offset;

    public UnstableEntitiesRequest(
            String entityType,
            UnstableEntitiesPredicate predicate
    ) {
        this.entityType = entityType;
        this.predicates = Collections.singletonList(predicate);
        this.order = Collections.emptyList();
        this.limit = 1;
        this.offset = 0;
    }

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public UnstableEntitiesRequest(
            @JsonProperty("entityType") String entityType,
            @JsonProperty("predicates") List<UnstableEntitiesPredicate> predicates,
            @JsonProperty("order") List<String> order,
            @JsonProperty("limit") Integer limit,
            @JsonProperty("offset") Integer offset
    ) {
        this.entityType = entityType;
        this.predicates = predicates;
        this.order = order;
        this.limit = limit;
        this.offset = offset;
    }
}
