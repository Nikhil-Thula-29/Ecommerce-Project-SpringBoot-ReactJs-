package com.nt.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthTokenFilter extends OncePerRequestFilter{
	
	@Autowired
	private JwtUtils jwtUtils;
	@Autowired
	private UserDetailsService userDetailsService;
	
	private static final Logger logger=LoggerFactory.getLogger(AuthTokenFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		logger.debug("AuthTokenFilter called for URI: {}",request.getRequestURI());
		try {
			String jwt=parseJwt(request);
			if(jwt!=null && jwtUtils.validateJwtToken(jwt)) {
				String userName=jwtUtils.getUserNameFromJwtToken(jwt);
				UserDetails userDetails=userDetailsService.loadUserByUsername(userName);
				//upto here we got username and userdetails, roles but spring dont know this user is having this roles, and this username i.e not authenticated so we need 
				//to authenticate using UsernamePasswordAuthenticationToken.
				UsernamePasswordAuthenticationToken authentication=new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());
				//This helps in Adds extra request information to authentication like ipaddress,sessionid,etc
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);//we will store the details for role based access in security context
				logger.debug("Roles from JWT: {}",userDetails.getAuthorities());				
			}
		}catch(Exception e) {
			logger.debug("cannot set user authentication {}",e);
		}
		filterChain.doFilter(request, response);//will follow next filters.
	}

	private String parseJwt(HttpServletRequest request) {
		String jwt=jwtUtils.getJWTFromHeader(request);
		return jwt;
	}

}
