package ru.webfluxExample.ds.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ProjectRunStatus {
    private UUID projectId;
    private ProjectExecutionStatus projectExecutionStatus;
    private List<NodeDto> nodes;
}