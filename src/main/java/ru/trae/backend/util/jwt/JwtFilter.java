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

import com.auth0.jwt.exceptions.JWTVerificationException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.trae.backend.service.CustomUserDetailsService;

/**
 * The JwtFilter is a security filter that implements the Spring Security filter chain.
 * It is used to authenticate requests using a bearer token in the Authorization header.
 * The filter is used to validate the access token in the header and retrieve the subject.
 * Once the subject is retrieved, a UsernamePasswordAuthenticationToken is created and set
 * in the SecurityContext.
 *
 * @author Vladimir Olennikov
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private final CustomUserDetailsService customUserDetailsService;
  private final JwtUtil jwtUtil;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")) {
      String jwt = authHeader.substring(7);
      if (jwt == null || jwt.isBlank()) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                "Invalid JWT Token in Bearer Header");
      } else {
        try {
          String username = jwtUtil.validateAccessTokenAndRetrieveSubject(jwt);
          UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
          UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                  username,
                  userDetails.getPassword(),
                  userDetails.getAuthorities());

          if (SecurityContextHolder.getContext().getAuthentication() == null) {
            SecurityContextHolder.getContext().setAuthentication(authToken);
          }

        } catch (JWTVerificationException exc) {
          throw new JWTVerificationException("Invalid JWT Token");
        }
      }
    }

    filterChain.doFilter(request, response);
  }

}