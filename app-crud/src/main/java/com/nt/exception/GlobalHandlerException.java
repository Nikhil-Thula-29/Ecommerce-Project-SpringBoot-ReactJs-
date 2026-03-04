package com.nt.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalHandlerException {

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<String> handleUniqueEmail(){
		return new ResponseEntity<String>("Email already exists. It must be unique",HttpStatus.BAD_REQUEST);
	}
}
