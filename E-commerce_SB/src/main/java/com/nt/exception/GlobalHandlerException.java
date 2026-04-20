package com.nt.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nt.payload.APIResponse;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestControllerAdvice
public class GlobalHandlerException {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String,String>> myMethodArgumentNotValidException(MethodArgumentNotValidException e){
		Map<String,String> response=new HashMap<>();
		e.getBindingResult().getAllErrors().forEach(err->{
			String filedName= ((FieldError) err).getField();
			String message=err.getDefaultMessage();
			response.put(filedName, message);
		});
		return new ResponseEntity<Map<String,String>>(response,HttpStatus.BAD_REQUEST);
	}
	
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<APIResponse> myResourceNotFoundException(ResourceNotFoundException e){
		String message=e.getMessage();
		APIResponse resp=new APIResponse();
		resp.setMessage(message);
		resp.setStatus(false);
		return new ResponseEntity<APIResponse>(resp,HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(APIException.class)
	public ResponseEntity<APIResponse> myAPIException(APIException e){
		String msg=e.getMessage();
		APIResponse resp=new APIResponse();
		resp.setMessage(msg);
		resp.setStatus(false);
		return new ResponseEntity<APIResponse>(resp,HttpStatus.BAD_REQUEST);
	}
	
	//these are for if @valid annotation if we give less length like that it gives ConstraintViolationException like in entity we have
	//@NotBlank @Size(min=3,message="Product name must contain atleast 3 characters") private String productName;
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Map<String, String>> handleConstraintViolation(
	        ConstraintViolationException ex) {

	    Map<String, String> errors = new HashMap<>();

	    ex.getConstraintViolations().forEach(violation -> {
	        String field = violation.getPropertyPath().toString();
	        String message = violation.getMessage();
	        errors.put(field, message);
	    });

	    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

}
