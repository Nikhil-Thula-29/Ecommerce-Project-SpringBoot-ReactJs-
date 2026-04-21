package com.nt.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.nt.payload.ProductDTO;
import com.nt.payload.ProductResponse;

public interface IProductService{

	public ProductDTO addProduct(ProductDTO productDTO, Long categoryId);

	public ProductResponse getAllProducts(Integer pageNumber,Integer pageSize,String sortBy,String sortOrder);

	public ProductResponse searchByCategory(Long categoryId,Integer pageNumber,Integer pageSize,String sortBy,String sortOrder);

	public ProductResponse getProductsByKeyword(String keyword,Integer pageNumber,Integer pageSize,String sortBy,String sortOrder);

	public ProductDTO updateProduct(ProductDTO productDTO, Long productId);

	public ProductDTO deleteProduct(Long productId);

	public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;


}
