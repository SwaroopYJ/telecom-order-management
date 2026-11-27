package com.telecom.oms.product.repository;

import com.telecom.oms.product.entity.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductPriceRepository
        extends JpaRepository<ProductPrice, Long> {

    List<ProductPrice> findByProductId(Long productId);
}