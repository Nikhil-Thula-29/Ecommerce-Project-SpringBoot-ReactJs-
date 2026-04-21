package com.nt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nt.config.AppConstants;
import com.nt.payload.CategoryDTO;
import com.nt.payload.CategoryResponse;
import com.nt.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class CategoryController {

	@Autowired
	private CategoryService catserv;
	
	
	//testing
	@GetMapping("/echo")
	public ResponseEntity<String> echoMessage(@RequestParam(name = "message",defaultValue = "test") String message){
		return new ResponseEntity<String>("echoedMessage " +message,HttpStatus.OK);
	}
	
	@GetMapping("/public/categories")
	//@RequestMapping(value = "/public/categories",method = RequestMethod.GET) 
	public ResponseEntity<CategoryResponse> getAllCategories(@RequestParam(name="pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
															 @RequestParam(name="pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false)Integer pageSize,
															 @RequestParam(name="sortBy",defaultValue=AppConstants.SORT_CATEGORIES_BY,required = false)String sortBy,
															 @RequestParam(name="sortDir",defaultValue = AppConstants.SORT_DIR,required = false)String sortDir){
		 CategoryResponse caties=catserv.getAllCategories(pageNumber,pageSize,sortBy,sortDir);
		 return new ResponseEntity<CategoryResponse>(caties,HttpStatus.OK);
	}
	
	
	@PostMapping("/public/categories")
	public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
		CategoryDTO savCatDTO=catserv.createCategory(categoryDTO);
		return new ResponseEntity<CategoryDTO>(savCatDTO,HttpStatus.CREATED);	
	}
	
	@DeleteMapping("/admin/categories/{catId}")
	public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable (name = "catId") Long catId) {
		/*try {
			String msg=catserv.deleteCategory(catId);
			return new ResponseEntity<String>(msg,HttpStatus.OK);
			//return ResponseEntity.ok(msg);
			//return ResponseEntity.status(HttpStatus.OK).body(msg);
		}catch(ResponseStatusException e) {
			return new ResponseEntity<String>(e.getReason(),e.getStatusCode());
		}*/
		
		CategoryDTO cat=catserv.deleteCategory(catId);
		return new ResponseEntity<CategoryDTO>(cat,HttpStatus.OK);
		
	}
	
	
	@PatchMapping("/admin/categories/{catId}")
	public ResponseEntity<CategoryDTO> updateCategory(@RequestBody CategoryDTO categoryDTO,
												@PathVariable(name="catId") Long catId){
		
		//we are using direct exceptional handling so try catch not recommended if not use try catch..
		/*try {
			catserv.updateCategory(category,catId);
			return new ResponseEntity<String>("Category with id "+catId+" updated successfully",HttpStatus.OK);
		}catch(ResponseStatusException e) {
			return new ResponseEntity<String>(e.getReason(),e.getStatusCode());
		}*/
		
		CategoryDTO catDTO=catserv.updateCategory(categoryDTO,catId);
		return new ResponseEntity<CategoryDTO>(catDTO,HttpStatus.OK);
		
	}
}
