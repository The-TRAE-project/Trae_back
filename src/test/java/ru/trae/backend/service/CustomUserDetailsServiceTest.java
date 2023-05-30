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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.trae.backend.entity.user.Manager;
import ru.trae.backend.repository.ManagerRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
  @Mock
  private ManagerRepository managerRepository;
  
  @InjectMocks
  private CustomUserDetailsService userDetailsService;
  
  @Test
  void loadUserByUsername_WhenManagerExists_ShouldReturnUserDetails() {
    String username = "test_username";
    Manager manager = new Manager();
    manager.setUsername(username);
    
    when(managerRepository.findByUsername(username)).thenReturn(Optional.of(manager));
    
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    
    assertNotNull(userDetails);
    assertEquals(username, userDetails.getUsername());
    
    verify(managerRepository, times(1)).findByUsername(username);
  }
  
  @Test
  void loadUserByUsername_WhenManagerDoesNotExist_ShouldThrowUsernameNotFoundException() {
    String username = "test_username";
    
    when(managerRepository.findByUsername(username)).thenReturn(Optional.empty());
    
    assertThrows(UsernameNotFoundException.class,
        () -> userDetailsService.loadUserByUsername(username));
    
    verify(managerRepository, times(1)).findByUsername(username);
  }
}