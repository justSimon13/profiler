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
import org.apache.commons.io.IOUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.*;


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

    public void updateUser(UserDto user, MultipartFile image) {
        KeycloakUser keycloakUser = modelMapper.map(user, KeycloakUser.class);
        Map<String, List<String>> attributeMap = new HashMap<>();

        attributeMap.put("description", Collections.singletonList(user.getAttributes().getDescription()));
        attributeMap.put("location", Collections.singletonList(user.getAttributes().getLocation()));
        attributeMap.put("bornOn", Collections.singletonList(user.getAttributes().getBornOn()));
        attributeMap.put("avatar", Collections.singletonList(uploadAvatar(image)));

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

    public byte[] getAvatar(String imageId) {
        byte[] avatar;
        InputStream avatarStream;
        avatarStream = Objects.requireNonNull(getClass().getResourceAsStream("/assets/profileImages/" + imageId));
        try {
            avatar = IOUtils.toByteArray(avatarStream);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new InternalServerException("There was an error getting the avatar");
        }

        return avatar;
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

    private String uploadAvatar(MultipartFile image) {
        String imageId = String.valueOf(new Random().nextInt(100000));
        String imagePath =
                Objects.requireNonNull(getClass().getResource("/assets/profileImages/")).getPath() + imageId + ".jpeg";

        try {
            File file = new File(imagePath);
            image.transferTo(file);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new InternalServerException("There was an error uploading the avatar");
        }

        return imagePath;
    }
}
