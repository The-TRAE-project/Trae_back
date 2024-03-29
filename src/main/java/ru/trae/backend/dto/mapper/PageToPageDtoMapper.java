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
import ru.trae.backend.dto.manager.ManagerShortDto;
import ru.trae.backend.dto.project.ProjectShortDto;
import ru.trae.backend.dto.type.TypeWorkDto;
import ru.trae.backend.entity.TypeWork;
import ru.trae.backend.entity.task.Project;
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
  private final EmployeeDtoMapper employeeDtoMapper;
  private final TypeWorkDtoMapper typeWorkDtoMapper;
  private final ManagerShortDtoMapper managerShortDtoMapper;
  private final ProjectShortDtoMapper projectShortDtoMapper;


  /**
   * Map a page of Manager to a page of ManagerShortDto.
   *
   * @param page page of Manager
   * @return page of ManagerShortDto
   */
  public PageDto<ManagerShortDto> managerPageToPageDto(Page<Manager> page) {
    return new PageDto<>(page.getContent().stream()
        .map(managerShortDtoMapper)
        .toList(), page.getTotalElements(), page.getTotalPages(), page.getNumber());
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
        .toList(), page.getTotalElements(), page.getTotalPages(), page.getNumber());
  }

  /**
   * This method maps a page of TypeWork objects to a page of TypeWorkDto objects.
   *
   * @param page The page of TypeWork objects to be mapped
   * @return A page of TypeWorkDto objects
   */
  public PageDto<TypeWorkDto> typeWorkPageToPageDto(Page<TypeWork> page) {
    return new PageDto<>(page.getContent().stream()
        .map(typeWorkDtoMapper)
        .toList(), page.getTotalElements(), page.getTotalPages(), page.getNumber());
  }

  /**
   * Converts a page of projects to page DTO.
   *
   * @param page page of projects
   * @return page DTO
   */
  public PageDto<ProjectShortDto> projectPageToPageDto(Page<Project> page) {
    return new PageDto<>(page.getContent().stream()
        .map(projectShortDtoMapper)
        .toList(), page.getTotalElements(), page.getTotalPages(), page.getNumber());
  }
}
