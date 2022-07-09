package server.backend.entity;


import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
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

    private String name;
    private String description;

    @OneToMany(mappedBy = "project")
    private List<Module> modules;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;


}
