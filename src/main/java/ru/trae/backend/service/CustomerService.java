/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.service;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.CustomerDto;
import ru.trae.backend.entity.user.Customer;
import ru.trae.backend.repository.CustomerRepository;

/**
 * Service class to work with customer data.
 *
 * @author Vladimir Olennikov
 */
@Service
@RequiredArgsConstructor
public class CustomerService {
  private final CustomerRepository customerRepository;

  /**
   * Method for saving new customers to the database.
   *
   * @param dto contains data for creating a new customer
   * @return a saved customer entity
   */
  public Customer saveNewCustomer(CustomerDto dto) {
    Customer c = new Customer();
    c.setFirstName(dto.firstName());
    c.setMiddleName(dto.middleName());
    c.setLastName(dto.lastName());
    c.setPhone(dto.phone());
    c.setDateOfRegister(LocalDateTime.now());

    return customerRepository.save(c);
  }

  public Optional<Customer> getCustomer(String firstName, String middleName, String lastName) {
    return customerRepository.findByFirstMiddleLastNameIgnoreCase(firstName, middleName, lastName);
  }
}
