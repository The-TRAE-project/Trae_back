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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ResponseTest {
  
  @Test
  void testResponseBuilder_WithDefaultValues() {
    //given
    Response response = Response.builder().build();
    
    //then
    assertNull(response.getTimestamp());
    assertNull(response.getStatus());
    assertNull(response.getError());
  }
  
  @Test
  void testResponseBuilder_WithCustomValues() {
    //given
    String timestamp = "2023-06-19T10:15:30";
    HttpStatus status = HttpStatus.BAD_REQUEST;
    String error = "Some error message";
    
    //when
    Response response = Response.builder()
        .timestamp(timestamp)
        .status(status)
        .error(error)
        .build();
    
    //then
    assertEquals(timestamp, response.getTimestamp());
    assertEquals(status, response.getStatus());
    assertEquals(error, response.getError());
  }
}

