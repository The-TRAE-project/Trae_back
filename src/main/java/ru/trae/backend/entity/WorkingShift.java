package ru.trae.backend.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.trae.backend.entity.user.Employee;
import ru.trae.backend.util.DayOrNight;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "working_shift")
public class WorkingShift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private LocalDateTime startShift;
    private LocalDateTime endShift;
    private boolean isEnded;
    @Column(nullable = false)
    private DayOrNight timeOfDay;
    @ToString.Exclude
    @ManyToMany
    @JoinTable(
            name = "shift_employee",
            joinColumns = @JoinColumn(name = "work_shifting_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id"))
    private List<Employee> employees;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkingShift that = (WorkingShift) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
