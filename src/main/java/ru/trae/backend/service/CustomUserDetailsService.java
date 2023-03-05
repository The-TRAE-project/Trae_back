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

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.trae.backend.entity.user.Manager;
import ru.trae.backend.exceptionhandler.exception.ManagerException;

/**
 * Custom implementation of UserDetailService.
 *
 * @author Vladimir Olennikov
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
  private final ManagerService managerService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Manager m = managerService.getManagerByUsername(username);

    if (m.isEnabled()) {
      return m;
    } else {
      throw new ManagerException(HttpStatus.LOCKED,
              "The manager with username: " + username + " is disabled");
    }
  }
}
