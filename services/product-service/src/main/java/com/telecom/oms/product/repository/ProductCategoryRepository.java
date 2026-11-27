package com.telecom.oms.product.repository;

import com.telecom.oms.product.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductCategoryRepository
        extends JpaRepository<ProductCategory, Long> {

    Optional<ProductCategory> findByCode(String code);

    boolean existsByCode(String code);
}
