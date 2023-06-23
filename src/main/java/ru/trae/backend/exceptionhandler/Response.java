/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.exceptionhandler;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Class representing a response.
 *
 * @author Vladimir Olennikov
 */
@Builder
@Getter
public class Response {
  String timestamp;
  HttpStatus status;
  String error;
}