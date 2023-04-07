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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.trae.backend.dto.PageDto;
import ru.trae.backend.dto.type.ChangeNameAndActiveReq;
import ru.trae.backend.dto.type.NewTypeWorkDto;
import ru.trae.backend.dto.type.TypeWorkDto;
import ru.trae.backend.service.TypeWorkService;
import ru.trae.backend.util.PageSettings;

@ExtendWith(MockitoExtension.class)
class TypeWorkControllerTest {
  @Mock
  private TypeWorkService typeWorkService;
  @InjectMocks
  private TypeWorkController controller;

  @Test
  void testTypes() {
    //given
    PageSettings pageSetting = new PageSettings();
    pageSetting.setPage(10);
    pageSetting.setElementPerPage(20);
    pageSetting.buildTypeWorkSort();
    pageSetting.setDirection("asc");

    TypeWorkDto twDto = new TypeWorkDto(1L, "Type", true);
    PageDto<TypeWorkDto> typeWorkDtoPage =
        new PageDto<>(Collections.singletonList(twDto), 100L, 11, 10);
    when(typeWorkService.getTypeWorkDtoPage(any(), anyBoolean())).thenReturn(typeWorkDtoPage);

    ResponseEntity<PageDto<TypeWorkDto>> responseEntity = controller.types(pageSetting, true);

    //then
    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getBody()).isNotNull();
    assertThat(responseEntity.getBody()).isEqualTo(typeWorkDtoPage);
  }

  @Test
  void typeWorkPersist() {
    //when
    when(typeWorkService.saveNewTypeWork(any())).thenReturn(new TypeWorkDto(1, "type1", true));

    ResponseEntity<TypeWorkDto> response = controller.typeWorkPersist(new NewTypeWorkDto("type1"));

    //then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void typeWorkChange() {
    //when
    when(typeWorkService.getTypeWorkDtoById(anyLong())).thenReturn(new TypeWorkDto(1, "type1", true));

    ResponseEntity<TypeWorkDto> response = controller.typeWorkChange(
        new ChangeNameAndActiveReq(1, "type2", false));

    //then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void activeTypes() {
    //given
    List<TypeWorkDto> typeWorkDtos = new ArrayList<>();
    typeWorkDtos.add(new TypeWorkDto(1L, "test", true));

    //when
    when(typeWorkService.getTypes()).thenReturn(typeWorkDtos);

    ResponseEntity<List<TypeWorkDto>> response = controller.activeTypes();

    //then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(typeWorkDtos, response.getBody());
  }
}