package com.nt.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.nt.entity.Category;
import com.nt.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class CategoryController {

	@Autowired
	private CategoryService catserv;
	
	@GetMapping("/public/categories")
	//@RequestMapping(value = "/public/categories",method = RequestMethod.GET) 
	public ResponseEntity<List<Category>> getAllCategories(){
		 List<Category> caties=catserv.getAllCategories();
		 return new ResponseEntity<List<Category>>(caties,HttpStatus.OK);
	}
	
	
	@PostMapping("/public/categories")
	public ResponseEntity<String> createCategory(@Valid @RequestBody Category category) {
		catserv.createCategory(category);
		return new ResponseEntity<String>("Category created successfully",HttpStatus.CREATED);	
	}
	
	@DeleteMapping("/admin/categories/{catId}")
	public ResponseEntity<String> deleteCategory(@PathVariable (name = "catId") Long catId) {
		/*try {
			String msg=catserv.deleteCategory(catId);
			return new ResponseEntity<String>(msg,HttpStatus.OK);
			//return ResponseEntity.ok(msg);
			//return ResponseEntity.status(HttpStatus.OK).body(msg);
		}catch(ResponseStatusException e) {
			return new ResponseEntity<String>(e.getReason(),e.getStatusCode());
		}*/
		
		String msg=catserv.deleteCategory(catId);
		return new ResponseEntity<String>(msg,HttpStatus.OK);
		
	}
	
	
	@PatchMapping("/admin/categories/{catId}")
	public ResponseEntity<String> updateCategory(@RequestBody Category category,
												@PathVariable(name="catId") Long catId){
		
		//we are using direct exceptional handling so try catch not recommended if not use try catch..
		/*try {
			catserv.updateCategory(category,catId);
			return new ResponseEntity<String>("Category with id "+catId+" updated successfully",HttpStatus.OK);
		}catch(ResponseStatusException e) {
			return new ResponseEntity<String>(e.getReason(),e.getStatusCode());
		}*/
		
		catserv.updateCategory(category,catId);
		return new ResponseEntity<String>("Category with id "+catId+" updated successfully",HttpStatus.OK);
		
	}
}
