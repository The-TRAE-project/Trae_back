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

import static ru.trae.backend.util.Role.ROLE_ADMINISTRATOR;
import static ru.trae.backend.util.Role.ROLE_EMPLOYEE;
import static ru.trae.backend.util.Role.ROLE_MANAGER;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import ru.trae.backend.exceptionhandler.RestAccessDeniedHandler;
import ru.trae.backend.exceptionhandler.RestAuthenticationEntryPoint;
import ru.trae.backend.util.jwt.JwtFilter;

/**
 * Security configuration to provide authentication and authorization.
 *
 * @author Vladimir Olennikov
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

  private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
  private final RestAccessDeniedHandler restAccessDeniedHandler;
  private final JwtFilter jwtFilter;
  private static final String[] AUTH_WHITELIST = {
      // -- Swagger UI v2
      "/v2/api-docs",
      "/swagger-resources",
      "/swagger-resources/**",
      "/configuration/ui",
      "/configuration/security",
      "/swagger-ui.html",
      // -- Swagger UI v3 (OpenAPI)
      "/v3/api-docs/**",
      "/swagger-ui/**"
  };

  /**
   * This method is used to create a bean for SecurityFilterChain.
   *
   * @param httpSecurity HttpSecurity object
   * @return SecurityFilterChain object
   * @throws Exception exception
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .csrf().disable()
        .cors().configurationSource(request -> {
          CorsConfiguration configuration = new CorsConfiguration();
          configuration.setAllowedOrigins(List.of("*"));
          configuration.setAllowedMethods(List.of("*"));
          configuration.setAllowedHeaders(List.of("*"));
          return configuration;
        })
        .and()
        .exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint)
        .and()
        .exceptionHandling().accessDeniedHandler(restAccessDeniedHandler)
        .and()
        .authorizeRequests()
        .antMatchers("/api/auth/login", "/api/auth/token").permitAll()
        .antMatchers("/webjars/**").permitAll()
        //.antMatchers(AUTH_WHITELIST).permitAll() - чтобы включить доступ к Swagger,
        // надо снять комментарий

        //auth
        .antMatchers(
            "/api/auth/logout",
            "/api/auth/refresh",
            "/api/manager/account-info",
            "/api/manager/update-data",
            "/api/manager/role").authenticated()

        //managers
        .antMatchers(
            "/api/manager/*")
        .hasAuthority(ROLE_ADMINISTRATOR.name())

        //employees
        .antMatchers(
            "/api/employee/checkin/*",
            "/api/employee/checkout/*",
            "/api/employee/login/*")
        .hasAuthority(ROLE_EMPLOYEE.name())
        .antMatchers(
            "/api/employee/employees",
            "/api/employee/register",
            "/api/employee/change-data",
            "/api/employee/employees/list")
        .hasAuthority(ROLE_ADMINISTRATOR.name())

        //types-work
        .antMatchers(
            "/api/type-work/types",
            "/api/type-work/new",
            "/api/type-work/change-name-active")
        .hasAuthority(ROLE_ADMINISTRATOR.name())
        .antMatchers(
            "/api/type-work/active/list-without-shipment",
            "/api/type-work/active/list")
        .hasAnyAuthority(ROLE_ADMINISTRATOR.name(), ROLE_MANAGER.name())

        //projects
        .antMatchers(
            "/api/project/new")
        .hasAnyAuthority(ROLE_MANAGER.name(), ROLE_ADMINISTRATOR.name())
        .antMatchers(
            "/api/project/*",
            "/api/project/projects/list",
            "/api/project/delete-project/*")
        .hasAuthority(ROLE_ADMINISTRATOR.name())
        .antMatchers(
            "/api/project/employee/available-projects/*")
        .hasAuthority(ROLE_EMPLOYEE.name())

        //operations
        .antMatchers(
            "/api/operation/*",
            "/api/operation/operations/list",
            "/api/operation/delete-operation/*")
        .hasAuthority(ROLE_ADMINISTRATOR.name())
        .antMatchers(
            "/api/operation/employee/project-operations/*",
            "/api/operation/employee/operations-in-work/*",
            "/api/operation/employee/receive-operation",
            "/api/operation/employee/finish-operation")
        .hasAuthority(ROLE_EMPLOYEE.name())

        //working shifts
        .antMatchers(
            "/api/working-shift/active")
        .hasAuthority(ROLE_ADMINISTRATOR.name())
        .antMatchers(
            "/api/working-shift/on-shift/*")
        .hasAnyAuthority(ROLE_ADMINISTRATOR.name(), ROLE_EMPLOYEE.name())

        //reports
        .antMatchers("/api/report/*")
        .hasAuthority(ROLE_ADMINISTRATOR.name())

        .anyRequest().authenticated()
        .and()
        .httpBasic().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return httpSecurity.build();
  }
}