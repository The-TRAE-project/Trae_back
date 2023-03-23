/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.dto.jwt;

import static org.springframework.test.util.AssertionErrors.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class JwtResponseTest {
  String accessToken = "12345";
  String refreshToken = "abcde";

  @Test
  void testJwtResponse() {
    //given
    JwtResponse jwtResponse = Mockito.mock(JwtResponse.class);

    //when
    Mockito.when(jwtResponse.accessToken()).thenReturn(accessToken);
    Mockito.when(jwtResponse.refreshToken()).thenReturn(refreshToken);

    //then
    assertEquals("access token: ", accessToken, jwtResponse.accessToken());
    assertEquals("refresh token: ", refreshToken, jwtResponse.refreshToken());
  }

  @Test
  void testJwtResponseFullConstructor() {
    //given
    JwtResponse jwtResponse = new JwtResponse(accessToken, refreshToken);

    //then
    assertEquals("access token: ", accessToken, jwtResponse.accessToken());
    assertEquals("refresh token: ", refreshToken, jwtResponse.refreshToken());
  }
}