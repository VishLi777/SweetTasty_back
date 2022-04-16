package com.example.demo.Repositories;

import com.example.demo.Entity.Category;
import com.example.demo.Entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByNameAndTypeId(String name, Type typeId);

}
