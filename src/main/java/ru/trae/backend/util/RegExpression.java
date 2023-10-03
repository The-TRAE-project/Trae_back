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

import lombok.experimental.UtilityClass;

/**
 * This is a class that contains different regular expressions.
 * for username, password, name, phone number, token, etc.
 */
@UtilityClass
public class RegExpression {
  public static final String USERNAME =
      "^(?=.{3,15}$)(?![_.-])(?!.*[_.-]{2})[a-zA-Z0-9._-]+(?<![_.-])$";
  //no _,- or . at the beginning
  //no __ or . or . or .. or .- or _- inside
  //no _,- or . at the end
  //password is 3-15 characters with no space
  public static final String PASSWORD =
      "^((?=\\S*?[A-Z])(?=\\S*?[a-z])(?=\\S*?[0-9]?).{4,14})\\S$";
  public static final String FIRST_MIDDLE_LAST_NAME = "^[А-ЯЁ][а-яё]{1,14}$";
  public static final String PHONE_NUMBER = "^(\\+\\d{1,3} )\\(?\\d{2,4}\\)[ ]\\d{3}[ ]\\d{4}$";
  public static final String TOKEN =
      "^([a-zA-Z0-9_=]+)\\.([a-zA-Z0-9_=]+)\\.([a-zA-Z0-9_\\-\\+\\/=]*)";
  public static final String ROLE = "^[А-ЯA-Z][а-яa-z ]{3,49}$";
  public static final String CUSTOMER =
      "^(?!.*\\s\\s)[а-яА-Яa-zA-Z0-9\\s!\"#$%&'()*+,-.\\/\\\\:;<=>?@\\[\\]^_`{|}~]{3,30}(?<!\\s)$";
  public static final String PROJECT_NAME =
      "^(?!.*\\s\\s)[а-яА-Яa-zA-Z0-9\\s!\"#$%&'()*+,-.\\/\\\\:;<=>?@\\[\\]^_`{|}~]{3,30}(?<!\\s)$";
  public static final String OPERATION_NAME =
      "^(?!.*\\s\\s)[а-яА-Яa-zA-Z0-9\\s!\"#$%&'()*+,-.\\/\\\\:;<=>?@\\[\\]^_`{|}~]{3,30}(?<!\\s)$";
  public static final String TYPE_WORK_NAME =
      "^(?!.*\\s\\s)[а-яА-Яa-zA-Z0-9\\s!\"#$%&'()*+,-.\\/\\\\:;<=>?@\\[\\]^_`{|}~]{3,30}(?<!\\s)$";
}
