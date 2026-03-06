package com.nt.service;


import com.nt.payload.CategoryDTO;
import com.nt.payload.CategoryResponse;


public interface ICategoryService {

	public CategoryResponse getAllCategories(Integer pageNumber,Integer pageSize,String sortBy,String sortDir);
	
	public CategoryDTO createCategory(CategoryDTO categoryDTO);
	
	public CategoryDTO deleteCategory(Long categoryId);
	
	public CategoryDTO updateCategory(CategoryDTO categoryDTO,Long categoryId);
}
