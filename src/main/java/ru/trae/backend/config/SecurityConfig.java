package ru.trae.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.trae.backend.exceptionhandler.RestAccessDeniedHandler;
import ru.trae.backend.exceptionhandler.RestAuthenticationEntryPoint;
import ru.trae.backend.util.jwt.JWTFilter;

import static ru.trae.backend.util.Role.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

	private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

	private final RestAccessDeniedHandler restAccessDeniedHandler;

	private final JWTFilter jwtFilter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf()
			.disable()
			.exceptionHandling()
			.authenticationEntryPoint(restAuthenticationEntryPoint)
			.and()
			.exceptionHandling()
			.accessDeniedHandler(restAccessDeniedHandler)
			.and()
			.authorizeRequests()
			// .antMatchers("/api/auth/login",
			// "/api/auth/token","/api/manager/register").permitAll()
			// .antMatchers("/api/employee/**").hasAuthority(ROLE_MANAGER.name())
			// .antMatchers("/api/operation/**").hasAuthority(ROLE_EMPLOYEE.name())
			// .antMatchers("/api/manager/**").hasAuthority(ROLE_ADMINISTRATOR.name())
			// .anyRequest().authenticated()
			.anyRequest()
			.permitAll()
			.and()
			.httpBasic()
			.disable()
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return httpSecurity.build();
	}

}