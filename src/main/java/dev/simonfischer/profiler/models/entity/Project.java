package dev.simonfischer.profiler.models.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String customer;
    private String name;
    private String description;
    private LocalDate start;
    private LocalDate end;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<ProjectKnowledge> projectKnowledges;
}
