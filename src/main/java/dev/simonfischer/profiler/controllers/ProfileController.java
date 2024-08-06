package dev.simonfischer.profiler.controllers;

import dev.simonfischer.profiler.models.dto.ProfileDto;
import dev.simonfischer.profiler.services.profile.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping(value = "/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @RequestMapping(value = "/public", method = RequestMethod.POST, consumes = {"multipart/form-data"}, produces = "application/pdf")
    public ResponseEntity<byte[]> downloadPublicProfile(@RequestPart("profilePublic") ProfileDto profileDto, @RequestPart(value = "image") MultipartFile image) {
        byte[] file = profileService.getPublicProfilePdf(profileDto, image);
        return new ResponseEntity<>(file, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/pdf")
    public ResponseEntity<byte[]> downloadProfilePdf() {
        byte[] file = profileService.getUserProfilePdf();
        return new ResponseEntity<>(file, HttpStatus.OK);
    }
}
