package ru.webfluxExample.ds.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.webfluxExample.ds.project.manager.api.dto.project.ProjectNodeStatus;

import java.util.UUID;

@Data
@Accessors(chain = true)
public class NodeDto {
    private UUID nodeId;
    private ProjectNodeStatus projectNodeStatus;
}
