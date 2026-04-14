package com.nt.security.jwt;

import java.security.Key;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtils {

	private static final Logger logger=LoggerFactory.getLogger(JwtUtils.class);
	
	@Value("${spring.app.jwtExpirationMs}")
	private int jwtExpirationMs;
	@Value("${spring.app.jwtSecret}")
	private String jwtSecret;
	
	
	
	
	//Getting JWT From Header
	public String getJWTFromHeader(HttpServletRequest request) {
		String bearerToken=request.getHeader("Authorization");
		if(bearerToken!=null && bearerToken.startsWith("Bearer ")) {
			return bearerToken=bearerToken.substring(7);
		}
		return null;
	}
	
	//Generating Token from username
	public String generateTokenFromUserName(UserDetails userDetails) {
		String userName=userDetails.getUsername();
		return Jwts.builder()
				.setSubject(userName)
				.setIssuedAt(new Date())
				.setExpiration(new Date(new Date().getTime()+jwtExpirationMs))
				.signWith(key())
				.compact();
	}
	
	//Getting username from JWT Token
	public String getUserNameFromJwtToken(String token) {
		Claims claims= Jwts.parserBuilder()
				.setSigningKey(key())
				.build()
				.parseClaimsJws(token) //validates token
				.getBody();
		return claims.getSubject();	//In jwt body is called as claims	
	}
	
	//Generate signing key
	//It will verifying JWT tokens
	public Key key() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}
	
	
	//validate JWT Token
	public boolean validateJwtToken(String authToken) {
		try {
			System.out.println("Validate");
			Jwts.parserBuilder()
			.setSigningKey(key())
			.build()
			.parseClaimsJws(authToken); //validates token
			return true;
		}catch(MalformedJwtException e) {
			 logger.error("Invalid JWT Token: {}",e.getMessage());
		}catch(ExpiredJwtException e) {
			 logger.error("Invalid JWT Token: {}",e.getMessage());
		}catch(UnsupportedJwtException e) {
			 logger.error("Invalid JWT Token: {}",e.getMessage());
		}catch(IllegalArgumentException e) {
			 logger.error("Invalid JWT Token: {}",e.getMessage());
		}
		return false;
	}
}
