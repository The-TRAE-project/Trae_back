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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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

  /**
   * Saves a new TypeWork entity.
   *
   * @param dto the NewTypeWorkDto to save
   * @return the saved TypeWork entity
   */
  public TypeWork saveNewTypeWork(NewTypeWorkDto dto) {
    TypeWork tw = new TypeWork();
    tw.setName(dto.name());

    return typeWorkRepository.save(tw);
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

  /**
   * Gets a list of all TypeWork entities.
   *
   * @return the list of TypeWorkDto
   */
  public List<TypeWorkDto> getTypes() {
    return typeWorkRepository.findAll()
            .stream()
            .map(t -> new TypeWorkDto(t.getId(), t.getName()))
            .toList();
  }

  /**
   * Checks if a TypeWork entity with the given name exists.
   *
   * @param name the name to check
   * @return true if the TypeWork entity exists, false otherwise
   */
  public boolean existsTypeByName(String name) {
    return typeWorkRepository.existsByNameIgnoreCase(name);
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
