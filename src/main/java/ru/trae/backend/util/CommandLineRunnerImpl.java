/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.util;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.trae.backend.entity.user.Manager;
import ru.trae.backend.repository.ManagerRepository;

/**
 * Utility class for filling the database with temporary data.
 *
 * @author Vladimir Olennikov
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommandLineRunnerImpl implements CommandLineRunner {
  private final ManagerRepository managerRepository;
  private final BCryptPasswordEncoder encoder;
  
  @Override
  public void run(String... args) {
    insertAdmin();
  }
  
  /**
   * Inserting admin data.
   */
  public void insertAdmin() {
    if (managerRepository.existsByUsernameIgnoreCase("admin")) {
      return;
    }
    
    String encodedPass = encoder.encode("TopSec");
    
    Manager m = new Manager();
    m.setFirstName("admin");
    m.setMiddleName("admin");
    m.setLastName("admin");
    m.setPhone("+0 (000) 000 0000");
    m.setUsername("admin");
    m.setPassword(encodedPass);
    m.setRole(Role.ROLE_ADMINISTRATOR);
    m.setDateOfRegister(LocalDate.now());
    m.setDateOfEmployment(LocalDate.now());
    m.setDateOfDismissal(null);
    
    m.setEnabled(true);
    m.setAccountNonExpired(true);
    m.setAccountNonLocked(true);
    m.setCredentialsNonExpired(true);
    
    managerRepository.save(m);
  }
}
