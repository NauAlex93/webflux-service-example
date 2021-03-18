package ru.webfluxExample.ds.dto.mlr.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import ru.webfluxExample.ds.dto.mlr.UserFindLogin;

import java.util.List;

@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserFindResponse {

    private final List<UserFindUser> users;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public UserFindResponse(@JsonProperty("users") List<UserFindUser> users) {
        this.users = users;
    }

    @Getter
    @ToString
    public static class UserFindUser {

        private final List<UserFindLogin> logins;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public UserFindUser(@JsonProperty("logins") List<UserFindLogin> logins) {
            this.logins = logins;
        }
    }
}
