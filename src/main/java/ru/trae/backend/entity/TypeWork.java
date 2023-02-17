package ru.trae.backend.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.user.Employee;

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
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "type_work_employee",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "type_work_id"))
    private List<Employee> employees;
    @ToString.Exclude
    @OneToMany(mappedBy = "typeWork", fetch = FetchType.LAZY)
    private List<Operation> operations;
}
