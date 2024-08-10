package dev.simonfischer.profiler.models.entity;

import lombok.Data;

import java.util.List;


@Data
public class UserAttributes {
    private String description;
    private String location;
    private String avatar;
    private String bornOn;
    private List<UserAttributesLinks> links;
}
