/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.dto.operation;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import ru.trae.backend.util.RegExpression;

/**
 * The {@code InsertingOperationDto} class is a data transfer object
 * used to represent a new operation to be inserted into the database.
 *
 * @author Vladimir Olennikov
 */
public record InsertingOperationDto(
    @NotNull(message = "Invalid project id: id is NULL")
    @Min(value = 0, message = "The project id cannot be less than 0")
    @Max(value = Integer.MAX_VALUE, message =
        "The project id cannot be more than " + Integer.MAX_VALUE)
    long projectId,
    @Schema(description = "Название этапа")
    @Pattern(regexp = RegExpression.OPERATION_NAME, message = "Invalid name format")
    String name,
    @NotNull(message = "Invalid type work id: id is NULL")
    @Min(value = 0, message = "The type work id cannot be less than 0")
    @Max(value = Integer.MAX_VALUE, message =
        "The type work id cannot be more than " + Integer.MAX_VALUE)
    long typeWorkId,
    @NotNull(message = "Invalid priority: priority is NULL")
    @Min(value = 1, message = "The priority cannot be less than 1")
    @Max(value = 989, message = "The priority cannot be more than 989")
    int priority
) {
}
