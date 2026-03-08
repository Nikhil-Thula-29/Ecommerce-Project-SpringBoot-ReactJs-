package com.nt.service;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.nt.entity.Category;
import com.nt.entity.Product;
import com.nt.exception.ResourceNotFoundException;
import com.nt.payload.ProductDTO;
import com.nt.payload.ProductResponse;
import com.nt.repository.ICategoryRepository;
import com.nt.repository.IProductRepository;

@Service
public class ProductService implements IProductService{
	
	@Autowired
	private IProductRepository prodRepo;
	
	@Autowired
	private ICategoryRepository catRepo;
	
	@Autowired
	private ModelMapper modelMapper;

	@Override
	public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
		Optional<Category> cat=catRepo.findById(categoryId);
		if(cat.isPresent()) {
			Product prod=modelMapper.map(productDTO, Product.class);
			double specialPrice=(prod.getPrice()-(prod.getDiscount()*0.01)*prod.getPrice());
			prod.setImage("default.png");
			prod.setCategory(cat.get());
			prod.setSpecialPrice(specialPrice);
			Product prodResp=prodRepo.save(prod);
			ProductDTO dtoResp=modelMapper.map(prodResp, ProductDTO.class);
			return dtoResp;
		}else {
			throw new ResourceNotFoundException("category", "categoryId", categoryId);
		}
		
	}

	@Override
	public ProductResponse getAllProducts() {
		List<Product> prods=prodRepo.findAll();
		List<ProductDTO> prodDTO=prods.stream().map(prod->modelMapper.map(prod, ProductDTO.class)).toList();
		ProductResponse prodResp=new ProductResponse();
		prodResp.setContent(prodDTO);
		return prodResp;
	}

	@Override
	public ProductResponse searchByCategory(Long categoryId) {
		Optional<Category> cate=catRepo.findById(categoryId);
		if(cate.isPresent()) {
			List<Product> prods=prodRepo.findByCategoryOrderByPriceAsc(cate.get());//rem option use .get()
			List<ProductDTO> listDTO=prods.stream().map(prod->modelMapper.map(prod, ProductDTO.class)).toList();
			ProductResponse resp=new ProductResponse();
			resp.setContent(listDTO);
			return resp;
		}else {
			throw new ResourceNotFoundException("category", "categoryId", categoryId);
		}
	}

	@Override
	public ProductResponse getProductsByKeyword(String keyword) {
		List<Product> prodList=prodRepo.findByProductNameContainingIgnoreCase(keyword);
		if(prodList.isEmpty()) {
			throw new ResourceNotFoundException("Product", "productName", keyword);
		}
		List<ProductDTO> listDTO=prodList.stream().map(prod->modelMapper.map(prod, ProductDTO.class)).toList();
		ProductResponse prodResp=new ProductResponse();
		prodResp.setContent(listDTO);
		return prodResp;
		
	}

	@Override
	public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
		Optional<Product> prod=prodRepo.findById(productId);
		if(prod.isPresent()) {
			Product product=prod.get();
			Product pr=modelMapper.map(productDTO, Product.class);
			product.setCategory(pr.getCategory());
			product.setDescription(pr.getDescription());
			product.setDiscount(pr.getDiscount());
			product.setImage("default.png");
			product.setPrice(pr.getPrice());
			product.setProductName(pr.getProductName());
			product.setQuantity(pr.getQuantity());
			product.setSpecialPrice((pr.getPrice()-(pr.getDiscount()*0.01)*pr.getPrice()));
			Product pro=prodRepo.save(product);
			ProductDTO resProd=modelMapper.map(pro, ProductDTO.class);
			return resProd;
		}else {
			throw new ResourceNotFoundException("Product", "productId", productId);
		}
	}

	@Override
	public ProductDTO deleteProduct(Long productId) {
		Optional<Product> prod=prodRepo.findById(productId);
		if(prod.isPresent()) {
			prodRepo.delete(prod.get());
			return modelMapper.map(prod, ProductDTO.class);
		}else {
			throw new ResourceNotFoundException("Product", "productId", productId);
		}
	}

	
}
