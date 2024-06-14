package dev.simonfischer.profiler.controllers;

import dev.simonfischer.profiler.services.knowledge.KnowledgeService;
import dev.simonfischer.profiler.models.dto.KnowledgeCategoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/knowledge")
public class KnowledgeController {

    @Autowired
    private KnowledgeService knowledgeService;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<KnowledgeCategoryDto>> getKnowledgeCategoryList() {
        List<KnowledgeCategoryDto> knowledgeCategoryDtos = knowledgeService.getKnowledgeCategoryList();
        return new ResponseEntity<>(knowledgeCategoryDtos, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<String> updateKnowledgeCategoryList(@RequestBody List<KnowledgeCategoryDto> knowledgeCategoryDtos) {
        knowledgeService.updateKnowledgeCategoryList(knowledgeCategoryDtos);
        return new ResponseEntity<>("Knowledge updated successfully", HttpStatus.OK);
    }
}
