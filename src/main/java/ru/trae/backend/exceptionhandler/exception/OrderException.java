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
 * OrderException is a custom exception class used to handle order related exceptions.
 *
 * @author Vladimir Olennikov
 */
@Getter
public class OrderException extends AbstractException {
  public OrderException(HttpStatus status, String errorMessage) {
    super(status, errorMessage);
  }
}
