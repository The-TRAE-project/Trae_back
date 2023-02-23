package ru.trae.backend.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "working_shifts")
public class WorkingShift {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	private LocalDateTime startShift;

	private LocalDateTime endShift;

	private boolean isEnded;

	@ToString.Exclude
	@OneToMany(mappedBy = "workingShift", fetch = FetchType.EAGER)
	private List<TimeControl> timeControls = new ArrayList<>();

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		WorkingShift that = (WorkingShift) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

}
