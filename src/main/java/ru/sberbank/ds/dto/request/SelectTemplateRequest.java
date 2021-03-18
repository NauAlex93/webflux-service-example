package ru.webfluxExample.ds.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import ru.webfluxExample.ds.dto.mlr.MlrRequiredParams;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
public final class SelectTemplateRequest {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public SelectTemplateRequest(
            @JsonProperty("MODEL_ID") long modelId,
            @JsonProperty("DATA_PARAMS_DATA_TYPE") String dataType,
            @JsonProperty("DATA_PARAMS_TASK_TYPE") String taskType,
            @JsonProperty("DEV_NEEDS_NO_KEY_METRICS") boolean devNeedsNoKeyMetrics,
            @JsonProperty("METRICS_NAME") String metricsName,
            @JsonProperty("SAMPLE") Sample sample,
            @JsonProperty("MLR_REQUIRED_PARAMS") MlrRequiredParams mlrRequiredParams) {
        this.modelId = modelId;
        this.dataType = dataType;
        this.taskType = taskType;
        this.devNeedsNoKeyMetrics = devNeedsNoKeyMetrics;
        this.metricsName = metricsName;
        this.sample = sample;
        this.mlrRequiredParams = mlrRequiredParams;
    }

    private final long modelId;

    @NotBlank
    private final String dataType;

    @NotBlank
    private final String taskType;

    private final boolean devNeedsNoKeyMetrics;

    @NotBlank
    private final String metricsName;

    @NotNull
    private final Sample sample;

    @NotNull
    private final MlrRequiredParams mlrRequiredParams;

    @Getter
    public static class Sample {

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public Sample(
                @JsonProperty(value = "FOR_DEVELOPMENT") Set<SampleDevelopmentType> forDevelopment,
                @JsonProperty(value = "FOR_VALIDATION") Set<SampleValidationType> forValidation
        ) {
            this.forDevelopment = forDevelopment;
            this.forValidation = forValidation;
        }

        private final Set<SampleDevelopmentType> forDevelopment;
        private final Set<SampleValidationType> forValidation;
    }
}
