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
import ru.trae.backend.dto.operation.OperationInfoForProjectTemplateDto;
import ru.trae.backend.entity.task.Operation;

class OperationInfoForProjectTemplateDtoMapperTest {
  
  @Test
  void testApply() {
    //given
    OperationInfoForProjectTemplateDtoMapper mapper = new OperationInfoForProjectTemplateDtoMapper();
    
    Operation o = new Operation();
    o.setName("Test Operation");
    o.setEnded(true);
    o.setInWork(true);
    o.setReadyToAcceptance(false);
    
    //when
    OperationInfoForProjectTemplateDto result = mapper.apply(o);
    
    //then
    assertEquals(o.getName(), result.name());
    assertEquals(o.isEnded(), result.isEnded());
    assertEquals(o.isInWork(), result.inWork());
    assertEquals(o.isReadyToAcceptance(), result.readyToAcceptance());
  }
}
