package ru.webfluxExample.ds.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.webfluxExample.ds.dto.NodeDto;
import ru.webfluxExample.ds.dto.NodeInfoDto;
import ru.webfluxExample.ds.project.manager.api.dto.project.ProjectNodeInfo;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class NodeMapper {

    public Map<UUID, String> toNodeSettingsMap(List<ProjectNodeInfo> projectNodeInfoList) {
        return projectNodeInfoList.stream()
                .map(this::toNodeInfoDto)
                .collect(Collectors.toMap(NodeInfoDto::getId, NodeInfoDto::getSettings));
    }

    @Mapping(target = "name", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "settings", source = "settingsJson")
    public abstract NodeInfoDto toNodeInfoDto(ProjectNodeInfo projectNodeInfo);

    @Mapping(target = "nodeId", source = "id")
    @Mapping(target = "projectNodeStatus", source = "status")
    public abstract NodeDto toNodeDto(ProjectNodeInfo projectNodeInfo);
}
