package com.myfullstackproject.springbootproject1.repository;

import com.myfullstackproject.springbootproject1.model.ProductImage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProduct_IdOrderByDisplayOrderAsc(Long productId);
    
    @Modifying
    @Transactional
    void deleteByProduct_IdAndId(Long productId, Long imageId);
}
