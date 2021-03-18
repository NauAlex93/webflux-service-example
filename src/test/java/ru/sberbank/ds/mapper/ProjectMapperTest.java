package ru.webfluxExample.ds.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.webfluxExample.ds.dto.CreateProjectRequest;
import ru.webfluxExample.ds.dto.ProjectShortInfoDto;
import ru.webfluxExample.ds.project.manager.api.dto.project.ProjectDto;
import ru.webfluxExample.ds.project.manager.api.dto.project.SaveAsProjectRequest;
import ru.webfluxExample.ds.util.FileUtils;
import ru.webfluxExample.ds.util.ProjectMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectMapperTest {
    private final ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    public void shouldSuccessfullyConvertToProjectRequest() {
        final UUID sourceProjectId = UUID.randomUUID();
        final CreateProjectRequest createProjectRequest = new CreateProjectRequest()
                .setName("Target project")
                .setDescription("Target description")
                .setSourceProjectId(sourceProjectId);

        final SaveAsProjectRequest projectRequest = projectMapper.toSaveAsProjectRequest(createProjectRequest);

        assertThat(createProjectRequest.getName()).isEqualTo(projectRequest.getName());
        assertThat(createProjectRequest.getDescription()).isEqualTo(projectRequest.getDescription());
        assertThat(createProjectRequest.getSourceProjectId()).isEqualTo(projectRequest.getSourceId());
    }

    @Test
    public void shouldSuccessfullyConvertToProjectInfoDto() throws JsonProcessingException {
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        final ProjectDto projectDto = objectMapper.readValue(FileUtils.readResource("json/api-save-project-response-with-nodes.json"), ProjectDto.class);
        final ProjectShortInfoDto projectShortInfoDto = projectMapper.toProjectInfoDto(projectDto);

        assertThat(projectDto.getId()).isEqualTo(projectShortInfoDto.getId());
        assertThat(projectDto.getName()).isEqualTo(projectShortInfoDto.getName());
        assertThat(projectDto.getDescription()).isEqualTo(projectShortInfoDto.getDescription());
        assertThat(projectShortInfoDto.getNodes()).isNotNull();
        assertThat(projectShortInfoDto.getNodes().size()).isEqualTo(2);
    }
}
