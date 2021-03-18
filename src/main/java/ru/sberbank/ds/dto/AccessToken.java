package ru.webfluxExample.ds.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AccessToken {
    @JsonProperty("user_name")
    String username;
    List<String> scope;
    boolean isActive;
    @JsonProperty("exp")
    Integer expiration;
    List<String> authorities;
    String jti;
    @JsonProperty("client_id")
    String clientId;
}
