package dev.simonfischer.profiler.models.dto.keycloak;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class KeycloakUser {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private boolean emailVerified;
    private Map<String, List<String>> attributes;
}

