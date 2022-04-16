package com.example.demo.DTO;

import com.example.demo.Entity.Enum.EStatusOfOrder;
import com.example.demo.Entity.Order;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;import lombok.Getter;
import lombok.Setter;


// Уже сделал, но нигде не использую. Удалять жалко((( -----------------------------------------------------------------!!!!!


@Getter
@Setter
public class OrderDTO {

    @ApiModelProperty(notes = ":id Заказа", name = "id", required = true, example = "13")
    private Long id;

    @ApiModelProperty(notes = "Ссылки на order-productsDTO", name = "order_productsDTO", required = true)
    private List<Order_productDTO> order_productDTOS;

    @ApiModelProperty(notes = "Статус заказа (ACTIVE/INACTIVE)", name = "status", required = true, example = "ACTIVE")
    private EStatusOfOrder status;

    @ApiModelProperty(notes = "Общая сумма заказа", name = "totalSumCheck", required = true, example = "1950")
    private Long totalSumCheck;

    @ApiModelProperty(notes = "Дата создания заказа", name = "dataOfCreate", required = true)
    private Long dataOfCreate;

    public static OrderDTO create(Order order){
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setOrder_productDTOS(Order_productDTO.createList(order.getOrder_products()));
        orderDTO.setStatus(order.getStatus());
        orderDTO.setTotalSumCheck(order.getTotalSumCheck());
        orderDTO.setDataOfCreate(order.getDataOfCreate());
        return orderDTO;
    }

    public static List<OrderDTO> createList(List<Order> list){
        List<OrderDTO> newList = new LinkedList<>();
        for(Order order: list){
            newList.add(create(order));
        }
        return newList;
    }

}


