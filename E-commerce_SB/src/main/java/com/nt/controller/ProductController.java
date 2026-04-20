package com.nt.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nt.payload.ProductDTO;
import com.nt.payload.ProductResponse;
import com.nt.service.IProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class ProductController {
	
	@Autowired
	private IProductService prodServ;

	
	@PostMapping("/admin/categories/{categoryId}/product")
	public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO,@PathVariable(name="categoryId") Long categoryId){
		ProductDTO prodDTO=prodServ.addProduct(productDTO,categoryId);
		return new ResponseEntity<ProductDTO>(prodDTO,HttpStatus.CREATED);
	}
	
	
	@GetMapping("/public/products")
	public ResponseEntity<ProductResponse> getAllProducts(){
		ProductResponse prodResp= prodServ.getAllProducts();
		return new ResponseEntity<ProductResponse>(prodResp,HttpStatus.OK);
		
	}
	
	@GetMapping("/public/categories/{categoryId}/product")
	public ResponseEntity<ProductResponse> searchByCategory(@PathVariable(name="categoryId")Long categoryId){
		ProductResponse prodResp=prodServ.searchByCategory(categoryId);
		return new ResponseEntity<ProductResponse>(prodResp,HttpStatus.OK);
	}
	
	
	@GetMapping("/public/products/keyword/{keyword}")
	public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable (name="keyword") String keyword){
		ProductResponse prodResp=prodServ.getProductsByKeyword(keyword);
		return new ResponseEntity<ProductResponse>(prodResp,HttpStatus.OK);
	}
	
	@PutMapping("/public/products/{productId}")
	public ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody ProductDTO productDTO,
													@PathVariable(name="productId") Long productId){
		ProductDTO prodDTO=prodServ.updateProduct(productDTO,productId);
		return new ResponseEntity<ProductDTO>(prodDTO,HttpStatus.OK);
	}
	
	@DeleteMapping("/admin/products/{productId}")
	public ResponseEntity<ProductDTO> deleteProduct(@PathVariable(name="productId") Long productId){
		ProductDTO prodDTO=prodServ.deleteProduct(productId);
		return new ResponseEntity<ProductDTO>(prodDTO,HttpStatus.OK);
	}
	
	@PutMapping("/products/{productId}/image")
	public ResponseEntity<ProductDTO> updateProductImage(@PathVariable(name="productId") Long productId,@RequestParam("image") MultipartFile image) throws IOException{
		ProductDTO updatedProduct=prodServ.updateProductImage(productId,image);
		return new ResponseEntity<ProductDTO>(updatedProduct,HttpStatus.OK);
	}
}
