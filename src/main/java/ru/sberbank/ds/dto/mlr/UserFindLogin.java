package ru.webfluxExample.ds.dto.mlr;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserFindLogin {

    private final String type;

    private final String username;


    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public UserFindLogin(
            @JsonProperty("$type") String type,
            @JsonProperty("username") String username
    ) {
        this.type = type;
        this.username = username;
    }
}
