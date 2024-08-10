package dev.simonfischer.profiler.models.dto.keycloak;

import lombok.Data;
import java.util.UUID;

@Data
public class KeycloakUserDto {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private boolean emailVerified;
    private KeycloakAttributesDto attributes;
}

