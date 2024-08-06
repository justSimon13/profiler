package dev.simonfischer.profiler.services.profile;

import dev.simonfischer.profiler.models.dto.*;
import dev.simonfischer.profiler.models.exception.entity.InternalServerException;
import dev.simonfischer.profiler.services.knowledge.KnowledgeService;
import dev.simonfischer.profiler.services.profile.pdf.PDFService;
import dev.simonfischer.profiler.services.profile.template.TemplateService;
import dev.simonfischer.profiler.services.project.ProjectService;
import dev.simonfischer.profiler.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;


@Service
public class ProfileService {

    @Autowired
    private TemplateService templateService;

    @Autowired
    private PDFService pdfService;

    @Autowired
    private UserService userService;

    @Autowired
    private KnowledgeService knowledgeService;

    @Autowired
    private ProjectService projectService;


    public byte[] getUserProfilePdf() {
        ProfileDto profileDto = new ProfileDto();
        UserDto userDto = userService.getUser();
        List<KnowledgeCategoryDto> knowledgeCategoryList = knowledgeService.getKnowledgeCategoryList();
        List<ProjectDto> projectDtoList = projectService.getAllProjectDto();

        profileDto.setUser(userDto);
        profileDto.setKnowledgeCategoryList(knowledgeCategoryList);
        profileDto.setProjectList(projectDtoList);

        String populatedTemplate = templateService.replacePlaceholder(getDefaultTemplate(), profileDto);

        return pdfService.getBytePdf(populatedTemplate);
    }

    public byte[] getPublicProfilePdf(ProfileDto profileDto, MultipartFile image) {
        Path savedImageFilePath = saveImageFile(image);
        profileDto.getUser().getAttributes().setAvatar(savedImageFilePath.toUri().getPath());

        String populatedTemplate = templateService.replacePlaceholder(getDefaultTemplate(), profileDto);
        byte[] profileBytes = pdfService.getBytePdf(populatedTemplate);

        deleteImageFile(savedImageFilePath);

        return profileBytes;
    }

    private String getDefaultTemplate() {
        URL url = getClass().getResource("/templates/defaultProfileTemplate.html");
        String filepath = Objects.requireNonNull(url, "Resource not found: " + "/templates/defaultProfileTemplate.html").getPath();
        File templateFile = new File(filepath);
        return convertFileToXhtml(templateFile);
    }

    private Path saveImageFile(MultipartFile image) {
        String uniqueFileName = UUID.randomUUID() + "_tmp_" + image.getOriginalFilename();
        Path uploadPath = Path.of("src/main/resources/static/images");
        Path newImagePath = uploadPath.resolve(uniqueFileName);

        try {
            Files.copy(image.getInputStream(), newImagePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Issue while storing the image file", e);
        }

        return newImagePath;
    }

    private void deleteImageFile(Path imageFilePath) {
        try {
            Files.delete(imageFilePath);
        } catch (IOException e) {
            System.err.println("image could not be deleted." + e.getMessage());
            throw new InternalServerException("image could not be deleted", e);
        }
    }

    private String convertFileToXhtml(File file) {
        try {
            return templateService.htmlToXhtml(file);
        } catch (IOException e) {
            throw new InternalServerException("Could not convert HTML to XHTML", e);
        }
    }
}
