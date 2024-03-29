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
 * Enum class Constant provides constants.
 */
public enum Constant {
  PROJECT_WITH_ID("Project with id: "),
  OPERATION_WITH_ID("Operation with id: "),
  NOT_FOUND_CONST(" not found"),
  WRONG_PARAMETER("Wrong second or third value in parameters");

  public final String value;

  Constant(String value) {
    this.value = value;
  }
}
