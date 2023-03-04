/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.util.Role;

/**
 * The Manager class is a concrete implementation of the User interface. This class represents
 * the manager
 * user type and contains information about the manager's role and list of orders and projects
 * that the manager is responsible for.
 *
 * @author Vladimir Olennikov
 */
@Entity
@Getter
@Setter
@Table(name = "managers")
public class Manager extends User implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  @Size(min = 3, max = 20)
  @Column(name = "username", unique = true, nullable = false, columnDefinition = "varchar(20)")
  private String username;
  @Column(name = "password", nullable = false)
  private String password;
  @JsonIgnore
  @Column(name = "account_non_expired")
  private boolean accountNonExpired;
  @JsonIgnore
  @Column(name = "account_non_locked")
  private boolean accountNonLocked;
  @JsonIgnore
  @Column(name = "credentials_non_expired")
  private boolean credentialsNonExpired;
  @JsonIgnore
  @Column(name = "enabled")
  private boolean enabled;
  @Column(name = "role", nullable = false)
  private Role role;
  @ToString.Exclude
  @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
  private List<Project> projects = new ArrayList<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Manager manager = (Manager) o;
    return Objects.equals(id, manager.id) && Objects.equals(username, manager.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(role.toString()));
    return authorities;
  }

  @Override
  public boolean isAccountNonExpired() {
    return accountNonExpired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return accountNonLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return credentialsNonExpired;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }
}
