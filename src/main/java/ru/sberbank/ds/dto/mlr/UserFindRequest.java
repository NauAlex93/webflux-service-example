package ru.webfluxExample.ds.dto.mlr;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserFindRequest {

    private final UserFindPredicate predicate;

    private final UserFindPaging paging;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public UserFindRequest(
            @JsonProperty("predicate") UserFindPredicate predicate,
            @JsonProperty("paging") UserFindPaging paging
    ) {
        this.predicate = predicate;
        this.paging = paging;
    }

    public UserFindRequest(String username) {
        this.predicate = new UserFindPredicate(username);
        this.paging = new UserFindPaging(0, 1);
    }

    @Getter
    @ToString
    public static class UserFindPaging {

        private final int skip;

        private final int take;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public UserFindPaging(
                @JsonProperty("skip") int skip,
                @JsonProperty("take") int take
        ) {
            this.skip = skip;
            this.take = take;
        }
    }
}
