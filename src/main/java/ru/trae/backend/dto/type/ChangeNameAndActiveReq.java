/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.dto.type;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import ru.trae.backend.util.RegExpression;

/**
 * This class defines the request parameters for changing the name and active of a type work.
 * It contains the type work ID, the new type work name, the active of the type work.
 *
 * @author Vladimir Olennikov
 */
public record ChangeNameAndActiveReq(
    @Schema(description = "Идентификатор типа работ")
    @NotNull(message = "Invalid type work id: id is NULL")
    @Min(value = 0, message = "The type work id cannot be less than 0")
    @Max(value = Integer.MAX_VALUE, message =
        "The type work id cannot be more than " + Integer.MAX_VALUE)
    long typeWorkId,
    @Schema(description = "Новое название типа работы")
    @Pattern(regexp = RegExpression.TYPE_WORK_NAME, message = "Invalid name format")
    String newName,
    @Schema(description = "Состояние типа работа (откл/вкл)")
    Boolean isActive
) {
}
