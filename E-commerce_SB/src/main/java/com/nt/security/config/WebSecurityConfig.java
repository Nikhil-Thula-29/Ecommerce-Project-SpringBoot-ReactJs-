package com.nt.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.nt.security.jwt.AuthEntryPointJWT;
import com.nt.security.jwt.AuthTokenFilter;
import com.nt.security.services.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Autowired
	UserDetailsServiceImpl userDetailsService;
	
	@Autowired
	private AuthEntryPointJWT unauthorizedHandler;
	
	@Autowired
	@Lazy
	private AuthTokenFilter authTokenFilter;
	
	@Autowired
	private DaoAuthenticationProvider authenticationProvider;
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider(UserDetailsServiceImpl userDetailsService,PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder);
		return authenticationProvider;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();	
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http)throws Exception{
		http.csrf(csrf->csrf.disable())
		.exceptionHandling(exception->exception.authenticationEntryPoint(unauthorizedHandler))	//mapping authorized error to AuthEntryPointJWT
		.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.authorizeHttpRequests((auth)->auth.requestMatchers("/api/auth/**").permitAll()
				.requestMatchers("/v3/api-docs/**").permitAll()
				.requestMatchers("/swagger-ui/**").permitAll()
				.requestMatchers("/api/public/**").permitAll()
				.requestMatchers("/api/admin/**").permitAll()
				.requestMatchers("/api/test/**").permitAll()
				.requestMatchers("/images/**").permitAll()
				.anyRequest().authenticated());
		http.authenticationProvider(authenticationProvider);
		http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
		http.headers(headers->headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
		return http.build();
		
	}
	
	
	//WebSecurityCustomizer is used to completely bypass Spring Security for specific URLs, but in modern apps you should prefer permitAll() instead.
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web->web.ignoring().requestMatchers("/v2/api-docs",
				"/configuration/ui",
				"/swagger-resources/**",
				"/configuration/security",
				"/swagger-ui.html",
				"/webjars/**"));
	}
	
}
