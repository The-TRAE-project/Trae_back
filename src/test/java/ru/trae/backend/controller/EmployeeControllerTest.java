/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.trae.backend.dto.PageDto;
import ru.trae.backend.dto.employee.ChangeDataDtoReq;
import ru.trae.backend.dto.employee.EmployeeDto;
import ru.trae.backend.dto.employee.EmployeeIdFirstLastNameDto;
import ru.trae.backend.dto.employee.EmployeeRegisterDtoReq;
import ru.trae.backend.dto.employee.EmployeeRegisterDtoResp;
import ru.trae.backend.dto.employee.ShortEmployeeDto;
import ru.trae.backend.service.EmployeeService;
import ru.trae.backend.util.PageSettings;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {
  @Mock
  private EmployeeService employeeService;
  @InjectMocks
  private EmployeeController employeeController;
  private final Long employeeId = 1L;
  private final String firstName = "test_first_name";
  private final String lastName = "test_last_name";
  
  @Test
  void employeeLogin_WhenValidPin_ShouldReturnShortEmployeeDto() {
    //given
    int pin = 123;
    ShortEmployeeDto expectedDto = new ShortEmployeeDto(employeeId, firstName, lastName, true);
    
    //when
    when(employeeService.employeeLogin(pin)).thenReturn(expectedDto);
    
    ResponseEntity<ShortEmployeeDto> response = employeeController.employeeLogin(pin);
    
    //then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedDto, response.getBody());
    verify(employeeService).employeeLogin(pin);
  }
  
  @Test
  void employeeCheckIn_WhenValidEmployeeId_ShouldReturnShortEmployeeDto() {
    //given
    long employeeId = 123;
    ShortEmployeeDto expectedDto = new ShortEmployeeDto(employeeId, firstName, lastName, true);
    
    //when
    when(employeeService.checkInEmployee(employeeId)).thenReturn(expectedDto);
    
    ResponseEntity<ShortEmployeeDto> response = employeeController.employeeCheckIn(employeeId);
    
    //then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedDto, response.getBody());
    verify(employeeService).checkInEmployee(employeeId);
  }
  
  @Test
  void employeeCheckOut_WhenValidEmployeeId_ShouldReturnShortEmployeeDto() {
    //given
    ShortEmployeeDto expectedDto = new ShortEmployeeDto(employeeId, firstName, lastName, false);
    
    //when
    when(employeeService.departureEmployee(employeeId)).thenReturn(expectedDto);
    
    ResponseEntity<ShortEmployeeDto> response = employeeController.employeeCheckOut(employeeId);
    
    //then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedDto, response.getBody());
    assertFalse(expectedDto.onShift());
    verify(employeeService).departureEmployee(employeeId);
  }
  
  @Test
  void employeesWithPagination_WhenValidParameters_ShouldReturnPageDto() {
    //given
    PageSettings pageSettings = new PageSettings();
    List<Long> typeWorkId = Collections.singletonList(1L);
    Boolean isActive = true;
    
    Sort employeeSort = pageSettings.buildManagerOrEmpSort();
    Pageable employeePage = PageRequest.of(pageSettings.getPage(), pageSettings.getElementPerPage(), employeeSort);
    PageDto<EmployeeDto> expectedPageDto = new PageDto<>(Collections.emptyList(), 1L, 1L, 1);
    
    //when
    when(employeeService.getEmployeeDtoPage(employeePage, typeWorkId, isActive)).thenReturn(expectedPageDto);
    
    ResponseEntity<PageDto<EmployeeDto>> response = employeeController.employeesWithPagination(pageSettings, typeWorkId, isActive);
    
    //then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedPageDto, response.getBody());
    verify(employeeService).getEmployeeDtoPage(employeePage, typeWorkId, isActive);
  }
  
  @Test
  void employeesForReportWithoutPagination_WhenValidParameters_ShouldReturnEmployeeIdFirstLastNameDtoList() {
    //given
    Set<Long> projectIds = Collections.singleton(1L);
    Set<Long> operationIds = Collections.singleton(2L);
    
    List<EmployeeIdFirstLastNameDto> expectedDtoList = Collections.singletonList(
        new EmployeeIdFirstLastNameDto(1L, firstName, lastName));
    
    //when
    when(employeeService.getEmployeeIdFirstLastNameDtoList(projectIds, operationIds))
        .thenReturn(expectedDtoList);
    
    ResponseEntity<List<EmployeeIdFirstLastNameDto>> response =
        employeeController.employeesForReportWithoutPagination(projectIds, operationIds);
    
    //then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedDtoList, response.getBody());
    verify(employeeService).getEmployeeIdFirstLastNameDtoList(projectIds, operationIds);
  }
  
  @Test
  void register_WhenValidDto_ShouldReturnEmployeeRegisterDtoResp() {
    //given
    EmployeeRegisterDtoReq dto = new EmployeeRegisterDtoReq(
        firstName, "test_middle_name", lastName, "+7 (999) 999 9999", LocalDate.now(), List.of(1L, 2L));
    
    EmployeeRegisterDtoResp expectedResponse = new EmployeeRegisterDtoResp(firstName, lastName, 123);
    
    //when
    when(employeeService.saveNewEmployee(dto)).thenReturn(expectedResponse);
    
    ResponseEntity<EmployeeRegisterDtoResp> response = employeeController.register(dto);
    
    //then
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(expectedResponse, response.getBody());
    verify(employeeService).checkAvailableCredentials(dto.firstName(), dto.middleName(), dto.lastName());
    verify(employeeService).saveNewEmployee(dto);
  }
  
  @Test
  void changeData_WhenValidDto_ShouldReturnEmployeeDto() {
    //given
    ChangeDataDtoReq dto = new ChangeDataDtoReq(1L, "another_first_name",
        null, null, null, null, null,
        null, null, null);
    
    EmployeeDto expectedDto = new EmployeeDto(1L, "another_first_name", "test_middle_name",
        lastName, "+7 (999) 999 9999", 123, true, LocalDate.now(),
        LocalDate.now(), null, Collections.emptyList());
    
    //when
    when(employeeService.getEmpDtoById(dto.employeeId())).thenReturn(expectedDto);
    
    ResponseEntity<EmployeeDto> response = employeeController.changeData(dto);
    
    //then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedDto, response.getBody());
    verify(employeeService).changeEmployeeDataAndStatusAndPinCodeAndTypesWork(dto);
    verify(employeeService).getEmpDtoById(dto.employeeId());
  }
  
}

