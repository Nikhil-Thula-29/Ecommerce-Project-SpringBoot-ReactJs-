package com.nt.service;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.nt.entity.Category;
import com.nt.exception.APIException;
import com.nt.exception.ResourceNotFoundException;
import com.nt.payload.CategoryDTO;
import com.nt.payload.CategoryResponse;
import com.nt.repository.ICategoryRepository;


@Service
public class CategoryService implements ICategoryService {


	@Autowired
	private ICategoryRepository catRepo;
	
	@Autowired
	private ModelMapper modelMapper;
	
	
	@Override
	public CategoryResponse getAllCategories(Integer pageNumber,Integer pageSize,String sortBy,String sortDir) {
		
		Sort sort=sortDir.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
		Pageable pageable=PageRequest.of(pageNumber, pageSize,sort);
		Page<Category> page=catRepo.findAll(pageable);
		
		List<Category> catlist=page.getContent();
		if(catlist.isEmpty()) {
			throw new APIException("No Category created till now!!");
		}
		List<CategoryDTO> response=catlist.stream().map(cat->modelMapper.map(cat,CategoryDTO.class)).toList();
		CategoryResponse catresp=new CategoryResponse();
		catresp.setContent(response);
		catresp.setPageNumber(page.getNumber());
		catresp.setPageSize(page.getSize());
		catresp.setTotalElements(page.getTotalElements());
		catresp.setTotalPages(page.getTotalPages());	
		catresp.setLast(page.isLast());	
		return catresp;
	}

	@Override
	public CategoryDTO createCategory(CategoryDTO categoryDTO) {
		Category cate=modelMapper.map(categoryDTO, Category.class);
		Category cat=catRepo.findByCategoryName(cate.getCategoryName());
		if(cat!=null) {
			throw new APIException("Category with the name "+cat.getCategoryName()+" is already exists!!!");
		}
		
		Category savedCat=catRepo.save(cate);
		CategoryDTO dto=modelMapper.map(savedCat, CategoryDTO.class);
		return dto;
	}

	@Override
	public CategoryDTO deleteCategory(Long categoryId) {
		/*Predicate<Category> predicate=new Predicate<Category>() {
			
			@Override
			public boolean test(Category t) {
				return t.getCategoryId().equals(categoryId);
			}
		};
		List<Category> list=catRepo.findAll();
		Category category=list.stream().filter(predicate).findFirst().orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource Not Found"));
		catRepo.delete(category);
		return categoryId+" deleted successfully";*/
		
		
		Optional<Category> cat=catRepo.findById(categoryId);
		if(cat.isPresent()) {
			Category cat1=cat.get();
			catRepo.delete(cat1);
			CategoryDTO caty=modelMapper.map(cat1,CategoryDTO.class);
			return caty;
		}else {
			throw new ResourceNotFoundException("Category","categoryId",categoryId);
		}
	}

	@Override
	public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
		/*List<Category> list=catRepo.findAll();
		Optional<Category> cats=list.stream().filter(c->c.getCategoryId().equals(categoryId)).findFirst();
		if(cats.isPresent()) {
			Category existCat=cats.get();
			existCat.setCategoryName(category.getCategoryName());
			Category upCat=catRepo.save(existCat);
			return upCat;
		}else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource Not Found");
		}*/
		
		Category cate=modelMapper.map(categoryDTO,Category.class);
		Optional<Category> cat=catRepo.findById(categoryId);
		if(cat.isPresent()) {
			Category cat1=cat.get();
			cat1.setCategoryName(cate.getCategoryName());
			cat1=catRepo.save(cat1);
			CategoryDTO catDTO=modelMapper.map(cat1,CategoryDTO.class);
			return catDTO;
		}else {
			throw new ResourceNotFoundException("Category","categoryId",categoryId);
		}
	}
	
	
}
