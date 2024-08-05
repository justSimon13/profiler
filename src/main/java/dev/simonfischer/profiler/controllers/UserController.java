package dev.simonfischer.profiler.controllers;

import dev.simonfischer.profiler.services.user.UserService;
import dev.simonfischer.profiler.models.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<UserDto> getUser() {
        UserDto userDto = userService.getUser();
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> updateUser(@RequestPart("userData") UserDto user, @RequestPart(value = "image", required = false) MultipartFile image) {
        userService.updateUser(user, image);
        return new ResponseEntity<>("User updated successfully", HttpStatus.OK);
    }

    @RequestMapping(value = "/avatar/{imageId}", method = RequestMethod.GET, produces = {"image/jpeg", "image/png"})
    public ResponseEntity<byte[]> getAvatar(@PathVariable String imageId) {
        byte[] file = userService.getAvatar(imageId);
        return new ResponseEntity<>(file, HttpStatus.OK);
    }
}
