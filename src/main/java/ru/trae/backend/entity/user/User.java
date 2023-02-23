package ru.trae.backend.entity.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class User {

	private String firstName;

	private String middleName;

	private String lastName;

	private Long phone;

	private LocalDateTime dateOfRegister;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		User user = (User) o;
		return Objects.equals(firstName, user.firstName) && Objects.equals(middleName, user.middleName)
				&& Objects.equals(lastName, user.lastName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(firstName, middleName, lastName);
	}

}
