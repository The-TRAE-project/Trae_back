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

import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.type.TypeWorkDto;
import ru.trae.backend.entity.TypeWork;

/**
 * This class is a mapper for TypeWork entities to TypeWorkDto objects.
 *
 * @author Vladimir Olennikov
 */
@Service
@RequiredArgsConstructor
public class TypeWorkDtoMapper implements Function<TypeWork, TypeWorkDto> {

  @Override
  public TypeWorkDto apply(TypeWork tw) {
    return new TypeWorkDto(tw.getId(), tw.getName(), tw.isActive());
  }
}
