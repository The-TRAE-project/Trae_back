package ru.trae.backend.entity.task;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.trae.backend.entity.user.Manager;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "projects")
public class Project extends Task{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "manager_id", nullable = false)
    private Manager manager;
    @ToString.Exclude
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<Operation> operations;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(id, project.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
