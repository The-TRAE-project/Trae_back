/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Principal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.trae.backend.dto.Credentials;
import ru.trae.backend.dto.jwt.JwtResponse;
import ru.trae.backend.entity.user.Manager;
import ru.trae.backend.exceptionhandler.exception.LoginCredentialException;
import ru.trae.backend.util.jwt.JwtUtil;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
  @Mock
  private ManagerService managerService;
  @Mock
  private JwtUtil jwtUtil;
  @Mock
  private BCryptPasswordEncoder encoder;
  @InjectMocks
  private AuthService authService;
  String username = "test_username";
  String password = "test_password";
  String encodedPassword = "encoded_password";
  String accessToken = "access_token";
  String refreshToken = "refresh_token";
  String newRefreshToken = "test_new_refresh_token";
  Manager m = new Manager();
  
  @BeforeEach
  public void init() {
    m.setAccountNonLocked(true);
    m.setUsername(username);
    m.setPassword(encodedPassword);
  }
  
  @Test
  void login_WithValidCredentials_ShouldReturnJwtResponse() {
    //given
    Credentials credentials = new Credentials(username, password);
    
    //when
    when(managerService.getManagerByUsername(username)).thenReturn(m);
    when(encoder.matches(password, encodedPassword)).thenReturn(true);
    when(jwtUtil.generateAccessToken(username)).thenReturn(accessToken);
    when(jwtUtil.generateRefreshToken(username)).thenReturn(refreshToken);
    
    JwtResponse response = authService.login(credentials);
    
    //then
    assertNotNull(response);
    assertEquals(accessToken, response.accessToken());
    assertEquals(refreshToken, response.refreshToken());
    
    verify(managerService, times(1)).getManagerByUsername(username);
    verify(encoder, times(1)).matches(password, encodedPassword);
    verify(jwtUtil, times(1)).generateAccessToken(username);
    verify(jwtUtil, times(1)).generateRefreshToken(username);
  }
  
  @Test
  void login_WithInvalidCredentials_ShouldThrowLoginCredentialException() {
    //given
    Credentials credentials = new Credentials(username, password);
    
    //when
    when(managerService.getManagerByUsername(username)).thenReturn(m);
    when(encoder.matches(password, encodedPassword)).thenReturn(false);
    
    //then
    assertThrows(LoginCredentialException.class,
        () -> authService.login(credentials),
        "Invalid login credentials");
    
    verify(managerService, times(1)).getManagerByUsername(username);
    verify(encoder, times(1)).matches(password, encodedPassword);
    verify(jwtUtil, never()).generateAccessToken(username);
    verify(jwtUtil, never()).generateRefreshToken(username);
  }
  
  @Test
  void login_WithInvalidCredentials_ShouldThrowLoginCredentialException_AccountIsLocked() {
    //given
    Credentials credentials = new Credentials(username, password);
    m.setAccountNonLocked(false);
    
    //when
    when(managerService.getManagerByUsername(username)).thenReturn(m);
    
    //then
    assertThrows(LoginCredentialException.class,
        () -> authService.login(credentials),
        "This account is locked");
    
    verify(managerService, times(1)).getManagerByUsername(username);
    verify(encoder, never()).matches(password, encodedPassword);
    verify(jwtUtil, never()).generateAccessToken(username);
    verify(jwtUtil, never()).generateRefreshToken(username);
  }
  
  @Test
  void login_WithNonExistentManager_ShouldThrowLoginCredentialException() {
    //given
    Credentials credentials = new Credentials(username, password);
    
    //when
    when(managerService.getManagerByUsername(username)).thenReturn(m);
    when(encoder.matches(password, encodedPassword)).thenReturn(false);
    
    //then
    assertThrows(LoginCredentialException.class,
        () -> authService.login(credentials),
        "Invalid login credentials");
    
    verify(managerService, times(1)).getManagerByUsername(username);
    verify(encoder, times(1)).matches(anyString(), anyString());
    verify(jwtUtil, never()).generateAccessToken(anyString());
    verify(jwtUtil, never()).generateRefreshToken(anyString());
  }
  
  @Test
  void logout_ShouldDeletePayloadRandomPieces() {
    //given
    Principal principal = mock(Principal.class);
    
    //when
    when(principal.getName()).thenReturn(username);
    
    authService.logout(principal);
    
    //then
    verify(jwtUtil, times(1)).deletePayloadRandomPieces(username);
  }
  
  @Test
  void getAccessToken_WithValidRefreshToken_ShouldReturnJwtResponse() {
    //when
    when(jwtUtil.validateRefreshTokenAndRetrieveSubject(refreshToken)).thenReturn(username);
    when(managerService.getManagerByUsername(username)).thenReturn(m);
    when(jwtUtil.generateAccessToken(username)).thenReturn(accessToken);
    
    JwtResponse response = authService.getAccessToken(refreshToken);
    
    //then
    assertNotNull(response);
    assertEquals(accessToken, response.accessToken());
    assertNull(response.refreshToken());
    
    verify(jwtUtil, times(1)).validateRefreshTokenAndRetrieveSubject(refreshToken);
    verify(managerService, times(1)).getManagerByUsername(username);
    verify(jwtUtil, times(1)).generateAccessToken(username);
  }
  
  @Test
  void getAccessToken_WithInvalidRefreshToken_ShouldThrowException() {
    //when
    when(jwtUtil.validateRefreshTokenAndRetrieveSubject(refreshToken)).thenReturn(null);
    
    //then
    assertThrows(Exception.class, () -> authService.getAccessToken(refreshToken));
    
    verify(jwtUtil, times(1)).validateRefreshTokenAndRetrieveSubject(refreshToken);
    verify(managerService, never()).getManagerByUsername(anyString());
    verify(jwtUtil, never()).generateAccessToken(anyString());
  }
  
  @Test
  void getAccessToken_WithNonExistentManager_ShouldThrowException() {
    //when
    when(jwtUtil.validateRefreshTokenAndRetrieveSubject(refreshToken)).thenReturn(username);
    when(managerService.getManagerByUsername(username)).thenReturn(null);
    
    //then
    assertThrows(Exception.class, () -> authService.getAccessToken(refreshToken));
    
    verify(jwtUtil, times(1)).validateRefreshTokenAndRetrieveSubject(refreshToken);
    verify(managerService, times(1)).getManagerByUsername(username);
    verify(jwtUtil, never()).generateAccessToken(anyString());
  }
  
  @Test
  void getRefreshToken_WithValidRefreshToken_ShouldReturnJwtResponse() {
    //when
    when(jwtUtil.validateRefreshTokenAndRetrieveSubject(refreshToken)).thenReturn(username);
    when(managerService.getManagerByUsername(username)).thenReturn(m);
    when(jwtUtil.generateAccessToken(username)).thenReturn(accessToken);
    when(jwtUtil.generateRefreshToken(username)).thenReturn(newRefreshToken);
    
    JwtResponse response = authService.getRefreshToken(refreshToken);
    
    //then
    assertNotNull(response);
    assertEquals(accessToken, response.accessToken());
    assertEquals(newRefreshToken, response.refreshToken());
    
    verify(jwtUtil, times(1)).validateRefreshTokenAndRetrieveSubject(refreshToken);
    verify(managerService, times(1)).getManagerByUsername(username);
    verify(jwtUtil, times(1)).generateAccessToken(username);
    verify(jwtUtil, times(1)).generateRefreshToken(username);
  }
  
  @Test
  void getRefreshToken_WithInvalidRefreshToken_ShouldThrowException() {
    //when
    when(jwtUtil.validateRefreshTokenAndRetrieveSubject(refreshToken)).thenReturn(null);
    
    //then
    assertThrows(Exception.class, () -> authService.getRefreshToken(refreshToken));
    
    verify(jwtUtil, times(1)).validateRefreshTokenAndRetrieveSubject(refreshToken);
    verify(managerService, never()).getManagerByUsername(anyString());
    verify(jwtUtil, never()).generateAccessToken(anyString());
    verify(jwtUtil, never()).generateRefreshToken(anyString());
  }
  
  @Test
  void getRefreshToken_WithNonExistentManager_ShouldThrowException() {
    //when
    when(jwtUtil.validateRefreshTokenAndRetrieveSubject(refreshToken)).thenReturn(username);
    when(managerService.getManagerByUsername(username)).thenReturn(null);
    
    //then
    assertThrows(Exception.class, () -> authService.getRefreshToken(refreshToken));
    
    verify(jwtUtil, times(1)).validateRefreshTokenAndRetrieveSubject(refreshToken);
    verify(managerService, times(1)).getManagerByUsername(username);
    verify(jwtUtil, never()).generateAccessToken(anyString());
    verify(jwtUtil, never()).generateRefreshToken(anyString());
  }
}
