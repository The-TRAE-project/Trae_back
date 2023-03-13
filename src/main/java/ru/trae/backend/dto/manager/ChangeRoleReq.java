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

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import ru.trae.backend.util.RegExpression;

/**
 * The {@code ChangeRoleReq} class is used to represent a request to change a user's role.
 *
 * @author Vladimir Olennikov
 */
public record ChangeRoleReq(
        @NotNull(message = "Invalid manager id: id is NULL")
        @Min(value = 0, message = "The manager id cannot be less than 0")
        @Max(value = Long.MAX_VALUE, message =
                "The manager id cannot be more than " + Long.MAX_VALUE)
        long managerId,
        @NotNull(message = "Invalid new role: new role is NULL")
        @Pattern(regexp = RegExpression.ROLE, message = "Invalid role format")
        String newRole
) {
}
