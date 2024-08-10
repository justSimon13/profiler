package dev.simonfischer.profiler.services.user;

import dev.simonfischer.profiler.models.dto.keycloak.KeycloakAttributesDto;
import dev.simonfischer.profiler.models.entity.User;
import dev.simonfischer.profiler.models.entity.UserAttributes;
import dev.simonfischer.profiler.models.entity.UserAttributesLinks;
import dev.simonfischer.profiler.models.exception.entity.ItemNotFoundException;
import dev.simonfischer.profiler.services.keycloak.KeycloakAccountService;
import dev.simonfischer.profiler.models.dto.keycloak.KeycloakUserDto;
import dev.simonfischer.profiler.models.exception.entity.AuthenticationException;
import dev.simonfischer.profiler.models.exception.entity.InternalServerException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;


@Service
public class UserServiceImpl implements UserService {

    private final String IMAGES_PATH = "src/main/resources/static/images";

    @Autowired
    private KeycloakAccountService keycloakAccountService;

    @Value("${server.url}")
    private String serverUrl;


    public User getUser() {
        if (keycloakAccountService.isAuthenticated()) {
            KeycloakUserDto keycloakUserDto = keycloakAccountService.getKeycloakUser();

            return mapKeycloakUserToUser(keycloakUserDto);
        }

        throw new AuthenticationException("User is not authenticated");
    }

    public void updateUser(User user, MultipartFile image) {
        if (image != null) {
           user.getAttributes().setAvatar(uploadAvatar(image, user.getAttributes().getAvatar()));
        }

        try {
            KeycloakUserDto keycloakUserDto = mapUserToKeycloakUser(user);

            keycloakAccountService.updateKeycloakUser(keycloakUserDto);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            throw new InternalServerException("There was an error saving the keycloak user");
        }
    }

    public byte[] getAvatar(String imageId) {
        Path uploadPath = Path.of(IMAGES_PATH);
        Path newImagePath = uploadPath.resolve(imageId);

        if (Files.exists(newImagePath)) {
            try {
                return Files.readAllBytes(newImagePath);
            } catch (IOException e) {
                System.err.println(e.getMessage());
                throw new InternalServerException("There was an error reading the avatar image");
            }

        } else {
            throw new ItemNotFoundException("Image " + imageId + " not found");
        }
    }

    private String uploadAvatar(MultipartFile image, String lastImgUrl) {
        String serverUrlTmp = serverUrl + "user/avatar/";
        String uniqueFileName = UUID.randomUUID() + "_" + image.getOriginalFilename();

        Path uploadPath = Path.of(IMAGES_PATH);
        Path newImagePath = uploadPath.resolve(uniqueFileName);

        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            if (lastImgUrl != null && !lastImgUrl.isEmpty()) {
                String lastImageId = lastImgUrl.substring(serverUrlTmp.length());
                Path oldImagePath = uploadPath.resolve(lastImageId);

                if (Files.exists(oldImagePath)) {
                    Files.delete(oldImagePath);
                }
            }

            Files.copy(image.getInputStream(), newImagePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new InternalServerException("There was an error uploading the avatar");
        }

        return serverUrlTmp +  uniqueFileName;
    }

    private User mapKeycloakUserToUser(KeycloakUserDto keycloakUserDto) {
        User user = new User();
        user.setId(keycloakUserDto.getId());
        user.setUsername(keycloakUserDto.getUsername());
        user.setFirstName(keycloakUserDto.getFirstName());
        user.setLastName(keycloakUserDto.getLastName());
        user.setEmail(keycloakUserDto.getEmail());

        UserAttributes userAttributes = new UserAttributes();
        if (keycloakUserDto.getAttributes() != null) {
            userAttributes.setBornOn(keycloakUserDto.getAttributes().getBornOn().stream().findFirst().orElse(null));
            userAttributes.setDescription(keycloakUserDto.getAttributes().getDescription().stream().findFirst().orElse(null));
            userAttributes.setLocation(keycloakUserDto.getAttributes().getLocation().stream().findFirst().orElse(null));
            userAttributes.setAvatar(keycloakUserDto.getAttributes().getAvatar().stream().findFirst().orElse(null));

            String linksStringArray = keycloakUserDto.getAttributes().getLinks().stream().findFirst().orElse(Arrays.toString(new String[0]));
            JSONArray jsonArray = new JSONArray(linksStringArray);
            List<UserAttributesLinks> userAttributesLinks = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("Name");
                String link = jsonObject.getString("link");
                userAttributesLinks.add(new UserAttributesLinks(name, link));
            }

            userAttributes.setLinks(userAttributesLinks);
            user.setAttributes(userAttributes);
        }

        return user;
    }

    private KeycloakUserDto mapUserToKeycloakUser(User user) {
        KeycloakUserDto keycloakUserDto = new KeycloakUserDto();
        keycloakUserDto.setId(user.getId());
        keycloakUserDto.setUsername(user.getUsername());
        keycloakUserDto.setFirstName(user.getFirstName());
        keycloakUserDto.setLastName(user.getLastName());
        keycloakUserDto.setEmail(user.getEmail());

        KeycloakAttributesDto attributes = new KeycloakAttributesDto();
        attributes.setBornOn(Collections.singletonList(user.getAttributes().getBornOn()));
        attributes.setDescription(Collections.singletonList(user.getAttributes().getDescription()));
        attributes.setLocation(Collections.singletonList(user.getAttributes().getLocation()));
        attributes.setAvatar(Collections.singletonList(user.getAttributes().getAvatar()));
        attributes.setLinks(Collections.singletonList(user.getAttributes().getLinks().toString()));

        keycloakUserDto.setAttributes(attributes);

        return keycloakUserDto;
    }
}
