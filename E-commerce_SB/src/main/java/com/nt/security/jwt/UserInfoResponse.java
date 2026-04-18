package com.nt.security.jwt;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse {

	private Long id;
	private String jwtToken;
	private String username;
	private List<String> roles;
	public UserInfoResponse(Long id, String username, List<String> roles) {
		super();
		this.id = id;
		this.username = username;
		this.roles = roles;
	}
	
	
	
}
