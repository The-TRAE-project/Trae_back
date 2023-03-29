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

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.trae.backend.dto.PageDto;
import ru.trae.backend.dto.mapper.PageToPageDtoMapper;
import ru.trae.backend.dto.mapper.TypeWorkDtoMapper;
import ru.trae.backend.dto.type.ChangeNameAndActiveReq;
import ru.trae.backend.dto.type.NewTypeWorkDto;
import ru.trae.backend.dto.type.TypeWorkDto;
import ru.trae.backend.entity.TypeWork;
import ru.trae.backend.exceptionhandler.exception.TypeWorkException;
import ru.trae.backend.repository.TypeWorkRepository;

/**
 * Service class that handles the operations related to TypeWork entities.
 *
 * @author Vladimir Olennikov
 */
@Service
@RequiredArgsConstructor
public class TypeWorkService {
  private final TypeWorkRepository typeWorkRepository;
  private final TypeWorkDtoMapper typeWorkDtoMapper;
  private final PageToPageDtoMapper pageToPageDtoMapper;

  /**
   * Saves a new TypeWork entity.
   *
   * @param dto the NewTypeWorkDto to save
   * @return the saved TypeWork entity
   */
  public TypeWorkDto saveNewTypeWork(NewTypeWorkDto dto) {
    TypeWork tw = new TypeWork();
    tw.setName(dto.name());
    tw.setActive(true);

    return typeWorkDtoMapper.apply(typeWorkRepository.save(tw));
  }

  /**
   * Gets a TypeWork entity by its ID.
   *
   * @param id the ID of the TypeWork entity
   * @return the TypeWork entity
   */
  public TypeWork getTypeWorkById(long id) {
    return typeWorkRepository.findById(id).orElseThrow(
        () -> new TypeWorkException(HttpStatus.NOT_FOUND,
            "Type work with ID: " + id + " not found")
    );
  }

  public TypeWorkDto getTypeWorkDtoById(long typeWorkId) {
    return typeWorkDtoMapper.apply(getTypeWorkById(typeWorkId));
  }

  /**
   * This method changes type work name or active using request.
   *
   * @param request The ChangeNameAndActiveReq request
   * @throws TypeWorkException if the newName or isActive is null
   *                           or if type work with ID not found
   */
  @Transactional
  public void changeNameAndActive(ChangeNameAndActiveReq request) {
    if (request.newName() == null && request.isActive() == null) {
      throw new TypeWorkException(HttpStatus.BAD_REQUEST,
          "Не указаны доступность типа работы или новое название");
    }
    if (!typeWorkRepository.existsById(request.typeWorkId())) {
      throw new TypeWorkException(HttpStatus.NOT_FOUND,
          "Type work with ID: " + request.typeWorkId() + " not found");
    }

    if (request.newName() != null) {
      changeTypeWorkName(request.newName(), request.typeWorkId());
    }

    if (request.isActive() != null) {
      changeTypeWorkActive(request.isActive(), request.typeWorkId());
    }
  }

  /**
   * Change type work active.
   *
   * @param newActive  the new active
   * @param typeWorkId the type work id
   */
  private void changeTypeWorkActive(boolean newActive, long typeWorkId) {
    boolean currentActive = typeWorkRepository.getTypeWorkActiveById(typeWorkId);
    if (currentActive == newActive) {
      throw new TypeWorkException(HttpStatus.CONFLICT,
          "This type work already have active: " + currentActive);
    }

    typeWorkRepository.updateIsActiveById(newActive, typeWorkId);
  }

  /**
   * Change type work name.
   *
   * @param newName    the new name
   * @param typeWorkId the type work id
   */
  private void changeTypeWorkName(String newName, long typeWorkId) {
    checkAvailableByName(newName);

    String currentName = typeWorkRepository.getTypeWorkNameById(typeWorkId);
    if (currentName.equals(newName.trim())) {
      throw new TypeWorkException(HttpStatus.CONFLICT,
          "This type work already have name: " + currentName);
    }

    typeWorkRepository.updateNameById(newName.trim(), typeWorkId);
  }

  /**
   * Gets a TypeWork entity by its name.
   *
   * @param name the name of the TypeWork entity
   * @return the TypeWork entity
   */
  public TypeWork getTypeWorkByName(String name) {
    return typeWorkRepository.findByName(name).orElseThrow(
        () -> new TypeWorkException(HttpStatus.NOT_FOUND,
            "Type work with name: " + name + " not found")
    );
  }

  /**
   * Gets a list of all TypeWork entities.
   *
   * @return the list of TypeWorkDto
   */
  public List<TypeWorkDto> getTypes() {
    return typeWorkRepository.findAll()
        .stream()
        .map(typeWorkDtoMapper)
        .toList();
  }

  public Page<TypeWork> getTypeWorkPage(Pageable typeWorkPage, Boolean isActive) {
    Page<TypeWork> page;

    if (isActive != null) {
      page = typeWorkRepository.findByIsActive(isActive, typeWorkPage);
    } else {
      page = typeWorkRepository.findAll(typeWorkPage);
    }
    return page;
  }

  public PageDto<TypeWorkDto> getTypeWorkDtoPage(Pageable typeWorkPage, Boolean status) {
    return pageToPageDtoMapper.typeWorkPageToPageDto(getTypeWorkPage(typeWorkPage, status));
  }

  /**
   * Checks if a TypeWork entity with the given name exists.
   *
   * @param name the name to check
   * @return true if the TypeWork entity exists, false otherwise
   */
  public boolean existsTypeByName(String name) {
    return typeWorkRepository.existsByNameIgnoreCase(name.trim());
  }

  /**
   * Throws an exception if a TypeWork entity with the given name exists.
   *
   * @param name the name to check
   */
  public void checkAvailableByName(String name) {
    if (existsTypeByName(name)) {
      throw new TypeWorkException(HttpStatus.CONFLICT,
          "Type work name: " + name + " already in use");
    }
  }
}
