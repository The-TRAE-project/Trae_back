package ru.trae.backend.controller;/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.security.Principal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.trae.backend.dto.Credentials;
import ru.trae.backend.dto.jwt.JwtResponse;
import ru.trae.backend.dto.jwt.RefreshJwtRequest;
import ru.trae.backend.service.AuthService;


@ExtendWith(MockitoExtension.class)
class AuthControllerTests {
  private static final String USERNAME = "user";
  private static final String PASSWORD = "pass";
  private static final String ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
  private static final String REFRESH_TOKEN = "dXNlcjpwYXNz";

  @Mock
  private AuthService authService;

  @InjectMocks
  private AuthController controller;

  @Test
  void loginTest() {
    Credentials credentials = new Credentials(USERNAME, PASSWORD);
    JwtResponse jwtResponse = new JwtResponse(ACCESS_TOKEN, REFRESH_TOKEN);

    when(authService.login(credentials)).thenReturn(jwtResponse);
    ResponseEntity<JwtResponse> response = controller.login(credentials);
    assertEquals("result: ", response.getStatusCode(), HttpStatus.OK);
    assertEquals("result: ", response.getBody(), jwtResponse);
  }

  @Test
  void logoutTest() {
    Principal principal = () -> USERNAME;
    doNothing().when(authService).logout(principal);
    ResponseEntity<HttpStatus> response = controller.logout(principal);
    assertEquals("result: ", response.getStatusCode(), HttpStatus.OK);
  }

  @Test
  void newAccessTokenTest() {
    RefreshJwtRequest request = new RefreshJwtRequest(REFRESH_TOKEN);
    JwtResponse jwtResponse = new JwtResponse(ACCESS_TOKEN, REFRESH_TOKEN);

    when(authService.getAccessToken(REFRESH_TOKEN)).thenReturn(jwtResponse);
    ResponseEntity<JwtResponse> response = controller.newAccessToken(request);
    assertEquals("result: ", response.getStatusCode(), HttpStatus.OK);
    assertEquals("result: ", response.getBody(), jwtResponse);
  }

  @Test
  void newRefreshTokenTest() {
    RefreshJwtRequest request = new RefreshJwtRequest(REFRESH_TOKEN);
    JwtResponse jwtResponse = new JwtResponse(ACCESS_TOKEN, REFRESH_TOKEN);

    when(authService.getRefreshToken(REFRESH_TOKEN)).thenReturn(jwtResponse);
    ResponseEntity<JwtResponse> response = controller.newRefreshToken(request);
    assertEquals("result: ", response.getStatusCode(), HttpStatus.OK);
    assertEquals("result: ", response.getBody(), jwtResponse);
  }
}