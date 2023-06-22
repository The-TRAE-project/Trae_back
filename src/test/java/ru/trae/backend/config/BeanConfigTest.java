/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import springfox.documentation.spring.web.plugins.Docket;

class BeanConfigTest {
  @Mock
  private AuthenticationConfiguration authenticationConfiguration;
  
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }
  
  @Test
  void api_ReturnsNonNullDocketBean() {
    //given
    BeanConfig beanConfig = new BeanConfig();
    
    //when
    Docket docket = beanConfig.api();
    
    //then
    assertNotNull(docket);
  }
  
  @Test
  void defaultViewResolver_ReturnsNonNullInternalResourceViewResolverBean() {
    //given
    BeanConfig beanConfig = new BeanConfig();
    
    //when
    InternalResourceViewResolver resolver = beanConfig.defaultViewResolver();
    
    //then
    assertNotNull(resolver);
  }
  
  @Test
  void encoder_ReturnsNonNullBCryptPasswordEncoderBean() {
    //given
    BeanConfig beanConfig = new BeanConfig();
    
    //when
    BCryptPasswordEncoder encoder = beanConfig.encoder();
    
    //then
    assertNotNull(encoder);
  }
  @Test
  void authenticationManager_ReturnsNonNullAuthenticationManagerBean() throws Exception {
    //given
    BeanConfig beanConfig = new BeanConfig();
    
    //when
    AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);
    
    AuthenticationManager result = beanConfig.authenticationManager(authenticationConfiguration);
    
    //then
    assertNotNull(result);
  }
  
}
