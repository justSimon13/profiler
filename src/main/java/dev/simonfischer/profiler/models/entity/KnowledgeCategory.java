package dev.simonfischer.profiler.models.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "knowledge_category")
public class KnowledgeCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "knowledgeCategory", cascade = CascadeType.ALL)
    private List<Knowledge> knowledgeList;
}
