package com.example.demo.DTO;

import com.example.demo.Entity.Order_product;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Order_productDTO {

    @ApiModelProperty(notes = "Продукт в заказе", name = "productDTO", required = true)
    private ProductDTO productDTO;

    @ApiModelProperty(notes = "Количество данного Продукта в заказе", name = "amountOfProduct",
            required = true, example = "7")
    private Long amountOfProduct;

    public static Order_productDTO create(Order_product order_product){
        Order_productDTO order_productDTO = new Order_productDTO();
        order_productDTO.setProductDTO(ProductDTO.create(order_product.getProduct()));
        order_productDTO.setAmountOfProduct(order_product.getAmountOfProduct());
        return order_productDTO;
    }

    public static List<Order_productDTO> createList(List<Order_product> list){
        List<Order_productDTO> newList = new LinkedList<>();
        for(Order_product order_product: list){
            newList.add(create(order_product));
        }
        return newList;
    }

}
