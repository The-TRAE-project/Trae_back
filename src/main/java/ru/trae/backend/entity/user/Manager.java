package ru.trae.backend.entity.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.trae.backend.entity.task.Order;
import ru.trae.backend.entity.task.Project;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "managers")
public class Manager extends User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String username;
    private String email;
    private String password;
    @ToString.Exclude
    @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
    private List<Order> orders;
    @ToString.Exclude
    @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
    private List<Project> projects;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Manager manager = (Manager) o;
        return Objects.equals(id, manager.id) && Objects.equals(username, manager.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
}
