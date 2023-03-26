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


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.trae.backend.dto.type.ChangeNameAndActiveReq;
import ru.trae.backend.dto.type.NewTypeWorkDto;
import ru.trae.backend.dto.type.TypeWorkDto;
import ru.trae.backend.service.TypeWorkService;

@ExtendWith(MockitoExtension.class)
class TypeWorkControllerTest {
  @Mock
  private TypeWorkService typeWorkService;
  @InjectMocks
  private TypeWorkController controller;

  @Test
  void types() {
    when(typeWorkService.getTypes()).thenReturn(List.of(
        new TypeWorkDto(1, "type1", true),
        new TypeWorkDto(2, "type2", true)
    ));

    ResponseEntity<List<TypeWorkDto>> response = controller.types();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).asList().contains(
        new TypeWorkDto(1, "type1", true),
        new TypeWorkDto(2, "type2", true)
    );
  }

  @Test
  void typeWorkPersist() {
    when(typeWorkService.saveNewTypeWork(any())).thenReturn(new TypeWorkDto(1, "type1", true));

    ResponseEntity<TypeWorkDto> response = controller.typeWorkPersist(new NewTypeWorkDto("type1"));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void typeWorkChange() {
    when(typeWorkService.getTypeWorkDtoById(anyLong())).thenReturn(new TypeWorkDto(1, "type1", true));

    ResponseEntity<TypeWorkDto> response = controller.typeWorkChange(
        new ChangeNameAndActiveReq(1, "type2", false));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }
}