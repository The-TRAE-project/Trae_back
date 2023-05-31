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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.trae.backend.dto.employee.EmployeeDto;
import ru.trae.backend.entity.user.Employee;

class EmployeeDtoMapperTest {
  @Mock
  private TypeWorkDtoMapper typeWorkDtoMapper;
  @InjectMocks
  private EmployeeDtoMapper employeeDtoMapper;
  
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }
  
  @Test
  void apply_ShouldMapEmployeeToEmployeeDto() {
    //given
    Employee employee = new Employee();
    employee.setId(1L);
    employee.setFirstName("John");
    employee.setLastName("Doe");
    employee.setPhone("1234567890");
    employee.setPinCode(123);
    employee.setActive(false);
    employee.setDateOfEmployment(LocalDate.of(2022, 1, 1));
    employee.setDateOfRegister(LocalDate.of(2022, 2, 2));
    employee.setDateOfDismissal(LocalDate.of(2022, 3, 3));
    employee.setTypeWorks(Collections.emptySet());
    
    //when
    when(typeWorkDtoMapper.apply(any())).thenReturn(null);
    
    EmployeeDto result = employeeDtoMapper.apply(employee);
    
    //then
    assertEquals(employee.getId(), result.id());
    assertEquals(employee.getFirstName(), result.firstName());
    assertEquals(employee.getLastName(), result.lastName());
    assertEquals(employee.getPhone(), result.phone());
    assertEquals(employee.getPinCode(), result.pinCode());
    assertEquals(employee.isActive(), result.isActive());
    assertEquals(employee.getDateOfEmployment(), result.dateOfEmployment());
    assertEquals(employee.getDateOfRegister(), result.dateOfRegister());
    assertEquals(employee.getDateOfDismissal(), result.dateOfDismissal());
    assertEquals(employee.getTypeWorks().size(), result.types().size());
    
    // Verify that mocked mapper was called
    verify(typeWorkDtoMapper, times(employee.getTypeWorks().size())).apply(any());
  }
  
  @Test
  void apply_ShouldMapEmployeeToEmployeeDto_WithoutDateOfDismissal() {
    //given
    Employee employee = new Employee();
    employee.setId(1L);
    employee.setFirstName("John");
    employee.setLastName("Doe");
    employee.setPhone("1234567890");
    employee.setPinCode(123);
    employee.setActive(true);
    employee.setDateOfEmployment(LocalDate.of(2022, 1, 1));
    employee.setDateOfRegister(LocalDate.of(2022, 2, 2));
    employee.setDateOfDismissal(null);
    employee.setTypeWorks(Collections.emptySet());
    
    //when
    when(typeWorkDtoMapper.apply(any())).thenReturn(null);
    
    EmployeeDto result = employeeDtoMapper.apply(employee);
    
    //then
    assertEquals(employee.getId(), result.id());
    assertEquals(employee.getFirstName(), result.firstName());
    assertEquals(employee.getLastName(), result.lastName());
    assertEquals(employee.getPhone(), result.phone());
    assertEquals(employee.getPinCode(), result.pinCode());
    assertEquals(employee.isActive(), result.isActive());
    assertEquals(employee.getDateOfEmployment(), result.dateOfEmployment());
    assertEquals(employee.getDateOfRegister(), result.dateOfRegister());
    assertEquals(employee.getDateOfDismissal(), result.dateOfDismissal());
    assertEquals(employee.getTypeWorks().size(), result.types().size());
    
    // Verify that mocked mapper was called
    verify(typeWorkDtoMapper, times(employee.getTypeWorks().size())).apply(any());
  }
}
