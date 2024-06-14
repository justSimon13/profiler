package dev.simonfischer.profiler.services.profile.template;

import dev.simonfischer.profiler.models.dto.ProfileDto;

import java.io.File;
import java.io.IOException;

public interface TemplateService {

    /**
     * Replaces placeholders in an HTML string with values from a ProfilePublicDto object.
     *
     * @param html The HTML string to replace placeholders in.
     * @param profileDto The ProfilePublicDto object containing the values for replacement.
     * @return The updated HTML string with the placeholders replaced by the corresponding values.
     */
    String replacePlaceholder(String html, ProfileDto profileDto);
    String htmlToXhtml(File file) throws IOException;
}
