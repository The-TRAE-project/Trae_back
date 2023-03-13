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
 * A request to change the manager's data.
 *
 * @param firstName  The manager's first name.
 * @param middleName The manager's middle name.
 * @param lastName   The manager's last name.
 * @param phone      The manager's phone number.
 */
public record ChangingManagerDataReq(
        @NotNull(message = "Invalid first name: first name is NULL")
        @Pattern(regexp = RegExpression.FIRST_MIDDLE_LAST_NAME, message = "Invalid first name")
        String firstName,
        @NotNull(message = "Invalid middle name: middle name is NULL")
        @Pattern(regexp = RegExpression.FIRST_MIDDLE_LAST_NAME, message = "Invalid middle name")
        String middleName,
        @NotNull(message = "Invalid last name: last name is NULL")
        @Pattern(regexp = RegExpression.FIRST_MIDDLE_LAST_NAME, message = "Invalid last name")
        String lastName,
        @NotNull(message = "Invalid phone number: phone number is NULL")
        @Pattern(regexp = RegExpression.PHONE_NUMBER, message = "Invalid phone number format")
        String phone
) {
}
