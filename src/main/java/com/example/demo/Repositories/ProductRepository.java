package com.example.demo.Repositories;

import com.example.demo.Entity.Category;
import com.example.demo.Entity.Product;
import com.example.demo.Entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Boolean existsByName(String name);
    List<Product> findAllByTypeIdAndCategoryId(Type typeId, Category categoryId);
    List<Product> findAllByTypeId(Type typeId);
    List<Product> findAllByCategoryId(Category categoryId);
    Product findByName(String name);

}
