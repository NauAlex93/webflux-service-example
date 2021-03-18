package ru.webfluxExample.ds.dto.mlr.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnstableEntitiesResponse {

    private List<Entity> entities = new ArrayList<>();

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Entity {

        private final long id;

        private final String status;

        private final List<Param> params;

        @JsonCreator
        public Entity(@JsonProperty("id") long id, @JsonProperty("status") String status, @JsonProperty("params") List<Param> params) {
            this.id = id;
            this.status = status;
            this.params = params;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Param {

            private final String id;

            private final List<String> values;

            @JsonCreator
            public Param(@JsonProperty("id") String id, @JsonProperty("values") List<String> values) {
                this.id = id;
                this.values = values;
            }
        }
    }

}
