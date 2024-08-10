package dev.simonfischer.profiler.services.user;


import dev.simonfischer.profiler.models.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    User getUser();

    void updateUser(User user, MultipartFile image);

    byte[] getAvatar(String imageId);
}