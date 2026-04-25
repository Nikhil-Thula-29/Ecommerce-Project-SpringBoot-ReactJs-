package com.nt.security.jwt;

import java.security.Key;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import com.nt.security.services.UserDetailsImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtils {

	private static final Logger logger=LoggerFactory.getLogger(JwtUtils.class);
	
	@Value("${spring.app.jwtExpirationMs}")
	private int jwtExpirationMs;
	@Value("${spring.app.jwtSecret}")
	private String jwtSecret;
	
	@Value("${spring.ecom.app.jwtCookieName}")
	private String jwtCookie;
	
	//Getting JWT From Header
	/*public String getJWTFromHeader(HttpServletRequest request) {
		String bearerToken=request.getHeader("Authorization");
		if(bearerToken!=null && bearerToken.startsWith("Bearer ")) {
			return bearerToken=bearerToken.substring(7);
		}
		return null;
	}*/
	
	//Getting JWT From cookie.(We are using cookies instead of headers)
	public String getJWTFromCookies(HttpServletRequest request) {
		Cookie cookie=WebUtils.getCookie(request, jwtCookie); //rem WebUtils class.
		if(cookie!=null) {
			return cookie.getValue();
		}else {
			return null;
		}
		
	}
	
	
	//Generate JWT by ResponseCookie it uses generateTokenFromUserName
	public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {
		String jwt=generateTokenFromUserName(userPrincipal.getUsername());
		ResponseCookie cookie=ResponseCookie.from(jwtCookie,jwt)
				.path("/")
				.maxAge(24*60*60*10)
				.httpOnly(true)  //this can stop xss attacks (cross-site scripting executing js code)
		        .secure(false) //Only works on HTTPS (recommended for production) we use need to use true if we have ssl certicate we need to make it true i.e http and if we are using https with ssl certifcate we need to make to true
		        .sameSite("Strict") //this can stop csrf attacks( redirecting to other pages) cross site request forgery.
				.build();
		return cookie;	
	}
	
	
	public ResponseCookie getCleanJwtCookie() {
		ResponseCookie cookie=ResponseCookie.from(jwtCookie,null)
				.path("/")
				.maxAge(0)  
		        .httpOnly(true)
		        .secure(false)
		        .sameSite("Strict")
				.build();
		return cookie;	
	}
	
	//Generating Token from username
	public String generateTokenFromUserName(String userName) {
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
