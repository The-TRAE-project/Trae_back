/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.exceptionhandler.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Abstract exception class that is used as the parent class for all custom exceptions.
 *
 * @author Vladimir Olennikov
 */
@Getter
public abstract class AbstractException extends RuntimeException {
  private final HttpStatus status;

  protected AbstractException(HttpStatus status, String errorMessage) {
    super(errorMessage);
    this.status = status;
  }
}
