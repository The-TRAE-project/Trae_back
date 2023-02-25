package ru.trae.backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.trae.backend.entity.user.Employee;

/**
 * Interface for repository operations on {@link Employee} entities.
 *
 * @author Vladimir Olennikov
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
  /**
   * Retrieves an {@link Optional} of an {@link Employee} based on the given pin code.
   *
   * @param pinCode the pin code to search for
   * @return an {@link Optional} of an {@link Employee}
   */
  Optional<Employee> findByPinCode(int pinCode);

  /**
   * Checks if an {@link Employee} exists with the given pin code.
   *
   * @param pinCode the pin code to search for
   * @return true if an {@link Employee} exists with given pin code, false otherwise
   */
  boolean existsByPinCode(int pinCode);

  /**
   * Checks if an {@link Employee} exists with the given first, middle and last name (case insensitive).
   *
   * @param firstName  the first name to search for
   * @param middleName the middle name to search for
   * @param lastName   the last name to search for
   * @return true if an {@link Employee} exists with given first, middle and last name, false otherwise
   */
  @Query("""
          select (count(e) > 0) from Employee e
          where upper(e.firstName) = upper(?1) and upper(e.middleName) = upper(?2) and\s
          upper(e.lastName) = upper(?3)""")
  boolean existsByFirstMiddleLastNameIgnoreCase(String firstName,
                                                String middleName,
                                                String lastName);
}
