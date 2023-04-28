/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.dto.project;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import ru.trae.backend.util.RegExpression;

/**
 * Request class for changing common data about project.
 *
 * @param projectNumber The number of the project.
 * @param projectName   The name of the project.
 * @param customer      The customer of the project.
 * @param commentary    The commentary.
 */
public record ChangingCommonDataReq(
    @Schema(description = "Идентификатор проекта")
    @NotNull(message = "Invalid project id: id is NULL")
    @Min(value = 0, message = "The project id cannot be less than 0")
    @Max(value = Integer.MAX_VALUE, message =
        "The project id cannot be more than " + Integer.MAX_VALUE)
    long projectId,
    @Schema(description = "Номер проекта")
    @Min(value = 1, message = "The number cannot be less than 1")
    @Max(value = 999, message = "The number cannot be more than 999")
    Integer projectNumber,
    @Schema(description = "Название проекта")
    @Pattern(regexp = RegExpression.PROJECT_NAME, message = "Invalid name format")
    String projectName,
    @Schema(description = "Заказчик проекта")
    @Pattern(regexp = RegExpression.CUSTOMER, message = "Invalid customer format")
    String customer,
    @Schema(description = "Комментарий")
    @Size(max = 1000, message = "The comment cannot be more than 1000 symbols")
    String commentary
) {
}
