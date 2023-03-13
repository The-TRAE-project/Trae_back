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

public class RegExpression {
  public static final String USERNAME = "^(?=.{3,15}$)(?![_.-])(?!.*[_.-]{2})[a-z0-9._-]+([^._-])$";
  public static final String FIRST_MIDDLE_LAST_NAME = "^[А-Я]-?[а-я]{1,15}$";
  public static final String PHONE_NUMBER =
          "^(\\+\\d{1,3}\\s?)?1?\\-?\\.?\\s?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$";
}
