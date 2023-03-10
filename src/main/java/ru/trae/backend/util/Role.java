/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.util;

/**
 * Enum representing the different roles a user can have.
 *
 * @author Vladimir Olennikov
 */
public enum Role {
  ROLE_ADMINISTRATOR("Administrator"),
  ROLE_EMPLOYEE("Employee"),
  ROLE_DEVELOPER("Developer"),
  ROLE_MANAGER("Manager"),
  ROLE_USER("User");

  public final String value;

  Role(String value) {
    this.value = value;
  }
}
