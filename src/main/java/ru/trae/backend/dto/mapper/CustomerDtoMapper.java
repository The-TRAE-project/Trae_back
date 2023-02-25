/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.dto.mapper;

import java.util.function.Function;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.CustomerDto;
import ru.trae.backend.entity.user.Customer;

/**
 * This class provides a function to map a Customer object to a CustomerDto object.
 *
 * @author Vladimir Olennikov
 */
@Service
public class CustomerDtoMapper implements Function<Customer, CustomerDto> {
  @Override
  public CustomerDto apply(Customer c) {
    return new CustomerDto(
            c.getFirstName(),
            c.getMiddleName(),
            c.getLastName(),
            c.getPhone(),
            c.getDateOfRegister()
    );
  }
}
