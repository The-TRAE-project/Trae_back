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

import org.junit.jupiter.api.Test;
import ru.trae.backend.dto.type.TypeWorkDto;
import ru.trae.backend.entity.TypeWork;

class TypeWorkDtoMapperTest {
  
  @Test
  void testApply() {
    //given
    TypeWorkDtoMapper mapper = new TypeWorkDtoMapper();
    
    TypeWork tw = new TypeWork();
    tw.setId(1L);
    tw.setName("Construction");
    tw.setActive(true);
    
    //when
    TypeWorkDto result = mapper.apply(tw);
    
    //then
    assertEquals(tw.getId(), result.id());
    assertEquals(tw.getName(), result.name());
    assertEquals(tw.isActive(), result.isActive());
  }
}
