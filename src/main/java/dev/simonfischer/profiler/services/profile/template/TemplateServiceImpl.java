package dev.simonfischer.profiler.services.profile.template;

import dev.simonfischer.profiler.models.dto.KnowledgeCategoryDto;
import dev.simonfischer.profiler.models.dto.KnowledgeDto;
import dev.simonfischer.profiler.models.dto.ProfileDto;
import dev.simonfischer.profiler.models.dto.ProjectDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;


@Service
public class TemplateServiceImpl implements TemplateService{

    public String replacePlaceholder(String html, ProfileDto profileDto) {
        Map<String, Object> replacements = getPlaceholderForProfile(profileDto);

        for (Map.Entry<String, Object> entry : replacements.entrySet()) {
            if (!(entry.getValue() instanceof Collection<?>)) {
                if (entry.getValue() != null) {
                    html = html.replace(entry.getKey(), entry.getValue().toString());
                    continue;
                }

                System.err.println("WARNING: No value for key " + entry.getKey());
            }

            html = replacePlaceholderLoop(html, entry);
        }

        return html;
    }

    public String htmlToXhtml(File file) throws IOException {
        Document document = Jsoup.parse(file);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        return document.html();
    }

    /**
     * Generates a placeholder map for a profile using the given ProfilePublicDto object.
     *
     * @param profileDto The ProfilePublicDto object containing the profile information.
     * @return The LinkedHashMap representing the placeholder map for the profile.
     */
    private LinkedHashMap<String, Object> getPlaceholderForProfile(ProfileDto profileDto) {
        String image = Objects.requireNonNull(getClass().getResource("/assets/profileImages/" + profileDto.getUser().getAttributes().getAvatar())).getPath();
        LinkedHashMap<String, Object> profilePlaceholder = new LinkedHashMap<>();

        profilePlaceholder.put("{{avatar}}", image);
        profilePlaceholder.put("{{firstName}}", profileDto.getUser().getFirstName());
        profilePlaceholder.put("{{lastName}}", profileDto.getUser().getLastName());
        profilePlaceholder.put("{{description}}", profileDto.getUser().getAttributes().getDescription());
        profilePlaceholder.put("{{bornOn}}", profileDto.getUser().getAttributes().getBornOn());
        profilePlaceholder.put("{{location}}", profileDto.getUser().getAttributes().getLocation());
        profilePlaceholder.put("{{email}}", profileDto.getUser().getEmail());

        List<LinkedHashMap<String, Object>> linkPlaceholder = getPlaceholderForLinks(profileDto.getUser().getAttributes().getLinks());
        profilePlaceholder.put("{{links}}", linkPlaceholder);

        List<LinkedHashMap<String, Object>> knowledgeCategoriesPlaceholder = getPlaceholderForKnowledgeCategory(profileDto);
        profilePlaceholder.put("{{categoriesList}}", knowledgeCategoriesPlaceholder);

        List<LinkedHashMap<String, Object>> placeholderForProfile = getPlaceholderForProject(profileDto);
        profilePlaceholder.put("{{Projects}}", placeholderForProfile);

        return profilePlaceholder;
    }

    /**
     * Replaces a specific placeholder in an HTML string with values from a map of parameters using a loop.
     * The loop starts at the first occurrence of the start comment and ends at the first occurrence of the end comment.
     *
     * @param html the original HTML string
     * @param parameters a map entry containing the key and value of the placeholder to be replaced
     * @return the updated HTML string with the placeholder replaced by the parameter values
     */
    private String replacePlaceholderLoop(String html, Map.Entry<String, Object> parameters) {
        String startComment = "<!-- Loop Start " + parameters.getKey() + " -->";
        String endComment = "<!-- Loop End " + parameters.getKey() + " -->";
        int startIdx = html.indexOf(startComment);
        int endIdx = html.indexOf(endComment);

        if (startIdx == -1 || endIdx == -1 || !(parameters.getValue() instanceof Collection<?>)) {
            if (startIdx == -1 || endIdx == -1) {
                System.out.println("WARNING: Placeholder " + parameters.getKey() + " not found");
            }

            return html;
        }

        int index = 0;
        StringBuilder htmlBlock = new StringBuilder();
        List<Map<String, Object>> iterationList = (List<Map<String, Object>>) parameters.getValue();

        for (Map<String, Object> iteration : iterationList) {
            StringBuilder blockToAppend = new StringBuilder(html.substring(startIdx + startComment.length(), endIdx));

            for (Map.Entry<String, Object> entry : iteration.entrySet()) {
                if (entry.getValue() instanceof Collection<?>) {
                    blockToAppend = new StringBuilder(replacePlaceholderLoop(blockToAppend.toString(), entry));
                } else {
                    blockToAppend = cleanHtml(blockToAppend, iterationList, entry, index);

                    if(entry.getValue() != null) {
                        blockToAppend = new StringBuilder(blockToAppend.toString().replace(entry.getKey(), entry.getValue().toString()));
                        continue;
                    }

                    System.err.println("WARNING: No Value found for " + entry.getKey());
                }
            }

            htmlBlock.append(blockToAppend);
            index++;
        }

        return html.substring(0, startIdx) + htmlBlock + html.substring(endIdx + endComment.length());
    }


    private List<LinkedHashMap<String, Object>> getPlaceholderForLinks(Map<String, Object> link) {
        List<LinkedHashMap<String, Object>> iterationList = new ArrayList<>();

        for (Map.Entry<String, Object> linkEntry : link.entrySet()) {
            LinkedHashMap<String, Object> linkPlaceholder = new LinkedHashMap<>();

            linkPlaceholder.put("{{web_label}}", linkEntry.getKey());
            linkPlaceholder.put("{{link}}", linkEntry.getValue());

            iterationList.add(linkPlaceholder);
        }

        return iterationList;
    }

    private List<LinkedHashMap<String, Object>> getPlaceholderForKnowledgeCategory(ProfileDto profileDto) {
        List<LinkedHashMap<String, Object>> iterationList = new ArrayList<>();

        for(int i = 0; i < profileDto.getKnowledgeCategoryList().size(); i++) {
            LinkedHashMap<String, Object> categoryPlaceholder = new LinkedHashMap<>();
            List<LinkedHashMap<String, Object>> iterationListDeep = new ArrayList<>();

            KnowledgeCategoryDto knowledgeCategoryDto = profileDto.getKnowledgeCategoryList().get(i);
            categoryPlaceholder.put("{{category_name}}", knowledgeCategoryDto.getName());

            List<KnowledgeDto> knowledgeList = knowledgeCategoryDto.getKnowledgeList();

            for (KnowledgeDto dto : knowledgeList) {
                LinkedHashMap<String, Object> knowledgePlaceholder = new LinkedHashMap<>();

                knowledgePlaceholder.put("{{knowledge_name}}", dto.getName());
                iterationListDeep.add(knowledgePlaceholder);
            }

            categoryPlaceholder.put("{{knowledgeList}}", iterationListDeep);
            iterationList.add(categoryPlaceholder);
        }

        return iterationList;
    }

    private List<LinkedHashMap<String, Object>> getPlaceholderForProject(ProfileDto profileDto) {
        List<LinkedHashMap<String, Object>> iterationList = new ArrayList<>();

        for (ProjectDto projectDto : profileDto.getProjectList()) {
            List<LinkedHashMap<String, Object>> iterationListDeep = new ArrayList<>();
            LinkedHashMap<String, Object> projectPlaceholder = new LinkedHashMap<>();

            projectPlaceholder.put("{{project_name}}", projectDto.getName());
            projectPlaceholder.put("{{project_customer}}", projectDto.getCustomer());
            projectPlaceholder.put("{{project_description}}", projectDto.getDescription());
            projectPlaceholder.put("{{project_start}}", projectDto.getStart());
            projectPlaceholder.put("{{project_end}}", projectDto.getEnd());

            for (KnowledgeDto dto : projectDto.getKnowledgeList()) {
                LinkedHashMap<String, Object> knowledgePlaceholder = new LinkedHashMap<>();

                knowledgePlaceholder.put("{{knowledge_name}}", dto.getName());
                iterationListDeep.add(knowledgePlaceholder);
            }

            projectPlaceholder.put("{{knowledgeList}}", iterationListDeep);
            iterationList.add(projectPlaceholder);
        }

        return iterationList;
    }

    /**
     * Cleans the HTML content by deleting all commas that appear after a given placeholder.
     *
     * @param blockToAppend The StringBuilder representing the HTML content block to clean.
     * @param iterationList The List of Map entries representing the iteration list.
     * @param entry The Map.Entry representing the current entry in the iteration list.
     * @param index The index of the current iteration in the iteration list.
     * @return The updated StringBuilder with the commas removed.
     */
    private StringBuilder cleanHtml(StringBuilder blockToAppend, List<Map<String, Object>> iterationList, Map.Entry<String, Object> entry, int index) {
        if (index == iterationList.size() - 1) {
            // Deletes all commas that appears after placeholder
            String placeholderKey = entry.getKey();
            int placeholderIdx = blockToAppend.indexOf(placeholderKey);
            int endTagIdx = blockToAppend.indexOf("</");

            if (placeholderIdx != -1 && endTagIdx != -1 && placeholderIdx + placeholderKey.length() < endTagIdx) {
                blockToAppend.delete(placeholderIdx + placeholderKey.length(), endTagIdx);
            }
        }
        return blockToAppend;
    }
}
