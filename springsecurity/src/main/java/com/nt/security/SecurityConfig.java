package com.nt.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity //used for enabling the PreAuthorize annotation
public class SecurityConfig {

	//authorization
	@Bean
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)throws Exception{
		http.authorizeHttpRequests((requests)->requests.anyRequest().authenticated());
		//http.formLogin(withDefaults());
		http.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		http.httpBasic(withDefaults()); //used for postman
		return http.build();
		
	}
	
	//authentication
	@Bean
	public UserDetailsService userDetailsService() {
		UserDetails user1=User.withUsername("admin")
				.password("{noop}admin123")
				.roles("ADMIN")
				.build();
		UserDetails user2=User.withUsername("user")
				.password("{noop}user123")
				.roles("USER")
				.build();
		return new InMemoryUserDetailsManager(user1,user2);
	}
}
