package com.nt.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nt.entity.Category;
import com.nt.entity.Product;

@Repository
public interface IProductRepository extends JpaRepository<Product, Long>{

	public Page<Product> findByCategoryOrderByPriceAsc(Category cate,Pageable pageable);

	public Page<Product> findByProductNameContainingIgnoreCase(String keyword,Pageable pageable);

	
}
