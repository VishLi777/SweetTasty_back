package com.example.demo.Repositories;

import com.example.demo.Entity.Order;
import com.example.demo.Entity.Order_product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Order_productRepository extends JpaRepository<Order_product, Long> {

    List<Order_product> findAllByOrder(Order order);

}
