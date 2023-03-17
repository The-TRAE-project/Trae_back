/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.dto.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import ru.trae.backend.util.RegExpression;

/**
 * This class defines the request parameters for changing the role and status of a user.
 * It contains the user ID, the new user role, the status of the user's account,
 * and the date of dismissal.
 *
 * @author Vladimir Olennikov
 */
public record ChangeRoleAndStatusReq(
    @Schema(description = "Идентификатор пользователя")
    @NotNull(message = "Invalid manager id: id is NULL")
    @Min(value = 0, message = "The manager id cannot be less than 0")
    @Max(value = Integer.MAX_VALUE, message =
        "The manager id cannot be more than " + Integer.MAX_VALUE)
    long managerId,
    @Schema(description = "Новая роль пользователя")
    @Pattern(regexp = RegExpression.ROLE, message = "Invalid role format")
    String newRole,
    @Schema(description = "Состояние учетной записи пользователя")
    Boolean accountStatus,
    @Schema(description = "Состояние учетной записи пользователя")
    LocalDate dateOfDismissal
) {
}
