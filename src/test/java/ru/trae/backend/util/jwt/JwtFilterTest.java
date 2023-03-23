/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.util.jwt;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth0.jwt.exceptions.JWTVerificationException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.trae.backend.service.CustomUserDetailsService;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {
  @Mock
  private CustomUserDetailsService customUserDetailsService;
  @Mock
  private JwtUtil jwtUtil;
  @Mock
  private HandlerExceptionResolver resolver;
  @Mock
  private HttpServletRequest request;
  @Mock
  private HttpServletResponse response;
  @Mock
  private FilterChain filterChain;
  @Mock
  private UserDetails userDetails;
  @InjectMocks
  private JwtFilter jwtFilter;

  @Test
  void shouldDoFilterInternal() throws ServletException, IOException {
    //given
    String authHeader = "Bearer TestToken";
    String username = "username";
    String jwt = "TestToken";

    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
        username,
        userDetails.getPassword(),
        userDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authToken);

    //when
    when(request.getHeader("Authorization")).thenReturn(authHeader);
    when(jwtUtil.validateAccessTokenAndRetrieveSubject(jwt)).thenReturn(username);
    when(customUserDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

    jwtFilter.doFilterInternal(request, response, filterChain);

    //then
    verify(jwtUtil, times(1)).validateAccessTokenAndRetrieveSubject(jwt);
    verify(customUserDetailsService, times(1)).loadUserByUsername(username);
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  void shouldDoFilterInternalWithEmptySecurityContextHolder() throws ServletException, IOException {
    //given
    String authHeader = "Bearer TestToken";
    String username = "username";
    String jwt = "TestToken";

    //when
    when(request.getHeader("Authorization")).thenReturn(authHeader);
    when(jwtUtil.validateAccessTokenAndRetrieveSubject(jwt)).thenReturn(username);
    when(customUserDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

    jwtFilter.doFilterInternal(request, response, filterChain);

    //then
    verify(jwtUtil, times(1)).validateAccessTokenAndRetrieveSubject(jwt);
    verify(customUserDetailsService, times(1)).loadUserByUsername(username);
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  void shouldThrowErrorWhenTokenIsInvalid() throws ServletException, IOException {
    // given
    JWTVerificationException invalidToken = new JWTVerificationException("Invalid token");

    // when
    when(request.getHeader(anyString())).thenReturn("Bearer invalidToken");
    when(jwtUtil.validateAccessTokenAndRetrieveSubject(anyString())).thenThrow(invalidToken);

    jwtFilter.doFilterInternal(request, response, filterChain);

    // then
    verify(resolver, times(1))
        .resolveException(request, response, null, invalidToken);
  }

  @Test
  void shouldThrowErrorWhenTokenIsNull() throws ServletException, IOException {
    // given
    String authHeader = "Bearer ";

    // when
    when(request.getHeader("Authorization")).thenReturn(authHeader);
    jwtFilter.doFilterInternal(request, response, filterChain);

    // then
    verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST,
        "Invalid JWT Token in Bearer Header");
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "Not bearer token"})
  void shouldThrowErrorWhenAuthHeaderIsBlank(String header) throws ServletException, IOException {
    // when
    when(request.getHeader("Authorization")).thenReturn(header);
    jwtFilter.doFilterInternal(request, response, filterChain);

    // then
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  void shouldThrowErrorWhenAuthHeaderIsNull() throws ServletException, IOException {
    // when
    when(request.getHeader("Authorization")).thenReturn(null);
    jwtFilter.doFilterInternal(request, response, filterChain);

    // then
    verify(filterChain, times(1)).doFilter(request, response);
  }
}