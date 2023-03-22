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


/**
 * This class represents a response to a reset password request. It contains the last name,
 * first name, and new password of the user.
 *
 * @author Vladimir Olennikov
 */
public record ResetPassResp(
    String lastName,
    String firstName,
    String newPassword
) {
}
