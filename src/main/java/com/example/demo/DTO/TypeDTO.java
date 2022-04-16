package com.example.demo.DTO;

import com.example.demo.Entity.Type;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TypeDTO {

    @ApiModelProperty(notes = ":id Типа", name = "id", required = true, example = "13")
    private Long id;

    @ApiModelProperty(notes = "Название Типа", name = "name", required = true, example = "Смартфоны")
    private String name;

    @ApiModelProperty(notes = "Категории, принадлежащие к данному Типу", name = "categoriesDTO")
    private List<CategoryDTO> categories;

    public static TypeDTO creat(Type type){
        TypeDTO typeDTO = new TypeDTO();
        typeDTO.setId(type.getId());
        typeDTO.setName(type.getName());

        typeDTO.setCategories(CategoryDTO.createList(type.getCategories()));
        return typeDTO;
    }

    public static List<TypeDTO> createList(List<Type> list){
        List<TypeDTO> newList = new ArrayList<>();
        for(Type type: list){
            newList.add(creat(type));
        }
        return newList;
    }

}
