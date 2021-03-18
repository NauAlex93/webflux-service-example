package ru.webfluxExample.ds.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(chain = true)
public class CreateProjectRequest {
    private String name;
    private String description;
    private UUID sourceProjectId;
    private boolean groupShared;
    private UUID userGroupId;
}
