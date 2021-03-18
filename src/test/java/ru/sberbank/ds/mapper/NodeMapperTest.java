package ru.webfluxExample.ds.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.webfluxExample.ds.dto.NodeDto;
import ru.webfluxExample.ds.dto.NodeInfoDto;
import ru.webfluxExample.ds.project.manager.api.dto.project.ProjectNodeInfo;
import ru.webfluxExample.ds.util.FileUtils;
import ru.webfluxExample.ds.util.NodeMapper;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NodeMapperTest {
    private final NodeMapper nodeMapper = Mappers.getMapper(NodeMapper.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void shouldSuccessfullyConvertToNodeSettings() throws JsonProcessingException {
        final Map<UUID, String> result = nodeMapper.toNodeSettingsMap(
                objectMapper.readValue(FileUtils.readResource("json/project-manager-nodes-info-response.json"), new TypeReference<List<ProjectNodeInfo>>() {
                })
        );

        assertThat(result.size()).isEqualTo(2);
        result.forEach(
                (uuid, jsonInString) -> assertThat(jsonInString).isEqualTo("settingsJson for ".concat(uuid.toString()))
        );
    }

    @Test
    public void shouldSuccessfullyConvertToNodeInfoDto() throws JsonProcessingException {
        List<ProjectNodeInfo> projectNodeInfoList = objectMapper.readValue(FileUtils.readResource("json/project-manager-nodes-info-response.json"), new TypeReference<List<ProjectNodeInfo>>() {
        });

        projectNodeInfoList.forEach(projectNodeInfo -> {
            NodeInfoDto nodeInfoDto = nodeMapper.toNodeInfoDto(projectNodeInfo);
            assertThat(nodeInfoDto.getId()).isEqualTo(projectNodeInfo.getId());
            assertThat(nodeInfoDto.getSettings()).isEqualTo(projectNodeInfo.getSettingsJson());
        });
    }

    @Test
    public void shouldSuccessfullyConvertToNodeDto() throws JsonProcessingException {
        List<ProjectNodeInfo> projectNodeInfoList = objectMapper.readValue(FileUtils.readResource("json/project-manager-nodes-info-response.json"), new TypeReference<List<ProjectNodeInfo>>() {
        });

        projectNodeInfoList.forEach(projectNodeInfo -> {
            NodeDto nodeDto = nodeMapper.toNodeDto(projectNodeInfo);
            assertThat(nodeDto.getNodeId()).isEqualTo(projectNodeInfo.getId());
            assertThat(nodeDto.getProjectNodeStatus()).isEqualTo(projectNodeInfo.getStatus());
        });
    }
}
