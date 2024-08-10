package dev.simonfischer.profiler.services.profile;

import dev.simonfischer.profiler.models.business.Profile;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {
    byte[] getUserProfilePdf();
    byte[] getPublicProfilePdf(Profile profile, MultipartFile image);
}
