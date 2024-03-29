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
 * This class represents the account information for a manager.
 *
 * @author Vladimir Olennikov
 */
public record AccountInfo(
    long managerId,
    String firstName,
    String middleName,
    String lastName,
    String phone
) {
}
