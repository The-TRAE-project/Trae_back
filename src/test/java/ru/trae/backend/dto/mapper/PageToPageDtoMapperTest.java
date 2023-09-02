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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.trae.backend.dto.PageDto;
import ru.trae.backend.dto.employee.EmployeeDto;
import ru.trae.backend.dto.manager.ManagerShortDto;
import ru.trae.backend.dto.project.ProjectShortDto;
import ru.trae.backend.dto.type.TypeWorkDto;
import ru.trae.backend.entity.TypeWork;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.entity.user.Employee;
import ru.trae.backend.entity.user.Manager;

class PageToPageDtoMapperTest {
  @Mock
  private EmployeeDtoMapper employeeDtoMapper;
  @Mock
  private TypeWorkDtoMapper typeWorkDtoMapper;
  @Mock
  private ManagerShortDtoMapper managerShortDtoMapper;
  @Mock
  private ProjectShortDtoMapper projectShortDtoMapper;
  @InjectMocks
  private PageToPageDtoMapper pageToPageDtoMapper;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void managerPageToPageDto_ShouldMapManagerPageToManagerShortDtoPage() {
    //given
    Manager manager1 = new Manager();
    manager1.setId(1L);
    Manager manager2 = new Manager();
    manager2.setId(2L);
    List<Manager> managers = List.of(manager1, manager2);
    Page<Manager> managerPage = new PageImpl<>(managers);

    //when
    ManagerShortDto managerDto1 = new ManagerShortDto(1L, null, null);
    ManagerShortDto managerDto2 = new ManagerShortDto(2L, null, null);
    when(managerShortDtoMapper.apply(manager1)).thenReturn(managerDto1);
    when(managerShortDtoMapper.apply(manager2)).thenReturn(managerDto2);

    PageDto<ManagerShortDto> result = pageToPageDtoMapper.managerPageToPageDto(managerPage);

    //then
    assertEquals(managers.size(), result.content().size());
    assertEquals(managerDto1, result.content().get(0));
    assertEquals(managerDto2, result.content().get(1));
    assertEquals(managerPage.getTotalElements(), result.totalElements());
    assertEquals(managerPage.getTotalPages(), result.totalPages());
    assertEquals(managerPage.getNumber(), result.currentNumberPage());

    verify(managerShortDtoMapper, times(2)).apply(any(Manager.class));
  }

  @Test
  void employeePageToPageDto_ShouldMapEmployeePageToEmployeeDtoPage() {
    //given
    Employee employee1 = new Employee();
    employee1.setId(1L);
    Employee employee2 = new Employee();
    employee2.setId(2L);
    List<Employee> employees = List.of(employee1, employee2);
    Page<Employee> employeePage = new PageImpl<>(employees);

    //when
    EmployeeDto employeeDto1 = new EmployeeDto(1L, null, null, null,
        null, null, true, null, null, null, null);
    EmployeeDto employeeDto2 = new EmployeeDto(2L, null, null, null,
        null, null, true, null, null, null, null);
    when(employeeDtoMapper.apply(employee1)).thenReturn(employeeDto1);
    when(employeeDtoMapper.apply(employee2)).thenReturn(employeeDto2);

    PageDto<EmployeeDto> result = pageToPageDtoMapper.employeePageToPageDto(employeePage);

    //then
    assertEquals(employees.size(), result.content().size());
    assertEquals(employeeDto1, result.content().get(0));
    assertEquals(employeeDto2, result.content().get(1));
    assertEquals(employeePage.getTotalElements(), result.totalElements());
    assertEquals(employeePage.getTotalPages(), result.totalPages());
    assertEquals(employeePage.getNumber(), result.currentNumberPage());

    verify(employeeDtoMapper, times(2)).apply(any(Employee.class));
  }

  @Test
  void typeWorkPageToPageDto_ShouldMapTypeWorkPageToTypeWorkDtoPage() {
    //given
    TypeWork typeWork1 = new TypeWork();
    typeWork1.setId(1L);
    TypeWork typeWork2 = new TypeWork();
    typeWork2.setId(2L);
    List<TypeWork> typeWorks = List.of(typeWork1, typeWork2);
    Page<TypeWork> typeWorkPage = new PageImpl<>(typeWorks);

    TypeWorkDto typeWorkDto1 = new TypeWorkDto(1L, null, true);
    TypeWorkDto typeWorkDto2 = new TypeWorkDto(2L, null, true);
    when(typeWorkDtoMapper.apply(typeWork1)).thenReturn(typeWorkDto1);
    when(typeWorkDtoMapper.apply(typeWork2)).thenReturn(typeWorkDto2);

    PageDto<TypeWorkDto> result = pageToPageDtoMapper.typeWorkPageToPageDto(typeWorkPage);

    //then
    assertEquals(typeWorks.size(), result.content().size());
    assertEquals(typeWorkDto1, result.content().get(0));
    assertEquals(typeWorkDto2, result.content().get(1));
    assertEquals(typeWorkPage.getTotalElements(), result.totalElements());
    assertEquals(typeWorkPage.getTotalPages(), result.totalPages());
    assertEquals(typeWorkPage.getNumber(), result.currentNumberPage());

    verify(typeWorkDtoMapper, times(2)).apply(any(TypeWork.class));
  }

  @Test
  void projectPageToPageDto_ShouldMapProjectPageToProjectShortDtoPage() {
    //given
    Project project1 = new Project();
    project1.setId(1L);
    Project project2 = new Project();
    project2.setId(2L);
    List<Project> projects = List.of(project1, project2);
    Page<Project> projectPage = new PageImpl<>(projects);

    //when
    ProjectShortDto projectDto1 = new ProjectShortDto(1L, true,
        false, false, 1, null, null, null);
    ProjectShortDto projectDto2 = new ProjectShortDto(2L, true,
        false, false, 2, null, null, null);
    when(projectShortDtoMapper.apply(project1)).thenReturn(projectDto1);
    when(projectShortDtoMapper.apply(project2)).thenReturn(projectDto2);

    PageDto<ProjectShortDto> result = pageToPageDtoMapper.projectPageToPageDto(projectPage);

    //then
    assertEquals(projects.size(), result.content().size());
    assertEquals(projectDto1, result.content().get(0));
    assertEquals(projectDto2, result.content().get(1));
    assertEquals(projectPage.getTotalElements(), result.totalElements());
    assertEquals(projectPage.getTotalPages(), result.totalPages());
    assertEquals(projectPage.getNumber(), result.currentNumberPage());

    verify(projectShortDtoMapper, times(2)).apply(any(Project.class));
  }
}
