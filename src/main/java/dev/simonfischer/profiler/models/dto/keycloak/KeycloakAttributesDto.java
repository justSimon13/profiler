package dev.simonfischer.profiler.models.dto.keycloak;

import lombok.Data;

import java.util.List;

@Data
public class KeycloakAttributesDto {

    private List<String> description;
    private List<String> location;
    private List<String> avatar;
    private List<String> bornOn;
    private List<String> links;
}

