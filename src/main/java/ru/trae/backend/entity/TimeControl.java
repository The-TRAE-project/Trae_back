package ru.trae.backend.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.trae.backend.entity.user.Employee;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "time_controls")
public class TimeControl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private boolean isOnShift;
    private boolean autoClosingShift;
    private LocalDateTime arrival;
    private LocalDateTime departure;
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "working_shift_id", nullable = false)
    private WorkingShift workingShift;
}
