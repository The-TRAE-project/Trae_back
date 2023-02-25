/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.trae.backend.entity.user.Customer;

/**
 * Repository class for <code>Customer</code> domain objects All method names are compliant
 * with Spring Data naming conventions so this interface can easily be extended for Spring
 * Data. See:
 * <a href="https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-creation">...</a>
 *
 * @author Vladimir Olennikov
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
  @Query("""
          select c from Customer c
          where upper(c.firstName) = upper(?1) and upper(c.middleName) = upper(?2)\s
          and upper(c.lastName) = upper(?3)""")
  Optional<Customer> findByFirstMiddleLastNameIgnoreCase(String firstName, String middleName,
                                                         String lastName);
}
