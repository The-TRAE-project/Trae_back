package ru.trae.backend.entity.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.WorkShifting;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "employees")
public class Employee extends User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(unique = true,nullable = false)
    private int pinCode;
    @ToString.Exclude
    @OneToOne
    @JoinColumn(name = "operation_id")
    private Operation operation;
    @ToString.Exclude
    @ManyToMany(mappedBy = "employees", fetch = FetchType.LAZY)
    private List<WorkShifting> workShiftings;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
