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

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

class RestAccessDeniedHandlerTest {
  
  @Test
  void handle_ShouldSetCorrectResponseValues() throws IOException {
    //given
    RestAccessDeniedHandler accessDeniedHandler = new RestAccessDeniedHandler();
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    AccessDeniedException accessDeniedException = new AccessDeniedException("Access denied");
    
    //when
    accessDeniedHandler.handle(request, response, accessDeniedException);
    
    //then
    assertEquals("application/json", response.getContentType());
    assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
  }
}
