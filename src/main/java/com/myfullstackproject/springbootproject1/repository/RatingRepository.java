package com.myfullstackproject.springbootproject1.repository;

import com.myfullstackproject.springbootproject1.model.Rating;
import com.myfullstackproject.springbootproject1.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByProduct(Product product);

    List<Rating> findByBuyerId(String buyerId);
}