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
import ru.trae.backend.dto.manager.ManagerShortDto;
import ru.trae.backend.entity.user.Manager;

import static org.junit.jupiter.api.Assertions.*;

class ManagerShortDtoMapperTest {
  
  @Test
  void testApply() {
    //given
    ManagerShortDtoMapper mapper = new ManagerShortDtoMapper();
    
    Manager m = new Manager();
    m.setId(1L);
    m.setLastName("Doe");
    m.setFirstName("John");
    
    //when
    ManagerShortDto result = mapper.apply(m);
    
    //then
    assertEquals(m.getId(), result.managerId());
    assertEquals(m.getLastName(), result.lastName());
    assertEquals(m.getFirstName(), result.firstName());
  }
}
