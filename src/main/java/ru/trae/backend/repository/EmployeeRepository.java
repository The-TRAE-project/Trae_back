package ru.trae.backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.trae.backend.entity.user.Employee;

/**
 * Repository class for <code>Employee</code> domain objects All method names are compliant
 * with Spring Data naming conventions so this interface can easily be extended for Spring
 * Data. See:
 * <a href="https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-creation">...</a>
 *
 * @author Vladimir Olennikov
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
  Optional<Employee> findByPinCode(int pinCode);

  boolean existsByPinCode(int pinCode);

  @Query("""
          select (count(e) > 0) from Employee e
          where upper(e.firstName) = upper(?1) and upper(e.middleName) = upper(?2) and\s
          upper(e.lastName) = upper(?3)""")
  boolean existsByFirstMiddleLastNameIgnoreCase(String firstName,
                                                String middleName,
                                                String lastName);
}
