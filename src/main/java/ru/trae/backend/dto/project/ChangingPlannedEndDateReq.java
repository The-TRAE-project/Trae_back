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
import java.time.LocalDateTime;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

public record ChangingPlannedEndDateReq(
    @Schema(description = "Идентификатор проекта")
    @NotNull(message = "Invalid project id: id is NULL")
    @Min(value = 0, message = "The project id cannot be less than 0")
    @Max(value = Integer.MAX_VALUE, message =
        "The project id cannot be more than " + Integer.MAX_VALUE)
    long projectId,
    @Schema(description = "Новая планируемая дата окончания проекта")
    @NotNull(message = "Invalid new planned end date: date is NULL")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime newPlannedEndDate
) {
}
