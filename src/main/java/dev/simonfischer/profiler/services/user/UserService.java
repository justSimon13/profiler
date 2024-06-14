package dev.simonfischer.profiler.services.user;

import dev.simonfischer.profiler.models.dto.UserDto;

public interface UserService {

    UserDto getUser() throws RuntimeException;

    void updateUser(UserDto user) throws RuntimeException;

}