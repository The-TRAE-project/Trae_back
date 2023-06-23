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

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.servlet.HandlerExceptionResolver;

class RestAuthenticationEntryPointTest {
  
  @Test
  void commence_ShouldResolveExceptionAndSetResponseValues() {
    //given
    HandlerExceptionResolver exceptionResolver = mock(HandlerExceptionResolver.class);
    RestAuthenticationEntryPoint authenticationEntryPoint = new RestAuthenticationEntryPoint(exceptionResolver);
    HttpServletRequest request = new MockHttpServletRequest();
    HttpServletResponse response = new MockHttpServletResponse();
    AuthenticationException authenticationException = mock(AuthenticationException.class);
    
    //when
    authenticationEntryPoint.commence(request, response, authenticationException);
    
    //then
    verify(exceptionResolver).resolveException(eq(request), eq(response), isNull(), eq(authenticationException));
  }
}
