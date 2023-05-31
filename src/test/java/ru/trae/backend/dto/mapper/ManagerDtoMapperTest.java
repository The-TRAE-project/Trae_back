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

import org.junit.jupiter.api.Test;
import ru.trae.backend.dto.manager.ManagerDto;
import ru.trae.backend.entity.user.Manager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import ru.trae.backend.util.Role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ManagerDtoMapperTest {
  
  @Test
  void apply_ShouldMapManagerToManagerDto_WithoutDateOfDismissal() {
    //given
    Manager manager = new Manager();
    manager.setId(1L);
    manager.setFirstName("test_first_name");
    manager.setLastName("test_last_name");
    manager.setPhone("1234567890");
    manager.setRole(Role.ROLE_MANAGER);
    manager.setUsername("test_username");
    manager.setAccountNonLocked(true);
    manager.setDateOfEmployment(LocalDate.of(2022, 1, 1));
    manager.setDateOfDismissal(null);
    
    ManagerDtoMapper managerDtoMapper = new ManagerDtoMapper();
    
    String dateFormat = "yyyy-MM-dd";
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
    String expectedDateOfEmployment = manager.getDateOfEmployment().format(dateFormatter);
    
    ManagerDto result = managerDtoMapper.apply(manager);
    
    // Verify
    assertEquals(manager.getId(), result.id());
    assertEquals(manager.getFirstName(), result.firstName());
    assertEquals(manager.getLastName(), result.lastName());
    assertEquals(manager.getPhone(), result.phone());
    assertEquals(manager.getRole().value, result.role());
    assertEquals(manager.getUsername(), result.username());
    assertEquals(manager.isAccountNonLocked(), result.status());
    assertEquals(expectedDateOfEmployment, result.dateOfEmployment());
    assertNull(result.dateOfDismissal());
  }
  
  @Test
  void apply_ShouldMapManagerToManagerDto_WithDateOfDismissal() {
    //given
    Manager manager = new Manager();
    manager.setId(1L);
    manager.setFirstName("test_first_name");
    manager.setLastName("test_last_name");
    manager.setPhone("1234567890");
    manager.setRole(Role.ROLE_MANAGER);
    manager.setUsername("test_username");
    manager.setAccountNonLocked(false);
    manager.setDateOfEmployment(LocalDate.of(2022, 1, 1));
    manager.setDateOfDismissal(LocalDate.of(2022, 2, 2));
    
    ManagerDtoMapper managerDtoMapper = new ManagerDtoMapper();
    
    String dateFormat = "yyyy-MM-dd";
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
    String expectedDateOfEmployment = manager.getDateOfEmployment().format(dateFormatter);
    String expectedDateOfDismissal = manager.getDateOfDismissal().format(dateFormatter);
    
    ManagerDto result = managerDtoMapper.apply(manager);
    
    // Verify
    assertEquals(manager.getId(), result.id());
    assertEquals(manager.getFirstName(), result.firstName());
    assertEquals(manager.getLastName(), result.lastName());
    assertEquals(manager.getPhone(), result.phone());
    assertEquals(manager.getRole().value, result.role());
    assertEquals(manager.getUsername(), result.username());
    assertEquals(manager.isAccountNonLocked(), result.status());
    assertEquals(expectedDateOfEmployment, result.dateOfEmployment());
    assertEquals(expectedDateOfDismissal, result.dateOfDismissal());
  }
}
