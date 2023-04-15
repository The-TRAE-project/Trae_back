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
  NOT_FOUND_CONST(" not found");

  public final String value;

  Constant(String value) {
    this.value = value;
  }
}
