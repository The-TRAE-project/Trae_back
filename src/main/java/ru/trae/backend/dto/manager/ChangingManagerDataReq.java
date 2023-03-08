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
 * A request to change the manager's data.
 *
 * @param firstName  The manager's first name.
 * @param middleName The manager's middle name.
 * @param lastName   The manager's last name.
 * @param phone      The manager's phone number.
 */
public record ChangingManagerDataReq(
        String firstName,
        String middleName,
        String lastName,
        String phone
) {
}
