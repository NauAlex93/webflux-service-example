package ru.webfluxExample.ds.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.webfluxExample.ds.dto.CreateProjectRequest;
import ru.webfluxExample.ds.dto.NodeInfoDto;
import ru.webfluxExample.ds.dto.ProjectShortInfoDto;
import ru.webfluxExample.ds.project.manager.api.dto.project.ProjectDto;
import ru.webfluxExample.ds.project.manager.api.dto.project.SaveAsProjectRequest;
import ru.webfluxExample.ds.project.manager.api.dto.workflow.Node;

@Mapper(componentModel = "spring")
public abstract class ProjectMapper {

    @Mapping(target = "sourceId", source = "sourceProjectId")
    @Mapping(target = "copyReports", ignore = true)
    public abstract SaveAsProjectRequest toSaveAsProjectRequest(CreateProjectRequest createProjectRequest);

    @Mapping(target = "nodes", source = "workflow.nodes")
    public abstract ProjectShortInfoDto toProjectInfoDto(ProjectDto projectDto);

    @Mapping(target = "settings", ignore = true)
    public abstract NodeInfoDto nodeToNodeInfoDto(Node node);
}
