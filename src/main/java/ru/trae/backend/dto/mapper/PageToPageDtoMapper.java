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

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.trae.backend.dto.PageDto;
import ru.trae.backend.dto.employee.EmployeeDto;
import ru.trae.backend.dto.manager.ManagerDto;
import ru.trae.backend.entity.user.Employee;
import ru.trae.backend.entity.user.Manager;

/**
 * This class is used for mapping a Page of a certain model to a PageDto with corresponding Dtos.
 *
 * @author Vladimir Olennikov
 */
@Component
@RequiredArgsConstructor
public class PageToPageDtoMapper {
  private final ManagerDtoMapper managerDtoMapper;
  private final EmployeeDtoMapper employeeDtoMapper;


  /**
   * Map a page of Manager to a page of ManagerDto.
   *
   * @param page page of Manager
   * @return page of ManagerDto
   */
  public PageDto<ManagerDto> managerPageToPageDto(Page<Manager> page) {
    return new PageDto<>(page.getContent().stream()
        .map(managerDtoMapper)
        .toList(), page.getTotalElements());
  }

  /**
   * Maps a Page of Employee to a PageDto of EmployeeDto.
   *
   * @param page The page of employee to be mapped.
   * @return The pageDto containing the employeeDtos.
   */
  public PageDto<EmployeeDto> employeePageToPageDto(Page<Employee> page) {
    return new PageDto<>(page.getContent().stream()
        .map(employeeDtoMapper)
        .toList(), page.getTotalElements());
  }
}
