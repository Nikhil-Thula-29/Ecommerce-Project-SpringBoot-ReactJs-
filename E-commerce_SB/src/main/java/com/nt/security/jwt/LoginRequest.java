package com.nt.security.jwt;

import lombok.Data;

@Data
public class LoginRequest {

	private String username;
	private String password;
	
}
