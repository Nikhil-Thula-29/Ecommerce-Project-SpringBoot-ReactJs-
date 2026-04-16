package com.nt.security.config;


import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.nt.entity.AppRole;
import com.nt.entity.Role;
import com.nt.entity.User;
import com.nt.repository.RoleRepository;
import com.nt.repository.UserRepository;
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
	public SecurityFilterChain filterChain(HttpSecurity http,DaoAuthenticationProvider authenticationProvider)throws Exception{
		http.csrf(csrf->csrf.disable())
		.exceptionHandling(exception->exception.authenticationEntryPoint(unauthorizedHandler))	//mapping authorized error to AuthEntryPointJWT
		.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.authorizeHttpRequests((auth)->auth.requestMatchers("/api/auth/**").permitAll()
				.requestMatchers("/v3/api-docs/**").permitAll()
				.requestMatchers("/swagger-ui/**").permitAll()
				.requestMatchers("/h2-console/**").permitAll()
				//.requestMatchers("/api/public/**").permitAll()
				//.requestMatchers("/api/admin/**").permitAll()
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
	
	
	//This we wrote because of using h2-console but if we use normal db then we can run scripts to insert dummy data of user,admin etc.
	 @Bean
	    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
	        return args -> {
	            // Retrieve or create roles
	            Optional<Role> optional = roleRepository.findByRoleName(AppRole.ROLE_USER);
	            Role userRole;
	            if(optional.isPresent()) {
	            	userRole=optional.get();
	            }else {
	            	Role newUserRole=new Role(AppRole.ROLE_USER);
	            	userRole=roleRepository.save(newUserRole);
	            }
	                    
	            //above or we can develop like this with .orElseGet() which assigns value at the end completely to sellerRole
	            Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
	                    .orElseGet(() -> {
	                        Role newSellerRole = new Role(AppRole.ROLE_SELLER);
	                        return roleRepository.save(newSellerRole);
	                    });

	            Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
	                    .orElseGet(() -> {
	                        Role newAdminRole = new Role(AppRole.ROLE_ADMIN);
	                        return roleRepository.save(newAdminRole);
	                    });

	            Set<Role> userRoles = Set.of(userRole);
	            Set<Role> sellerRoles = Set.of(sellerRole);
	            Set<Role> adminRoles = Set.of(userRole, sellerRole, adminRole);


	            // Create users if not already present
	            if (!userRepository.existsByUserName("user1")) {
	                User user1 = new User("user1", "user1@example.com", passwordEncoder.encode("password1"));
	                userRepository.save(user1);
	            }

	            if (!userRepository.existsByUserName("seller1")) {
	                User seller1 = new User("seller1", "seller1@example.com", passwordEncoder.encode("password2"));
	                userRepository.save(seller1);
	            }

	            if (!userRepository.existsByUserName("admin")) {
	                User admin = new User("admin", "admin@example.com", passwordEncoder.encode("adminPass"));
	                userRepository.save(admin);
	            }

	            // Update roles for existing users
	            userRepository.findByUserName("user1").ifPresent(user -> {
	                user.setRoles(userRoles);
	                userRepository.save(user);
	            });

	            userRepository.findByUserName("seller1").ifPresent(seller -> {
	                seller.setRoles(sellerRoles);
	                userRepository.save(seller);
	            });

	            userRepository.findByUserName("admin").ifPresent(admin -> {
	                admin.setRoles(adminRoles);
	                userRepository.save(admin);
	            });
	        };
	    }
	 
	 @Bean
		public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) {
			return builder.getAuthenticationManager();
		}
	
}
