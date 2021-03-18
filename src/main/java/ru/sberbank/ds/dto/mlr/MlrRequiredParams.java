package ru.webfluxExample.ds.dto.mlr;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MlrRequiredParams {
    private final long modelId;
    private final boolean isBlackBox;
    private final String cdsBlock;
    private final String developmentBlock;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public MlrRequiredParams(
            @JsonProperty(value = "MODEL_ID") long modelId,
            @JsonProperty(value = "CDS_BLOCK") String cdsBlock,
            @JsonProperty(value = "DEVELOPMENT_BLOCK") String developmentBlock
    ) {
        this.isBlackBox = true;
        this.developmentBlock = developmentBlock;
        this.cdsBlock = cdsBlock;
        this.modelId = modelId;
    }

    public MlrRequiredParams(
            long modelId,
            boolean isBlackBox,
            String cdsBlock,
            String developmentBlock
    ) {
        this.isBlackBox = isBlackBox;
        this.developmentBlock = developmentBlock;
        this.cdsBlock = cdsBlock;
        this.modelId = modelId;
    }
}
