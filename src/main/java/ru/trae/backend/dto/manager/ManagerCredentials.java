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
 * The ManagerCredentials class is used for containing data related to the credentials
 * of a manager.
 * It consists of two fields, username and temporaryRandomPassword.
 *
 * @author Vladimir Olennikov
 */
public record ManagerCredentials(
        String username,
        String temporaryRandomPassword
) {
}
