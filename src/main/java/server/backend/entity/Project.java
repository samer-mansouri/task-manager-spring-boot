package server.backend.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "project")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @Id
    @SequenceGenerator(name = "project_seq", sequenceName = "project_seq", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "project_seq")
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    private String titre;
    private Date dateDebut;
    private Date dateFin;
    private String description;
    private String etat;

    @OneToMany(mappedBy = "project")
    private List<Module> modules;

    @ManyToOne
    @JoinColumn(name = "chief_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private User chief;

    @OneToMany(mappedBy = "project")
    private List<Document> documents;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    public Project(String titre, Date dateDebut, Date dateFin, String description, String etat) {
        this.titre = titre;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.description = description;
        this.etat = etat;
    }



}
