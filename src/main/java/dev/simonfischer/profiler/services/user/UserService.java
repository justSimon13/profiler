package dev.simonfischer.profiler.services.user;

import dev.simonfischer.profiler.models.dto.UserDto;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserDto getUser();

    void updateUser(UserDto user, MultipartFile image);

    byte[] getAvatar(String imageId);
}