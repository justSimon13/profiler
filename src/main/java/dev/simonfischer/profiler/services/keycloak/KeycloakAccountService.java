package dev.simonfischer.profiler.services.keycloak;

import dev.simonfischer.profiler.models.dto.keycloak.KeycloakUser;

public interface KeycloakAccountService {

    KeycloakUser getKeycloakUser();

    void updateKeycloakUser(KeycloakUser keycloakUser);

    boolean isAdmin();

    boolean isAuthenticated();
}
