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

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import ru.trae.backend.util.RegExpression;

/**
 * The {@code ChangePassReq} class is used to represent a request to change a user's password.
 *
 * @author Vladimir Olennikov
 */
public record ChangePassReq(
    @NotNull(message = "Invalid old password: old password is NULL")
    @Pattern(regexp = RegExpression.PASSWORD, message = "Invalid old password format")
    String oldPassword,
    @NotNull(message = "Invalid new password: new password is NULL")
    @Pattern(regexp = RegExpression.PASSWORD, message = "Invalid new password format")
    String newPassword
) {
}
