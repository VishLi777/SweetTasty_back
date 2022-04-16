package com.example.demo.DTO;

import com.example.demo.Entity.Product;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {

    @ApiModelProperty(notes = ":id Продукта", name = "id", required = true, example = "13")
    private Long id;

    @ApiModelProperty(notes = "Название Продукта", name = "name", required = true, example = "EI540-A Чёрный")
    private String name;

    @ApiModelProperty(notes = "Цена Продукта", name = "price", required = true, example = "500")
    private String price;

    @ApiModelProperty(notes = "Путь до картинки", name = "pathFile", required = true,
            example = "3e9f88a8-6f12-4683-88c8-c6e6d3efc593fff.png")
    private String pathFile;

    @ApiModelProperty(notes = "Поле, отвечающее на вопрос:Является ли pathFile именем картинки?(Если нет, значит эта " +
            "прямая ссылка из инета)", name = "isName", required = true, example = "true")
    private Boolean isName;

    @ApiModelProperty(notes = "Название Типа, к которому принадлежит данный Продукт",
            name = "typeName", required = true, example = "Смартфоны")
    private String typeName;

    @ApiModelProperty(notes = "Название Категории, к которому принадлежит данный Продукт",
            name = "categoryName", required = true, example = "Apple")
    private String categoryName;

    @ApiModelProperty(notes = "Дополнительная инфа о Продукте", name = "productInfosDTO")
    private List<CharacteristicDTO> characteristicDTOS;

    public static ProductDTO create(Product product){
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setPrice(product.getPrice());
        productDTO.setPathFile(product.getPathFile());
        productDTO.setIsName(product.getIsName());
        productDTO.setTypeName(product.getTypeId().getName());
        productDTO.setCategoryName(product.getCategoryId().getName());
        productDTO.setCharacteristicDTOS(CharacteristicDTO.createList(product.getProductInfos()));
        return productDTO;
    }

    public static List<ProductDTO> createList(List<Product> list){
        List<ProductDTO> newList = new ArrayList<>();
        for(Product product: list){
            newList.add(create(product));
        }
        return newList;
    }

}
