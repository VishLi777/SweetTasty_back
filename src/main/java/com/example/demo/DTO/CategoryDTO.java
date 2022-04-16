package com.example.demo.DTO;

import com.example.demo.Entity.Category;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;import lombok.Getter;
import lombok.Setter;import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDTO {

    @ApiModelProperty(notes = ":id Категории", name = "id", required = true, example = "13")
    private Long id;

    @ApiModelProperty(notes = "Название Категории", name = "name", required = true, example = "Apple")
    private String name;

    public static CategoryDTO create(Category category){
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setName(category.getName());
        return categoryDTO;
    }

    public static List<CategoryDTO> createList(List<Category> list){
        List<CategoryDTO> newList = new ArrayList<>();
        for(Category category : list){
            newList.add(create(category));
        }
        return newList;
    }

}
