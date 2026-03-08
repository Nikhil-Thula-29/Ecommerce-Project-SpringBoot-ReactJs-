package com.nt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nt.entity.Category;
import com.nt.entity.Product;

@Repository
public interface IProductRepository extends JpaRepository<Product, Long>{

	public List<Product> findByCategoryOrderByPriceAsc(Category cate);

	public List<Product> findByProductNameContainingIgnoreCase(String keyword);

	
}
