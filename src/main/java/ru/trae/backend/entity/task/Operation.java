package ru.trae.backend.entity.task;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.trae.backend.entity.TypeWork;
import ru.trae.backend.entity.user.Employee;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "operations")
public class Operation extends Task {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	private int priority;

	private boolean inWork;

	private boolean readyToAcceptance;

	private LocalDateTime acceptanceDate;

	@ToString.Exclude
	@ManyToOne
	@JoinColumn(name = "employee_id")
	private Employee employee;

	@ToString.Exclude
	@ManyToOne
	@JoinColumn(name = "project_id", nullable = false)
	private Project project;

	@ToString.Exclude
	@ManyToOne
	@JoinColumn(name = "type_work_id", nullable = false)
	private TypeWork typeWork;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Operation operation = (Operation) o;
		return Objects.equals(id, operation.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

}
