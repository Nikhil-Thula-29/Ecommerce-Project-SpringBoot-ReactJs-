package com.nt.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nt.entity.Cart;
import com.nt.entity.Category;
import com.nt.entity.Product;
import com.nt.exception.APIException;
import com.nt.exception.ResourceNotFoundException;
import com.nt.payload.CartDTO;
import com.nt.payload.ProductDTO;
import com.nt.payload.ProductResponse;
import com.nt.repository.CartRepository;
import com.nt.repository.ICategoryRepository;
import com.nt.repository.IProductRepository;
import com.nt.util.AuthUtil;

@Service
public class ProductService implements IProductService {

	@Autowired
	private IProductRepository prodRepo;

	@Autowired
	private ICategoryRepository catRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private AuthUtil authUtil;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private ICartService cartService;

	@Autowired
	private FileService fileService;

	@Value("${project.image}")
	private String path;

	@Override
	public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
		Category cat = catRepo.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
		//check if product already present or not
		boolean isProductNotPresent = true;
		List<Product> products = cat.getProduct();
		for (Product prod : products) {
			if (prod.getProductName().equalsIgnoreCase(productDTO.getProductName())) {
				isProductNotPresent = false;
				break;
			}
		}

		if (isProductNotPresent) {
			Product prod = modelMapper.map(productDTO, Product.class);
			double specialPrice = (prod.getPrice() - (prod.getDiscount() * 0.01) * prod.getPrice());
			prod.setImage("default.png");
			prod.setCategory(cat);
			prod.setSpecialPrice(specialPrice);
			prod.setUser(authUtil.loggedInUser()); //this is common util class to get user details
			Product prodResp = prodRepo.save(prod);
			ProductDTO dtoResp = modelMapper.map(prodResp, ProductDTO.class);
			return dtoResp;
		} else {
			throw new APIException("Product already exists!");
		}
	}

	@Override
	public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
		Page<Product> page = prodRepo.findAll(pageable);
		List<Product> prods = page.getContent();
		List<ProductDTO> prodDTO = prods.stream().map(prod -> modelMapper.map(prod, ProductDTO.class)).toList();
		ProductResponse prodResp = new ProductResponse();
		prodResp.setContent(prodDTO);
		prodResp.setPageNumber(page.getNumber());
		prodResp.setPageSize(page.getSize());
		prodResp.setTotalElements(page.getTotalElements());
		prodResp.setTotalPages(page.getTotalPages());
		prodResp.setLast(page.isLast());
		return prodResp;
	}

	@Override
	public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy,
			String sortOrder) {
		Category cate = catRepo.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("category", "categoryId", categoryId));
		Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
		Page<Product> page = prodRepo.findByCategoryOrderByPriceAsc(cate,pageable);
		List<Product> prods = page.getContent();
		if (prods.isEmpty()) {
			throw new APIException(cate.getCategoryName()+" category does not have any products");
		}
		List<ProductDTO> listDTO = prods.stream().map(prod -> modelMapper.map(prod, ProductDTO.class)).toList();
		ProductResponse resp = new ProductResponse();
		resp.setContent(listDTO);
		resp.setPageNumber(page.getNumber());
		resp.setPageSize(page.getSize());
		resp.setTotalElements(page.getTotalElements());
		resp.setTotalPages(page.getTotalPages());
		resp.setLast(page.isLast());
		return resp;
	}

	@Override
	public ProductResponse getProductsByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy,
			String sortOrder) {
		Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
		Page<Product> page = prodRepo.findByProductNameContainingIgnoreCase(keyword,pageable);
		List<Product> prodList = page.getContent();
		if (prodList.isEmpty()) {
			throw new APIException("Products not found with keyword: "+keyword);
		}
		List<ProductDTO> listDTO = prodList.stream().map(prod -> modelMapper.map(prod, ProductDTO.class)).toList();
		ProductResponse prodResp = new ProductResponse();
		prodResp.setContent(listDTO);
		prodResp.setPageNumber(page.getNumber());
		prodResp.setPageSize(page.getSize());
		prodResp.setTotalElements(page.getTotalElements());
		prodResp.setTotalPages(page.getTotalPages());
		prodResp.setLast(page.isLast());
		return prodResp;

	}

	@Override
	public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
		Optional<Product> prod = prodRepo.findById(productId);
		if (prod.isPresent()) {
			Product product = prod.get();
			Product pr = modelMapper.map(productDTO, Product.class);
			product.setCategory(pr.getCategory());
			product.setDescription(pr.getDescription());
			product.setDiscount(pr.getDiscount());
			product.setImage("default.png");
			product.setPrice(pr.getPrice());
			product.setProductName(pr.getProductName());
			product.setQuantity(pr.getQuantity());
			product.setSpecialPrice((pr.getPrice() - (pr.getDiscount() * 0.01) * pr.getPrice()));
			Product pro = prodRepo.save(product);

			//we need to do this after saving of product details
			//Existing products can be updated it's price and can be deleted so if that product is in users cart then we need to update or delete the same in user cart also this is that code.
			List<Cart> carts = cartRepository.findCartsByProductId(productId);
			List<CartDTO> cartDTOs = carts.stream().map(cart -> {
				CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
				List<ProductDTO> prodDTOs = cart.getCartItems().stream().map(item -> {
					ProductDTO prods = modelMapper.map(item.getProduct(), ProductDTO.class);
					prods.setQuantity(item.getQuantity());
					return prods;
				}).collect(Collectors.toList());
				cartDTO.setProducts(prodDTOs);
				return cartDTO;
			}).collect(Collectors.toList());
			cartDTOs.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), productId));

			ProductDTO resProd = modelMapper.map(pro, ProductDTO.class);
			return resProd;
		} else {
			throw new ResourceNotFoundException("Product", "productId", productId);
		}
	}

	@Override
	public ProductDTO deleteProduct(Long productId) {
		Optional<Product> prod = prodRepo.findById(productId);
		if (prod.isPresent()) {
			List<Cart> carts = cartRepository.findCartsByProductId(productId);
			carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(), productId));
			prodRepo.delete(prod.get());
			return modelMapper.map(prod, ProductDTO.class);
		} else {
			throw new ResourceNotFoundException("Product", "productId", productId);
		}
	}

	@Override
	public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
		//Get the product from DB
		Product prod = prodRepo.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
		//upload the image to server
		//Get the file name of uploaded image
		String fileName = fileService.uploadImage(path, image);

		//updating the new file name to the product
		prod.setImage(fileName);

		//save updaed product
		Product updatedProduct = prodRepo.save(prod); //here we are using put mapping we are not creating new product obj and setting only image to existing one so no override of existing product.
		//return DTO after mapping product to DTO.
		return modelMapper.map(updatedProduct, ProductDTO.class);
	}

}
