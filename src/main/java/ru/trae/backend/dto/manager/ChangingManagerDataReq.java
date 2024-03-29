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

import javax.validation.constraints.Pattern;
import ru.trae.backend.util.RegExpression;

/**
 * A request to change the manager's data.
 *
 * @param firstName  The manager's first name.
 * @param middleName The manager's middle name.
 * @param lastName   The manager's last name.
 * @param phone      The manager's phone number.
 */
public record ChangingManagerDataReq(
    @Pattern(regexp = RegExpression.FIRST_MIDDLE_LAST_NAME, message = "Invalid first name")
    String firstName,
    @Pattern(regexp = RegExpression.FIRST_MIDDLE_LAST_NAME, message = "Invalid middle name")
    String middleName,
    @Pattern(regexp = RegExpression.FIRST_MIDDLE_LAST_NAME, message = "Invalid last name")
    String lastName,
    @Pattern(regexp = RegExpression.PHONE_NUMBER, message = "Invalid phone number format")
    String phone,
    @Pattern(regexp = RegExpression.PASSWORD, message = "Invalid old password format")
    String oldPassword,
    @Pattern(regexp = RegExpression.PASSWORD, message = "Invalid new password format")
    String newPassword
) {
}
