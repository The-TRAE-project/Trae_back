package ru.trae.backend.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.trae.backend.entity.task.Operation;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "types")
public class TypeWork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String name;

    @ToString.Exclude
    @OneToMany(mappedBy = "typeWork", fetch = FetchType.LAZY)
    private List<Operation> operations;
}
