package ru.webfluxExample.ds.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ProjectShortInfoDto {
    @NotBlank
    protected String name;
    protected String description;
    private UUID id;
    private List<NodeInfoDto> nodes;
}
