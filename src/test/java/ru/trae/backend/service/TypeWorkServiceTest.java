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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import ru.trae.backend.dto.PageDto;
import ru.trae.backend.dto.mapper.PageToPageDtoMapper;
import ru.trae.backend.dto.mapper.TypeWorkDtoMapper;
import ru.trae.backend.dto.type.ChangeNameAndActiveReq;
import ru.trae.backend.dto.type.NewTypeWorkDto;
import ru.trae.backend.dto.type.TypeWorkDto;
import ru.trae.backend.entity.TypeWork;
import ru.trae.backend.exceptionhandler.exception.TypeWorkException;
import ru.trae.backend.repository.TypeWorkRepository;

@ExtendWith(MockitoExtension.class)
class TypeWorkServiceTest {
  @Mock
  private TypeWorkRepository typeWorkRepository;
  @Mock
  TypeWorkDtoMapper typeWorkDtoMapper;
  @Mock
  PageToPageDtoMapper pageToPageDtoMapper;
  @InjectMocks
  private TypeWorkService typeWorkService;
  
  @Test
  void saveNewTypeWork_shouldReturnTypeWorkDto() {
    //given
    NewTypeWorkDto dto = new NewTypeWorkDto("work");
    
    TypeWork tw = new TypeWork();
    tw.setName(dto.name());
    tw.setActive(true);
    tw.setId(2L);
    
    //when
    when(typeWorkRepository.save(any(TypeWork.class))).thenReturn(tw);
    when(typeWorkDtoMapper.apply(tw)).thenReturn(new TypeWorkDto(2L, "work", true));
    
    //then
    TypeWorkDto twDto = typeWorkService.saveNewTypeWork(dto);
    assertNotNull(twDto);
    assertEquals("result: ", dto.name(), twDto.name());
  }
  
  @Test
  void getTypeWorkById_whenTypeWorkExist_returnTypeWork() {
    // given
    long typeWorkId = 1L;
    TypeWork expectedTypeWork = new TypeWork();
    when(typeWorkRepository.findById(typeWorkId)).thenReturn(Optional.of(expectedTypeWork));
    
    // when
    TypeWork actualTypeWork = typeWorkService.getTypeWorkById(typeWorkId);
    
    // then
    assertEquals("result: ", expectedTypeWork, actualTypeWork);
  }
  
  @Test
  void getTypeWorkById_whenTypeWorkNotExist_throwTypeWorkException() {
    // given
    long typeWorkId = 1L;
    when(typeWorkRepository.findById(typeWorkId)).thenReturn(Optional.empty());
    
    // when
    // then
    assertThrows(TypeWorkException.class, () -> typeWorkService.getTypeWorkById(typeWorkId));
  }
  
  @Test
  void getTypeWorkDtoById_shouldReturnTypeWorkDto() {
    // given
    long typeWorkId = 1L;
    TypeWork typeWork = new TypeWork();
    TypeWorkDto expectedTypeWorkDto = new TypeWorkDto(typeWorkId, "work", true);
    
    // when
    when(typeWorkRepository.findById(typeWorkId)).thenReturn(Optional.of(typeWork));
    when(typeWorkDtoMapper.apply(typeWork)).thenReturn(expectedTypeWorkDto);
    
    // then
    TypeWorkDto actualTypeWorkDto = typeWorkService.getTypeWorkDtoById(typeWorkId);
    assertEquals("result: ", expectedTypeWorkDto, actualTypeWorkDto);
  }
  
  @Test
  void changeNameAndActive() {
    // given
    ChangeNameAndActiveReq request =
        new ChangeNameAndActiveReq(2L, "newWork", true);
    
    // when
    when(typeWorkRepository.existsById(2L)).thenReturn(true);
    when(typeWorkRepository.getTypeWorkNameById(2L)).thenReturn("work");
    doNothing().when(typeWorkRepository).updateNameById("newWork", 2L);
    doNothing().when(typeWorkRepository).updateIsActiveById(true, 2L);
    
    // then
    typeWorkService.changeNameAndActive(request);
    verify(typeWorkRepository, times(1)).updateNameById("newWork", 2L);
    verify(typeWorkRepository, times(1)).updateIsActiveById(true, 2L);
  }
  
  @Test
  void changeNameAndActive_onlyName() {
    // given
    ChangeNameAndActiveReq request =
        new ChangeNameAndActiveReq(2L, "newWork", null);
    
    // when
    when(typeWorkRepository.existsById(2L)).thenReturn(true);
    when(typeWorkRepository.getTypeWorkNameById(2L)).thenReturn("work");
    doNothing().when(typeWorkRepository).updateNameById("newWork", 2L);
    
    // then
    typeWorkService.changeNameAndActive(request);
    verify(typeWorkRepository, times(1)).updateNameById("newWork", 2L);
  }
  
  @Test
  void changeNameAndActive_onlyActive() {
    // given
    ChangeNameAndActiveReq request =
        new ChangeNameAndActiveReq(2L, null, true);
    
    // when
    when(typeWorkRepository.existsById(2L)).thenReturn(true);
    doNothing().when(typeWorkRepository).updateIsActiveById(true, 2L);
    
    // then
    typeWorkService.changeNameAndActive(request);
    verify(typeWorkRepository, times(1)).updateIsActiveById(true, 2L);
  }
  
  @Test
  void changeNameAndActive_noNameAndActive() {
    // given
    ChangeNameAndActiveReq request = new ChangeNameAndActiveReq(2L, null, null);
    
    // when
    try {
      typeWorkService.changeNameAndActive(request);
    } catch (TypeWorkException e) {
      
      // then
      assertThat(e.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
      assertThat(e.getMessage()).isEqualTo(
          "The availability of the type work or the new name are not specified");
    }
  }
  
  @Test
  void changeNameAndActive_conflictName() {
    // given
    ChangeNameAndActiveReq request =
        new ChangeNameAndActiveReq(2L, "work", null);
    
    // when
    when(typeWorkRepository.existsById(2L)).thenReturn(true);
    when(typeWorkRepository.getTypeWorkNameById(2L)).thenReturn("work");
    
    // when
    try {
      typeWorkService.changeNameAndActive(request);
      fail();
    } catch (TypeWorkException e) {
      // then
      assertThat(e.getStatus()).isEqualTo(HttpStatus.CONFLICT);
      assertThat(e.getMessage()).isEqualTo("This type work already have name: work");
    }
  }
  
  @Test
  void changeNameAndActive_conflictActive() {
    // given
    ChangeNameAndActiveReq request =
        new ChangeNameAndActiveReq(2L, null, true);
    
    // when
    when(typeWorkRepository.existsById(2L)).thenReturn(true);
    when(typeWorkRepository.getTypeWorkActiveById(2L)).thenReturn(true);
    
    // when
    try {
      typeWorkService.changeNameAndActive(request);
      fail();
    } catch (TypeWorkException e) {
      // then
      assertThat(e.getStatus()).isEqualTo(HttpStatus.CONFLICT);
      assertThat(e.getMessage()).isEqualTo("This type work already have active: true");
    }
  }
  
  @Test
  void changeNameAndActive_typeWorkNotFound() {
    // given
    ChangeNameAndActiveReq request = new ChangeNameAndActiveReq(1L, "newWork", true);
    
    when(typeWorkRepository.existsById(1L)).thenReturn(false);
    
    // when
    try {
      typeWorkService.changeNameAndActive(request);
      fail();
    } catch (TypeWorkException e) {
      // then
      assertThat(e.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
      assertThat(e.getMessage()).isEqualTo("Type work with ID: 1 not found");
    }
  }
  
  @Test
  void getTypeWorkByNameTest() {
    //given
    TypeWork typeWork = new TypeWork();
    typeWork.setName("typeWork");
    
    //when
    when(typeWorkRepository.findByName("typeWork")).thenReturn(Optional.of(typeWork));
    TypeWork result = typeWorkService.getTypeWorkByName("typeWork");
    
    //then
    assertEquals("result: ", "typeWork", result.getName());
  }
  
  @Test
  void getTypeWorkByNameExceptionTest() {
    //when
    when(typeWorkRepository.findByName("typeWork")).thenReturn(Optional.empty());
    
    //then
    assertThrows(TypeWorkException.class, () -> typeWorkService.getTypeWorkByName("typeWork"));
  }
  
  @Test
  void getTypes_ShouldReturnListOfTypeWorkDtos() {
    // given
    when(typeWorkRepository.findByIsActiveTrue()).thenReturn(Collections.emptyList());
    
    // when
    List<TypeWorkDto> result = typeWorkService.getTypes();
    
    // then
    assertThat(result).isNotNull();
  }
  
  @Test
  void getTypes_ShouldReturnListOfTypeWorkDtos_WithoutShipment() {
    // given
    when(typeWorkRepository.findByIsActiveTrueAndIdNot(1)).thenReturn(Collections.emptyList());
    
    // when
    List<TypeWorkDto> result = typeWorkService.getTypesWithoutShipment();
    
    // then
    assertThat(result).isNotNull();
  }
  
  @Test
  void checkAvailableByName_shouldThrowException_whenNameExists() {
    // given
    String name = "name";
    when(typeWorkRepository.existsByNameIgnoreCase(name)).thenReturn(true);
    
    // when
    Throwable throwable = assertThrows(TypeWorkException.class, () ->
        typeWorkService.checkAvailableByName(name));
    
    // then
    assertThat(throwable.getMessage()).isEqualTo
        ("Type work name: " + name + " already in use");
  }
  
  @Test
  void shouldReturnTypeWorkPageWhenGetTypeWorkPage() {
    //given
    Pageable typeWorkPage = PageRequest.of(0, 10);
    Page<TypeWork> expectedTypeWorkPage = Page.empty();
    when(typeWorkRepository.findAll(typeWorkPage)).thenReturn(expectedTypeWorkPage);
    
    //when
    Page<TypeWork> typeWorkPageResult = typeWorkService.getTypeWorkPage(typeWorkPage, null);
    
    //then
    assertEquals("result: ", expectedTypeWorkPage, typeWorkPageResult);
  }
  
  @Test
  void shouldReturnTypeWorkPageWhenGetTypeWorkPageWithIsActive() {
    //given
    Pageable typeWorkPage = PageRequest.of(0, 10);
    boolean isActive = true;
    Page<TypeWork> expectedTypeWorkPage = Page.empty();
    when(typeWorkRepository.findByIsActive(isActive, typeWorkPage)).thenReturn(expectedTypeWorkPage);
    
    //when
    Page<TypeWork> typeWorkPageResult = typeWorkService.getTypeWorkPage(typeWorkPage, isActive);
    
    //then
    assertEquals("result: ", expectedTypeWorkPage, typeWorkPageResult);
  }
  
  @Test
  void shouldReturnTypeWorkDtoPageWhenGetTypeWorkDtoPage() {
    //given
    Pageable typeWorkPage = PageRequest.of(0, 10);
    Page<TypeWork> typeWorkPageResult = Page.empty();
    PageDto<TypeWorkDto> expectedTypeWorkDtoPage = new PageDto<>(Collections.emptyList(), 0, 0, 0);
    when(typeWorkRepository.findAll(typeWorkPage)).thenReturn(typeWorkPageResult);
    when(pageToPageDtoMapper.typeWorkPageToPageDto(typeWorkPageResult))
        .thenReturn(expectedTypeWorkDtoPage);
    
    //when
    PageDto<TypeWorkDto> typeWorkDtoPageResult = typeWorkService.getTypeWorkDtoPage(typeWorkPage, null);
    
    //then
    assertEquals("result: ", expectedTypeWorkDtoPage, typeWorkDtoPageResult);
  }
}