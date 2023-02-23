package ru.trae.backend.entity.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.trae.backend.entity.TimeControl;
import ru.trae.backend.entity.TypeWork;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
@Table(name = "employees")
public class Employee extends User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(unique = true, nullable = false)
	private int pinCode;

	private boolean isActive;

	@ToString.Exclude
	@OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
	private List<TimeControl> timeControls = new ArrayList<>();

	@ManyToMany
	@JoinTable(name = "employees_type_works", joinColumns = @JoinColumn(name = "employee_id"),
			inverseJoinColumns = @JoinColumn(name = "type_works_id"))
	private Set<TypeWork> typeWorks = new HashSet<>();

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Employee employee = (Employee) o;
		return Objects.equals(id, employee.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

}
