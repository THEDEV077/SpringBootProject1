package com.myfullstackproject.springbootproject1.repository;

import com.myfullstackproject.springbootproject1.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}