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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.employee.EmployeeDto;
import ru.trae.backend.entity.user.Employee;

/**
 * The EmployeeDtoMapper class is a mapper that is used to convert an Employee object to
 * an EmployeeDto object.
 * It implements the Function interface, allowing it to be used as a function to map one
 * object type to another.
 *
 * @author Vladimir Olennikov
 */
@Service
@RequiredArgsConstructor
public class EmployeeDtoMapper implements Function<Employee, EmployeeDto> {
  private final TypeWorkDtoMapper typeWorkDtoMapper;

  @Override
  public EmployeeDto apply(Employee e) {
    return new EmployeeDto(
        e.getId(),
        e.getFirstName(),
        e.getMiddleName(),
        e.getLastName(),
        e.getPhone(),
        e.getPinCode(),
        e.isActive(),
        e.getDateOfEmployment(),
        e.getDateOfRegister(),
        e.getTypeWorks().stream()
            .map(typeWorkDtoMapper)
            .toList()
    );
  }
}
