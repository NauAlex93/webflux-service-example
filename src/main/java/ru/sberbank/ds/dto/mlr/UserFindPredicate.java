package ru.webfluxExample.ds.dto.mlr;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserFindPredicate {

    private final String type;

    private final UserFindLogin login;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public UserFindPredicate(
            @JsonProperty("$type") String type,
            @JsonProperty("login") UserFindLogin login
    ) {
        this.type = type;
        this.login = login;
    }

    public UserFindPredicate(String username) {
        this.type = "HasLogin";
        this.login = new UserFindLogin(
                "IpaLogin", username
        );
    }
}
