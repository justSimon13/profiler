package dev.simonfischer.profiler.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "knowledge")
public class Knowledge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private KnowledgeCategory knowledgeCategory;

    @OneToMany(mappedBy = "knowledge", cascade = CascadeType.ALL)
    private List<ProjectKnowledge> projectKnowledges;
}
