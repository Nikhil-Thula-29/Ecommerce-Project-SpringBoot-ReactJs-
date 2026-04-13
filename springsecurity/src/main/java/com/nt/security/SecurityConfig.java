package com.nt.security;

import static org.springframework.security.config.Customizer.withDefaults;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity //used for enabling the PreAuthorize annotation
public class SecurityConfig {

	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private AuthEntryPointJWT unauthorizedHandler;
	
	@Autowired
	@Lazy //because to over come circular depency authTokenFilter dependent on that class but in that class we are using userDetailService for which we are creating bean in this class so circular depency to overcome use lazy.
	private AuthTokenFilter authTokenFilter;
	
	//authorization
	@Bean
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)throws Exception{
		http.authorizeHttpRequests((requests)->requests.requestMatchers("/h2-console/**").permitAll()
				.requestMatchers("/signin").permitAll()
				.anyRequest().authenticated());
		//http.formLogin(withDefaults());
		http.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		http.exceptionHandling(exception->exception.authenticationEntryPoint(unauthorizedHandler));//mapping authorized error to AuthEntryPointJWT
		//http.httpBasic(withDefaults()); //used for postman
		http.headers(headers->headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
		http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
		http.csrf(csrf->csrf.disable()); 
		return http.build();
		
	}
	
	//authentication
	
	//Note imp:
	//This is we have commented because it is giving starting without table creation so getting error at creatuser so we are using commandlinerunner so that will create on application startup and separate jdbcuserdetailsmanager bean creation.
//	@Bean
//	public UserDetailsService userDetailsService() {
//		UserDetails user1=User.withUsername("admin")
//				.password(passwordEncoder().encode("admin123"))
//				.roles("ADMIN")
//				.build();
//		UserDetails user2=User.withUsername("user")
//				.password(passwordEncoder().encode("user123"))
//				.roles("USER")
//				.build();
//		JdbcUserDetailsManager userDetailsManager=new JdbcUserDetailsManager(dataSource); //This is to fetch data from db.
//		userDetailsManager.createUser(user1);
//		userDetailsManager.createUser(user2);
//		return userDetailsManager;
//		//return new InMemoryUserDetailsManager(user1,user2);  //This is used for inmemory db
//	}
	
	@Bean
	public UserDetailsService userDetailsService() {
		return new JdbcUserDetailsManager(dataSource);
	}
	
	@Bean
	public CommandLineRunner intiData(UserDetailsService userDetailsService) {
		JdbcUserDetailsManager manager=(JdbcUserDetailsManager) userDetailsService; //here we dont have direct object of jdbcuserdetailsmanager from userdetailsservice so we need to type case and use that.
		return args->{
			if (!manager.userExists("admin")) {
			UserDetails user1=User.withUsername("admin")
					.password(passwordEncoder().encode("admin123"))
					.roles("ADMIN")
					.build();
			manager.createUser(user1);
			}
			if (!manager.userExists("user")) {
			UserDetails user2=User.withUsername("user")
					.password(passwordEncoder().encode("user123"))
					.roles("USER")
					.build();
			manager.createUser(user2);
			}
		};
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) {
		return builder.getAuthenticationManager();
	}
}
