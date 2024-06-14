package dev.simonfischer.profiler.models.dto;

import lombok.Data;

import java.util.Map;

@Data
public class AttributesDto {
    private String bornOn;
    private String location;
    private String description;
    private String avatar;
    private Map<String, Object> links;
}
