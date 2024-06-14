package dev.simonfischer.profiler.services.knowledge;

import dev.simonfischer.profiler.models.dto.KnowledgeCategoryDto;
import dev.simonfischer.profiler.models.entity.Knowledge;
import dev.simonfischer.profiler.models.entity.KnowledgeCategory;
import dev.simonfischer.profiler.models.exception.entity.InternalServerException;
import dev.simonfischer.profiler.repositories.KnowledgeCategoryRepository;
import dev.simonfischer.profiler.repositories.KnowledgeRepository;
import dev.simonfischer.profiler.utility.GeneralUtility;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class KnowledgeServiceImpl implements KnowledgeService {

    @Autowired
    private KnowledgeCategoryRepository knowledgeCategoryRepository;

    @Autowired
    private KnowledgeRepository knowledgeRepository;

    @Autowired
    private ModelMapper modelMapper;

    public void updateKnowledgeCategoryList(List<KnowledgeCategoryDto> knowledgeCategoryDtos) {
        List<KnowledgeCategory> knowledgeCategoriesNew = knowledgeCategoryDtos.stream()
                .map(knowledgeCategoryDto ->
                        modelMapper.map(knowledgeCategoryDto, KnowledgeCategory.class)).toList();

        // Delete KnowledgeCategory
        List<KnowledgeCategory> knowledgeCategoriesDb = (List<KnowledgeCategory>) knowledgeCategoryRepository.findAll();
        knowledgeCategoryRepository.deleteAllById(
                GeneralUtility.getDifferences(knowledgeCategoriesNew, knowledgeCategoriesDb, KnowledgeCategory::getId));

        // Delete Knowledge
        List<Knowledge> knowledgeFromDb = (List<Knowledge>)knowledgeRepository.findAll();
        knowledgeFromDb = knowledgeFromDb.stream().filter(knowledge -> knowledge.getKnowledgeCategory() != null).toList();
        List<Knowledge> knowledgeNew = knowledgeCategoriesNew.stream()
                .flatMap(knowledgeCategory -> knowledgeCategory.getKnowledgeList().stream())
                .toList();

        try {
            knowledgeRepository.deleteAllById(
                    GeneralUtility.getDifferences(knowledgeNew, knowledgeFromDb, Knowledge::getId));

            for (KnowledgeCategory category : knowledgeCategoriesNew) {
                List<Knowledge> knowledgeList = new ArrayList<>();
                knowledgeCategoryRepository.save(category);

                for (Knowledge knowledge : category.getKnowledgeList()) {
                    knowledge.setKnowledgeCategory(category);
                    knowledgeList.add(knowledge);
                }

                knowledgeRepository.saveAll(knowledgeList);
            }

        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            throw new InternalServerException("There was an error while saving the knowledge list.");
        }
    }

    public List<KnowledgeCategoryDto> getKnowledgeCategoryList() {
        List<KnowledgeCategory> knowledgeCategories =
                (List<KnowledgeCategory>) knowledgeCategoryRepository.findAll();

        return knowledgeCategories.stream().map(knowledgeCategory ->
                modelMapper.map(knowledgeCategory, KnowledgeCategoryDto.class)).toList();
    }
}
