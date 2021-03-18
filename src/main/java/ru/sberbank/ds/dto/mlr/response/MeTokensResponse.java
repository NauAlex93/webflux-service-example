package ru.webfluxExample.ds.dto.mlr.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class MeTokensResponse {

    private final String authToken;

    private final String refreshToken;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public MeTokensResponse(
            @JsonProperty("authToken") String authToken,
            @JsonProperty("refreshToken") String refreshToken
    ) {
        this.authToken = authToken;
        this.refreshToken = refreshToken;
    }
}
