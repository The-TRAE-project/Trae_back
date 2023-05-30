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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.trae.backend.dto.employee.EmployeeRegisterDtoReq;
import ru.trae.backend.dto.employee.EmployeeRegisterDtoResp;
import ru.trae.backend.dto.employee.ShortEmployeeDto;
import ru.trae.backend.dto.mapper.EmployeeDtoMapper;
import ru.trae.backend.dto.mapper.PageToPageDtoMapper;
import ru.trae.backend.entity.user.Employee;
import ru.trae.backend.exceptionhandler.exception.EmployeeException;
import ru.trae.backend.repository.EmployeeRepository;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
  @Mock
  private EmployeeRepository employeeRepository;
  @Mock
  private EmployeeDtoMapper employeeDtoMapper;
  @Mock
  private WorkingShiftService workingShiftService;
  @Mock
  private TimeControlService timeControlService;
  @Mock
  private TypeWorkService typeWorkService;
  @Mock
  private PageToPageDtoMapper pageToPageDtoMapper;
  @InjectMocks
  private EmployeeService employeeService;
  long employeeId = 1L;
  String phoneNumber = "+7 (999) 000 0000";
  String firstname = "test_first_name";
  String middleName = "test_middle_name";
  String lastName = "test_last_name";
  LocalDate dateOfEmployment = LocalDate.now();
  int pinCode = 123;
  Employee e = new Employee();
  
  @BeforeEach
  public void init() {
    e.setId(employeeId);
    e.setFirstName(firstname);
    e.setMiddleName(middleName);
    e.setLastName(lastName);
    e.setPhone(phoneNumber);
    e.setPinCode(pinCode);
    e.setActive(true);
    e.setDateOfEmployment(dateOfEmployment);
  }
  
  @Test
  void saveNewEmployee_WithValidDto_ShouldReturnEmployeeRegisterDtoResp() {
    //given
    EmployeeRegisterDtoReq dto = new EmployeeRegisterDtoReq(
        firstname, middleName, lastName,
        phoneNumber, dateOfEmployment, Collections.singletonList(1L));
    
    //when
    Employee employee = new Employee();
    employee.setFirstName(dto.firstName());
    employee.setLastName(dto.lastName());
    employee.setPhone(dto.phone());
    employee.setDateOfEmployment(dto.dateOfEmployment());
    employee.setPinCode(pinCode);
    
    when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
    
    EmployeeRegisterDtoResp response = employeeService.saveNewEmployee(dto);
    
    //then
    assertNotNull(response);
    assertEquals(firstname, response.firstName());
    assertEquals(lastName, response.lastName());
    assertEquals(pinCode, response.pinCode());
    
    verify(employeeRepository).save(any(Employee.class));
  }
  
  @Test
  void getEmployeeById_WithExistingId_ShouldReturnEmployee() {
    //when
    when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(e));
    
    Employee result = employeeService.getEmployeeById(employeeId);
    
    //then
    assertNotNull(result);
    assertEquals(employeeId, result.getId());
    
    verify(employeeRepository, times(1)).findById(employeeId);
  }
  
  @Test
  void getEmployeeById_WithNonExistingId_ShouldThrowException() {
    //when
    when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());
    
    //then
    assertThrows(EmployeeException.class, () -> employeeService.getEmployeeById(employeeId));
    
    verify(employeeRepository, times(1)).findById(employeeId);
  }
  
  @Test
  void employeeLogin_WithExistingPinCodeAndActiveEmployee_ShouldReturnShortEmployeeDto() {
    //when
    when(employeeRepository.findByPinCode(pinCode)).thenReturn(Optional.of(e));
    when(workingShiftService.employeeOnShift(true, e.getId())).thenReturn(true);
    
    ShortEmployeeDto result = employeeService.employeeLogin(pinCode);
    
    //then
    assertNotNull(result);
    assertEquals(1L, result.id());
    assertEquals(firstname, result.firstName());
    assertEquals(lastName, result.lastName());
    assertTrue(result.onShift());
    
    verify(employeeRepository, times(1)).findByPinCode(pinCode);
    verify(workingShiftService, times(1)).employeeOnShift(true, e.getId());
  }
  
  @Test
  void employeeLogin_WithNonExistingPinCode_ShouldThrowException() {
    //when
    when(employeeRepository.findByPinCode(pinCode)).thenReturn(Optional.empty());
    
    //then
    assertThrows(EmployeeException.class, () -> employeeService.employeeLogin(pinCode));
    
    verify(employeeRepository, times(1)).findByPinCode(pinCode);
  }
  
  @Test
  void employeeLogin_WithInactiveEmployee_ShouldThrowException() {
    //given
    e.setActive(false);
    
    //when
    when(employeeRepository.findByPinCode(pinCode)).thenReturn(Optional.of(e));
    
    //then
    assertThrows(EmployeeException.class, () -> employeeService.employeeLogin(pinCode));
    
    verify(employeeRepository, times(1)).findByPinCode(pinCode);
  }
  
  @Test
  void checkInEmployee_WithNonShiftEmployee_ShouldUpdateShiftAndReturnShortEmployeeDto() {
    //when
    when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(e));
    when(workingShiftService.employeeOnShift(true, e.getId())).thenReturn(true);
    
    ShortEmployeeDto result = employeeService.checkInEmployee(employeeId);
    
    //then
    assertNotNull(result);
    assertEquals(employeeId, result.id());
    assertEquals(firstname, result.firstName());
    assertEquals(lastName, result.lastName());
    assertTrue(result.onShift());
    
    verify(workingShiftService, times(2)).employeeOnShift(true, e.getId());
  }
  
  @Test
  void checkInEmployee_WithShiftEmployee_ShouldNotUpdateShiftAndReturnShortEmployeeDto() {
    //when
    when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(e));
    when(workingShiftService.employeeOnShift(true, e.getId())).thenReturn(true);
    
    ShortEmployeeDto result = employeeService.checkInEmployee(employeeId);
    
    //then
    assertNotNull(result);
    assertEquals(employeeId, result.id());
    assertEquals(firstname, result.firstName());
    assertEquals(lastName, result.lastName());
    assertTrue(result.onShift());
    
    verify(workingShiftService, times(2)).employeeOnShift(true, e.getId());
  }
  
  @Test
  void departureEmployee_WithShiftEmployee_ShouldUpdateTimeControlAndReturnShortEmployeeDto() {
    //when
    when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(e));
    when(workingShiftService.employeeOnShift(true, e.getId())).thenReturn(true);
    
    ShortEmployeeDto result = employeeService.departureEmployee(employeeId);
    
    //then
    assertNotNull(result);
    assertEquals(employeeId, result.id());
    assertEquals(firstname, result.firstName());
    assertEquals(lastName, result.lastName());
    assertTrue(result.onShift());
    
    verify(workingShiftService, times(2)).employeeOnShift(true, e.getId());
    //verify(timeControlService, times(1)).updateTimeControlForDeparture(employeeId, LocalDateTime.now());
  }
  
  @Test
  void departureEmployee_WithNonShiftEmployee_ShouldNotUpdateTimeControlAndReturnShortEmployeeDto() {
    //when
    when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(e));
    when(workingShiftService.employeeOnShift(true, e.getId())).thenReturn(false);
    
    ShortEmployeeDto result = employeeService.departureEmployee(employeeId);
    
    //then
    assertNotNull(result);
    assertEquals(employeeId, result.id());
    assertEquals(firstname, result.firstName());
    assertEquals(lastName, result.lastName());
    assertFalse(result.onShift());
    
    verify(workingShiftService, times(2)).employeeOnShift(true, e.getId());
    verify(timeControlService, never()).updateTimeControlForDeparture(anyLong(), any(LocalDateTime.class));
  }
  
  @Test
  void getEmployeePage_WithAllParameters_ShouldReturnEmployeePage() {
    //given
    int page = 0;
    int size = 10;
    List<Long> typeWorkId = Arrays.asList(1L, 2L);
    boolean isActive = true;
    Page<Employee> employeePage = new PageImpl<>(new ArrayList<>());
    
    Pageable pageable = PageRequest.of(page, size);
    
    //when
    when(employeeRepository.findByIsActiveAndTypeWorksId(isActive, typeWorkId, pageable)).thenReturn(employeePage);
    
    Page<Employee> result = employeeService.getEmployeePage(pageable, typeWorkId, isActive);
    
    //then
    assertNotNull(result);
    assertEquals(employeePage, result);
    
    verify(employeeRepository, times(1)).findByIsActiveAndTypeWorksId(isActive, typeWorkId, pageable);
  }
  
  @Test
  void getEmployeePage_WithOnlyIsActive_ShouldReturnEmployeePage() {
    //given
    int page = 0;
    int size = 10;
    boolean isActive = true;
    Page<Employee> employeePage = new PageImpl<>(new ArrayList<>());
    
    Pageable pageable = PageRequest.of(page, size);
    
    //when
    when(employeeRepository.findByIsActive(isActive, pageable)).thenReturn(employeePage);
    
    Page<Employee> result = employeeService.getEmployeePage(pageable, null, isActive);
    
    //then
    assertNotNull(result);
    assertEquals(employeePage, result);
    
    verify(employeeRepository, times(1)).findByIsActive(isActive, pageable);
  }
  
  @Test
  void getEmployeePage_WithOnlyTypeWorkId_ShouldReturnEmployeePage() {
    //given
    int page = 0;
    int size = 10;
    List<Long> typeWorkId = Arrays.asList(1L, 2L);
    Page<Employee> employeePage = new PageImpl<>(new ArrayList<>());
    
    Pageable pageable = PageRequest.of(page, size);
    
    //when
    when(employeeRepository.findByTypeWorksId(typeWorkId, pageable)).thenReturn(employeePage);
    
    Page<Employee> result = employeeService.getEmployeePage(pageable, typeWorkId, null);
    
    //then
    assertNotNull(result);
    assertEquals(employeePage, result);
    
    verify(employeeRepository, times(1)).findByTypeWorksId(typeWorkId, pageable);
  }
  
  @Test
  void getEmployeePage_WithNoParameters_ShouldReturnEmployeePage() {
    //given
    int page = 0;
    int size = 10;
    Page<Employee> employeePage = new PageImpl<>(new ArrayList<>());
    
    Pageable pageable = PageRequest.of(page, size);
    
    //when
    when(employeeRepository.findAll(pageable)).thenReturn(employeePage);
    
    Page<Employee> result = employeeService.getEmployeePage(pageable, null, null);
    
    //then
    assertNotNull(result);
    assertEquals(employeePage, result);
    
    verify(employeeRepository, times(1)).findAll(pageable);
  }
}
