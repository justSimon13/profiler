package dev.simonfischer.profiler.services.keycloak;

import dev.simonfischer.profiler.models.dto.keycloak.KeycloakUserDto;

public interface KeycloakAccountService {

    KeycloakUserDto getKeycloakUser();

    void updateKeycloakUser(KeycloakUserDto keycloakUserDto);

    boolean isAdmin();

    boolean isAuthenticated();
}
