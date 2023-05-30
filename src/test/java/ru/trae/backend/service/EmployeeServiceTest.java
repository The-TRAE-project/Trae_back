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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
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
import org.springframework.http.HttpStatus;
import ru.trae.backend.dto.PageDto;
import ru.trae.backend.dto.employee.ChangeDataDtoReq;
import ru.trae.backend.dto.employee.EmployeeDto;
import ru.trae.backend.dto.employee.EmployeeIdFirstLastNameDto;
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
  String firstName = "test_first_name";
  String middleName = "test_middle_name";
  String lastName = "test_last_name";
  LocalDate dateOfEmployment = LocalDate.now();
  int pinCode = 123;
  Employee e = new Employee();
  
  @BeforeEach
  public void init() {
    e.setId(employeeId);
    e.setFirstName(firstName);
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
        firstName, middleName, lastName,
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
    assertEquals(firstName, response.firstName());
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
    assertEquals(firstName, result.firstName());
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
  void checkInEmployee_WithShiftEmployee_ShouldUpdateShiftAndReturnShortEmployeeDto() {
    //when
    when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(e));
    when(workingShiftService.employeeOnShift(true, e.getId())).thenReturn(true);
    
    ShortEmployeeDto result = employeeService.checkInEmployee(employeeId);
    
    //then
    assertNotNull(result);
    assertEquals(employeeId, result.id());
    assertEquals(firstName, result.firstName());
    assertEquals(lastName, result.lastName());
    assertTrue(result.onShift());
    
    verify(workingShiftService, times(2)).employeeOnShift(true, e.getId());
  }
  
  @Test
  void checkInEmployee_WithNonShiftEmployee_ShouldUpdateShiftAndReturnShortEmployeeDto() {
    //when
    when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(e));
    when(workingShiftService.employeeOnShift(true, e.getId())).thenReturn(false);
    
    ShortEmployeeDto result = employeeService.checkInEmployee(employeeId);
    
    //then
    assertNotNull(result);
    assertEquals(employeeId, result.id());
    assertEquals(firstName, result.firstName());
    assertEquals(lastName, result.lastName());
    assertFalse(result.onShift());
    
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
    assertEquals(firstName, result.firstName());
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
    assertEquals(firstName, result.firstName());
    assertEquals(lastName, result.lastName());
    assertTrue(result.onShift());
    
    verify(workingShiftService, times(2)).employeeOnShift(true, e.getId());
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
    assertEquals(firstName, result.firstName());
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
  
  @Test
  void testGetEmpDtoById() {
    //given
    EmployeeDto employeeDto = new EmployeeDto(employeeId, null, null,
        null, null, 0, true, null,
        null, null, null);
    
    //when
    when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(e));
    when(employeeDtoMapper.apply(e)).thenReturn(employeeDto);
    
    EmployeeDto result = employeeService.getEmpDtoById(employeeId);
    
    //then
    assertNotNull(result);
    assertEquals(employeeId, result.id());
    
    verify(employeeRepository, times(1)).findById(employeeId);
    verify(employeeDtoMapper, times(1)).apply(e);
  }
  
  @Test
  void testExistsByCredentials() {
    //when
    when(employeeRepository.existsByFirstMiddleLastNameIgnoreCase(firstName, middleName, lastName))
        .thenReturn(true);
    
    boolean result = employeeService.existsByCredentials(firstName, middleName, lastName);
    
    //then
    assertTrue(result);
    
    verify(employeeRepository, times(1)).existsByFirstMiddleLastNameIgnoreCase(firstName, middleName, lastName);
  }
  
  @Test
  void testCheckAvailableCredentials_WhenCredentialsExist_ThrowsEmployeeException() {
    //when
    when(employeeService.existsByCredentials(firstName, middleName, lastName))
        .thenReturn(true);
    
    EmployeeException exception = assertThrows(EmployeeException.class,
        () -> employeeService.checkAvailableCredentials(firstName, middleName, lastName));
    
    //then
    assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    assertEquals("Such credentials are already in use", exception.getMessage());
  }
  
  @Test
  void testCheckAvailableCredentials_WhenCredentialsExist_NotThrowsEmployeeException() {
    //when
    when(employeeService.existsByCredentials(firstName, middleName, lastName))
        .thenReturn(false);
    
    assertDoesNotThrow(() -> employeeService.checkAvailableCredentials(firstName, middleName, lastName));
    
    verify(employeeRepository, times(1)).existsByFirstMiddleLastNameIgnoreCase(firstName, middleName, lastName);
  }
  
  @Test
  void testGetEmployeeDtoPage() {
    //given
    Pageable employeePage = PageRequest.of(0, 10);
    List<Long> typeWorkId = Arrays.asList(1L, 2L, 3L);
    Boolean isActive = true;
    
    Page<Employee> employeePageResult = new PageImpl<>(Collections.singletonList(new Employee()));
    PageDto<EmployeeDto> expectedPageDto = new PageDto<>(Collections.emptyList(), 0L, 0, 0);
    
    when(employeeService.getEmployeePage(employeePage, typeWorkId, isActive)).thenReturn(employeePageResult);
    when(pageToPageDtoMapper.employeePageToPageDto(employeePageResult)).thenReturn(expectedPageDto);
    
    PageDto<EmployeeDto> result = employeeService.getEmployeeDtoPage(employeePage, typeWorkId, isActive);
    
    //then
    assertEquals(expectedPageDto, result);
    verify(pageToPageDtoMapper, times(1)).employeePageToPageDto(employeePageResult);
  }
  
  @Test
  void testGetEmployeeDtoByListId() {
    //given
    List<Long> listEmpId = Arrays.asList(1L, 2L, 3L);
    List<EmployeeIdFirstLastNameDto> expectedDtoList = List.of(new EmployeeIdFirstLastNameDto(employeeId, firstName, lastName));
    
    //when
    when(employeeRepository.findByIdIn(listEmpId)).thenReturn(expectedDtoList);
    
    List<EmployeeIdFirstLastNameDto> result = employeeService.getEmployeeDtoByListId(listEmpId);
    
    //then
    assertEquals(expectedDtoList, result);
    verify(employeeRepository, times(1)).findByIdIn(listEmpId);
  }
  
  @Test
  void testGetAllEmployeeDtoList() {
    //given
    List<EmployeeIdFirstLastNameDto> expectedDtoList = List.of(new EmployeeIdFirstLastNameDto(employeeId, firstName, lastName));
    
    //when
    when(employeeRepository.findAllBy()).thenReturn(expectedDtoList);
    
    List<EmployeeIdFirstLastNameDto> result = employeeService.getAllEmployeeDtoList();
    
    //then
    assertEquals(expectedDtoList, result);
    verify(employeeRepository, times(1)).findAllBy();
  }
  
  @Test
  void testChangeEmployeeDataAndStatusAndPinCodeAndTypesWork_OnlyStatusAndDateOfDismissal() {
    //given
    ChangeDataDtoReq dto = new ChangeDataDtoReq(employeeId, null, null,
        null, null, null, false, null, LocalDate.now(), null);
    EmployeeService spyEmployeeService = spy(employeeService);
    
    //when
    doReturn(e).when(spyEmployeeService).getEmployeeById(dto.employeeId());
    
    spyEmployeeService.changeEmployeeDataAndStatusAndPinCodeAndTypesWork(dto);
    
    //then
    verify(spyEmployeeService, times(1)).getEmployeeById(dto.employeeId());
    verify(employeeRepository, times(1)).save(e);
  }
  
  @Test
  void testChangeEmployeeDataAndStatusAndPinCodeAndTypesWork_OnlyTrueStatus() {
    //given
    e.setActive(false);
    ChangeDataDtoReq dto = new ChangeDataDtoReq(employeeId, null, null,
        null, null, null, true, null, null, null);
    EmployeeService spyEmployeeService = spy(employeeService);
    
    //when
    doReturn(e).when(spyEmployeeService).getEmployeeById(dto.employeeId());
    
    spyEmployeeService.changeEmployeeDataAndStatusAndPinCodeAndTypesWork(dto);
    
    //then
    verify(spyEmployeeService, times(1)).getEmployeeById(dto.employeeId());
    verify(employeeRepository, times(1)).save(e);
  }
  
  @Test
  void testChangeEmployeeDataAndStatusAndPinCodeAndTypesWork_OnlyDateOfDismissal() {
    //given
    e.setActive(false);
    ChangeDataDtoReq dto = new ChangeDataDtoReq(employeeId, null, null,
        null, null, null, null, null, LocalDate.now(), null);
    EmployeeService spyEmployeeService = spy(employeeService);
    
    //when
    doReturn(e).when(spyEmployeeService).getEmployeeById(dto.employeeId());
    
    EmployeeException exception = assertThrows(EmployeeException.class,
        () -> spyEmployeeService.changeEmployeeDataAndStatusAndPinCodeAndTypesWork(dto));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    assertEquals("Date of dismissal without status", exception.getMessage());
  }
  
  @Test
  void testChangeEmployeeDataAndStatusAndPinCodeAndTypesWork_SuchStatus() {
    //given
    ChangeDataDtoReq dto = new ChangeDataDtoReq(employeeId, null, null,
        null, null, null, true, null, null, null);
    EmployeeService spyEmployeeService = spy(employeeService);
    
    //when
    doReturn(e).when(spyEmployeeService).getEmployeeById(dto.employeeId());
    
    EmployeeException exception = assertThrows(EmployeeException.class,
        () -> spyEmployeeService.changeEmployeeDataAndStatusAndPinCodeAndTypesWork(dto));
    assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    assertEquals("The employee already has this status", exception.getMessage());
  }
  
  @Test
  void testChangeEmployeeDataAndStatusAndPinCodeAndTypesWork_FalseStatusWithoutDateOfDismissal() {
    //given
    ChangeDataDtoReq dto = new ChangeDataDtoReq(employeeId, null, null,
        null, null, null, false, null, null, null);
    EmployeeService spyEmployeeService = spy(employeeService);
    
    //when
    doReturn(e).when(spyEmployeeService).getEmployeeById(dto.employeeId());
    
    EmployeeException exception = assertThrows(EmployeeException.class,
        () -> spyEmployeeService.changeEmployeeDataAndStatusAndPinCodeAndTypesWork(dto));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    assertEquals("Incorrect status or missing date of dismissal", exception.getMessage());
  }
  
  @Test
  void testChangeEmployeeDataAndStatusAndPinCodeAndTypesWork_SuchFalseStatus() {
    //given
    e.setActive(false);
    ChangeDataDtoReq dto = new ChangeDataDtoReq(employeeId, null, null,
        null, null, null, false, null, LocalDate.now(), null);
    EmployeeService spyEmployeeService = spy(employeeService);
    
    //when
    doReturn(e).when(spyEmployeeService).getEmployeeById(dto.employeeId());
    
    EmployeeException exception = assertThrows(EmployeeException.class,
        () -> spyEmployeeService.changeEmployeeDataAndStatusAndPinCodeAndTypesWork(dto));
    assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    assertEquals("The employee already has this status", exception.getMessage());
  }
  
  @Test
  void testChangeEmployeeDataAndStatusAndPinCodeAndTypesWork_OnlyDateOfDeployment() {
    //given
    ChangeDataDtoReq dto = new ChangeDataDtoReq(employeeId, null, null,
        null, null, null, null, LocalDate.now(), null, null);
    EmployeeService spyEmployeeService = spy(employeeService);
    
    //when
    doReturn(e).when(spyEmployeeService).getEmployeeById(dto.employeeId());
    
    spyEmployeeService.changeEmployeeDataAndStatusAndPinCodeAndTypesWork(dto);
    
    //then
    verify(spyEmployeeService, times(1)).getEmployeeById(dto.employeeId());
    verify(employeeRepository, times(1)).save(e);
  }
  
  @Test
  void testChangeEmployeeDataAndStatusAndPinCodeAndTypesWork_OnlyPinCode() {
    //given
    ChangeDataDtoReq dto = new ChangeDataDtoReq(employeeId, null, null,
        null, null, 100, null, null, null, null);
    EmployeeService spyEmployeeService = spy(employeeService);
    
    //when
    doReturn(e).when(spyEmployeeService).getEmployeeById(dto.employeeId());
    
    spyEmployeeService.changeEmployeeDataAndStatusAndPinCodeAndTypesWork(dto);
    
    //then
    verify(spyEmployeeService, times(1)).getEmployeeById(dto.employeeId());
    verify(employeeRepository, times(1)).save(e);
  }
  
  @Test
  void testChangeEmployeeDataAndStatusAndPinCodeAndTypesWork_PinCodeThrowException() {
    //given
    ChangeDataDtoReq dto = new ChangeDataDtoReq(employeeId, null, null,
        null, null, pinCode, null, null, null, null);
    EmployeeService spyEmployeeService = spy(employeeService);
    
    //when
    doReturn(e).when(spyEmployeeService).getEmployeeById(dto.employeeId());
    when(spyEmployeeService.existsEmpByPinCode(pinCode)).thenReturn(true);
    
    assertThrows(EmployeeException.class, () -> spyEmployeeService.changeEmployeeDataAndStatusAndPinCodeAndTypesWork(dto));
  }
  
  @Test
  void testChangeEmployeeDataAndStatusAndPinCodeAndTypesWork_OnlyFirstLastMiddleNameAndPhone() {
    //given
    ChangeDataDtoReq dto = new ChangeDataDtoReq(employeeId, "another_first_name", "another_middle_name",
        "another_last_name", "another_phone", null, null,
        null, null, null);
    EmployeeService spyEmployeeService = spy(employeeService);
    
    //when
    doReturn(e).when(spyEmployeeService).getEmployeeById(dto.employeeId());
    
    spyEmployeeService.changeEmployeeDataAndStatusAndPinCodeAndTypesWork(dto);
    
    //then
    verify(spyEmployeeService, times(1)).getEmployeeById(dto.employeeId());
    verify(employeeRepository, times(1)).save(e);
  }
  
  @Test
  void testChangeEmployeeDataAndStatusAndPinCodeAndTypesWork_SuchFirstName() {
    //given
    ChangeDataDtoReq dto = new ChangeDataDtoReq(employeeId, firstName, null,
        null, null, null, null, null, null, null);
    EmployeeService spyEmployeeService = spy(employeeService);
    
    //when
    doReturn(e).when(spyEmployeeService).getEmployeeById(dto.employeeId());
    
    EmployeeException exception = assertThrows(EmployeeException.class,
        () -> spyEmployeeService.changeEmployeeDataAndStatusAndPinCodeAndTypesWork(dto));
    assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    assertEquals("The new first name must not match the current one", exception.getMessage());
  }
  
  @Test
  void testChangeEmployeeDataAndStatusAndPinCodeAndTypesWork_SuchMiddleName() {
    //given
    ChangeDataDtoReq dto = new ChangeDataDtoReq(employeeId, null, middleName,
        null, null, null, null, null, null, null);
    EmployeeService spyEmployeeService = spy(employeeService);
    
    //when
    doReturn(e).when(spyEmployeeService).getEmployeeById(dto.employeeId());
    
    EmployeeException exception = assertThrows(EmployeeException.class,
        () -> spyEmployeeService.changeEmployeeDataAndStatusAndPinCodeAndTypesWork(dto));
    assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    assertEquals("The new middle name must not match the current one", exception.getMessage());
  }
  
  @Test
  void testChangeEmployeeDataAndStatusAndPinCodeAndTypesWork_SuchLastName() {
    //given
    ChangeDataDtoReq dto = new ChangeDataDtoReq(employeeId, null, null,
        lastName, null, null, null, null, null, null);
    EmployeeService spyEmployeeService = spy(employeeService);
    
    //when
    doReturn(e).when(spyEmployeeService).getEmployeeById(dto.employeeId());
    
    EmployeeException exception = assertThrows(EmployeeException.class,
        () -> spyEmployeeService.changeEmployeeDataAndStatusAndPinCodeAndTypesWork(dto));
    assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    assertEquals("The new last name must not match the current one", exception.getMessage());
  }
  
  @Test
  void testChangeEmployeeDataAndStatusAndPinCodeAndTypesWork_SuchPhone() {
    //given
    ChangeDataDtoReq dto = new ChangeDataDtoReq(employeeId, null, null,
        null, phoneNumber, null, null, null, null, null);
    EmployeeService spyEmployeeService = spy(employeeService);
    
    //when
    doReturn(e).when(spyEmployeeService).getEmployeeById(dto.employeeId());
    
    EmployeeException exception = assertThrows(EmployeeException.class,
        () -> spyEmployeeService.changeEmployeeDataAndStatusAndPinCodeAndTypesWork(dto));
    assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    assertEquals("The new phone must not match the current one", exception.getMessage());
  }
  
}
