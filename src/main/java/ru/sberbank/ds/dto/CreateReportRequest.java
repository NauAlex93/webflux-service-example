package ru.webfluxExample.ds.dto;

import java.util.UUID;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class CreateReportRequest {

    @NotNull
    private UUID rootProjectId;

    @NotBlank
    private String createdBy;
}