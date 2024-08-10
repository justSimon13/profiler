package dev.simonfischer.profiler.services.keycloak;

import dev.simonfischer.profiler.models.dto.keycloak.KeycloakUserDto;
import dev.simonfischer.profiler.models.exception.entity.AuthenticationException;
import dev.simonfischer.profiler.models.exception.entity.InternalServerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class KeycloakAccountServiceImpl implements KeycloakAccountService {

    @Value("${auth-provider.realm}")
    private String realm;
    @Value("${auth-provider.uri}")
    private String keycloakUrl;

    private static final String REALM_BASE_PATH = "/auth/realms/";
    private static final String ACCOUNT_PATH = "/account";

    private final RestTemplate restTemplate = new RestTemplate();


    public KeycloakUserDto getKeycloakUser() {
        HttpHeaders headers = getHttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        URI url = getRequestUrl();
        ResponseEntity<KeycloakUserDto> response;

        response = restTemplate.exchange(url, HttpMethod.GET, entity, KeycloakUserDto.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            System.err.println("HTTP Status Code: " + response.getStatusCode());
            System.err.println(response.getBody());
            throw new InternalServerException("There was an error retrieving the keycloak user");
        }

        return response.getBody();
    }

    public void updateKeycloakUser(KeycloakUserDto keycloakUserDto) {
        HttpHeaders headers = getHttpHeaders();
        HttpEntity<KeycloakUserDto> request = new HttpEntity<>(keycloakUserDto, headers);
        ResponseEntity<String> response = restTemplate.exchange(getRequestUrl(), HttpMethod.POST, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            System.err.println("HTTP Status Code: " + response.getStatusCode());
            System.err.println(response.getBody());
            throw new InternalServerException("There was an Error updating the keycloak user");
        }
    }

    public boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_admin"));
    }

    public boolean isAuthenticated() {
        Authentication auth = getAuthentication();
        return auth != null && auth.isAuthenticated();
    }

    private URI getRequestUrl() {
        try {
            return new URI(keycloakUrl + REALM_BASE_PATH + realm + ACCOUNT_PATH);
        } catch (URISyntaxException e) {
            System.err.println(e.getMessage());
            throw new InternalServerException("There was an error building the request URL.");
        }
    }

    private HttpHeaders getHttpHeaders() {
        String token = "";

        Authentication authentication = getAuthentication();

        if (authentication == null) {
            System.err.println("Authentication is null. User not authenticated.");
            throw new AuthenticationException("Authentication is null.  User not authenticated.");
        }

        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            token = jwtAuthenticationToken.getToken().getTokenValue();
        }

        if (token == null || token.isEmpty()) {
            System.err.println("Token value is null or empty.");
            throw new AuthenticationException("Token value is null or empty.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        return headers;
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}