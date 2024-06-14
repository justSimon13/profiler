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
        File imageFile = saveImageFile(image);
        profileDto.getUser().getAttributes().setAvatar(imageFile.getName());

        String populatedTemplate = templateService.replacePlaceholder(getDefaultTemplate(), profileDto);
        byte[] profileBytes = pdfService.getBytePdf(populatedTemplate);
        deleteImageFile(imageFile);

        return profileBytes;
    }

    private String getDefaultTemplate() {
        final String TEMPLATE_PATH = "/templates/defaultProfileTemplate.html";
        File templateFile = getFileFromClasspath(TEMPLATE_PATH);
        return convertFileToXhtml(templateFile);
    }

    private File saveImageFile(MultipartFile image) {
        final String IMAGES_PATH = "/assets/profileImages/";
        String imageFilename = UUID.randomUUID() + ".jpg";
        String imageFilepath = getFileFromClasspath(IMAGES_PATH).getPath() + "/" + imageFilename;
        File imageFile = new File(imageFilepath);
        try {
            image.transferTo(imageFile);
        } catch (IOException e) {
            throw new IllegalStateException("Issue while storing the image file", e);
        }
        return imageFile;
    }

    private void deleteImageFile(File imageFile) {
        boolean deleted = imageFile.delete();
        if (!deleted) {
            System.err.println("image could not be deleted.");
        }
    }

    private File getFileFromClasspath(String path) {
        URL url = getClass().getResource(path);
        String filepath = Objects.requireNonNull(url, "Resource not found: " + path).getPath();
        return new File(filepath);
    }

    private String convertFileToXhtml(File file) {
        try {
            return templateService.htmlToXhtml(file);
        } catch (IOException e) {
            throw new InternalServerException("Could not convert HTML to XHTML", e);
        }
    }
}
