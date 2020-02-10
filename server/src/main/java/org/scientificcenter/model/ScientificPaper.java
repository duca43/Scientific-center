package org.scientificcenter.model;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class ScientificPaper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String title;

    @Column
    private String paperAbstract;

    @Column(nullable = false)
    private String pdfFilePath;

    @Column
    private Boolean enabled;

    @Column
    private Boolean chosenCoauthors;

    @Column
    private Boolean approvedByMainEditor;

    @Column
    private Boolean pdfFormattedWell;

    @Column
    private Boolean requestedChanges;

    @ManyToOne
    private ScientificArea scientificArea;

    @ManyToMany
    private Set<Keyword> keywords;

    @OneToMany(mappedBy = "scientificPaper")
    private Set<Coauthor> coauthors;

    @ManyToOne
    private User author;

    @ManyToOne
    private User editor;

    @ManyToMany
    private Set<User> reviewers;

    @ManyToOne
    private Magazine magazine;
}