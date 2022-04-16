package com.example.demo.Entity.Response;

import com.example.demo.DTO.ProductDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductWIthNecessaryParameters {

    @ApiModelProperty(value = "Продукты без лишних полей")
    List<ProductDTO> list;

    @ApiModelProperty(value = "Количество всех существующих продуктов")
    int amountOfAllProducts;

    @ApiModelProperty(value = "Минимальная цена по данному запросу")
    String maxPrice;

    @ApiModelProperty(value = "Максимальная цена по данному запросу")
    String minPrice;

    public ProductWIthNecessaryParameters(List<ProductDTO> listDTO, int amountOfAllProducts, String maxPrice, String minPrice) {
        this.list = listDTO;
        this.amountOfAllProducts = amountOfAllProducts;
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
    }



}
