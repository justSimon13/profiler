package dev.simonfischer.profiler.services.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.simonfischer.profiler.models.dto.AttributesDto;
import dev.simonfischer.profiler.models.dto.UserDto;
import dev.simonfischer.profiler.models.enumeratio.AccountRole;
import dev.simonfischer.profiler.services.keycloak.KeycloakAccountService;
import dev.simonfischer.profiler.models.dto.keycloak.KeycloakUser;
import dev.simonfischer.profiler.models.exception.entity.AuthenticationException;
import dev.simonfischer.profiler.models.exception.entity.InternalServerException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class UserServiceImpl implements UserService {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private KeycloakAccountService keycloakAccountService;

    @Autowired
    private ModelMapper modelMapper;


    public UserDto getUser() {
        if (keycloakAccountService.isAuthenticated()) {
            KeycloakUser keycloakUser = keycloakAccountService.getKeycloakUser();
            UserDto user = mapKeycloakUserToUserDto(keycloakUser);

            if (keycloakAccountService.isAdmin()) {
                user.setAccountRole(AccountRole.ADMIN);
            } else {
                user.setAccountRole(AccountRole.USER);
            }

            return user;
        }

        throw new AuthenticationException("User is not authenticated");
    }

    public void updateUser(UserDto user) {
        KeycloakUser keycloakUser = modelMapper.map(user, KeycloakUser.class);
        Map<String, List<String>> attributeMap = new HashMap<>();

        attributeMap.put("avatar", Collections.singletonList(user.getAttributes().getAvatar()));
        attributeMap.put("description", Collections.singletonList(user.getAttributes().getDescription()));
        attributeMap.put("location", Collections.singletonList(user.getAttributes().getLocation()));
        attributeMap.put("bornOn", Collections.singletonList(user.getAttributes().getBornOn()));

        Map<String, Object> links = user.getAttributes().getLinks();

        try {
            String linksJson = mapper.writeValueAsString(links);
            attributeMap.put("links", Collections.singletonList(linksJson));
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }

        keycloakUser.setAttributes(attributeMap);
        try {
            keycloakAccountService.updateKeycloakUser(keycloakUser);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            throw new InternalServerException("There was an error saving the keycloak user");
        }
    }

    public UserDto mapKeycloakUserToUserDto(KeycloakUser keycloakUser) {
        UserDto userDto = new UserDto();
        userDto.setId(keycloakUser.getId());
        userDto.setUsername(keycloakUser.getUsername());
        userDto.setFirstName(keycloakUser.getFirstName());
        userDto.setLastName(keycloakUser.getLastName());
        userDto.setEmail(keycloakUser.getEmail());

        AttributesDto attributesDto = new AttributesDto();
        if (keycloakUser.getAttributes() != null) {
            attributesDto.setBornOn(getFirstItem(keycloakUser.getAttributes().get("bornOn")));
            attributesDto.setLocation(getFirstItem(keycloakUser.getAttributes().get("location")));
            attributesDto.setDescription(getFirstItem(keycloakUser.getAttributes().get("description")));
            attributesDto.setAvatar(getFirstItem(keycloakUser.getAttributes().get("avatar")));

            try {
                String linksJson = getFirstItem(keycloakUser.getAttributes().get("links"));
                if (linksJson != null) {
                    Map<String, Object> linksMap = mapper.readValue(linksJson, new TypeReference<>() {});
                    attributesDto.setLinks(linksMap);
                }
            } catch (JsonProcessingException e) {
                System.err.println(e.getMessage());
                throw new InternalServerException("There was an error parsing the links JSON");
            }
        }
        userDto.setAttributes(attributesDto);

        return userDto;
    }

    private String getFirstItem(List<String> list) {
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }
}
