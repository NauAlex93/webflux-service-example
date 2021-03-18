package ru.webfluxExample.ds.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class NodeInfoDto {
    private UUID id;
    private String name;
    private String description;
    private String settings;
}
