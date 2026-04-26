package com.nt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

//This file is for adding token in swagger to check
//url to check:/swagger-ui/index.html
//There are different annotations in swagger which we write on method like @Tag for creating swaggers,@operation to get description check in gpt
@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		SecurityScheme bearerScheme=new SecurityScheme()
				.type(SecurityScheme.Type.HTTP)
				.scheme("bearer")
				.bearerFormat("JWT")
				.description("JWT Bearer Token");
		SecurityRequirement bearerRequirement=new SecurityRequirement()
				.addList("Bearer Authentication");
		return new OpenAPI()
				.components(new Components().addSecuritySchemes("Bearer Authentication", bearerScheme))
				.addSecurityItem(bearerRequirement);
				
		
	}
	
}
