package com.example.demo.Entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "st_order_product")
public class Order_product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = ":id пользователя", name = "id", required = true, example = "13")
    private Long id;

    @ApiModelProperty(notes = "Ссылка на Заказ", name = "order", required = true)
    @ManyToOne(cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.PERSIST,
            CascadeType.REFRESH
    })
    @JoinColumn(name = "order_id")
    private Order order;


    @ApiModelProperty(notes = "Продукт в заказе", name = "product", required = true)
    @OneToOne(cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.PERSIST,
            CascadeType.REFRESH
    })
    @JoinColumn(name = "product_id")
    private Product product;


    @ApiModelProperty(notes = "Количество данного Продукта в заказе", name = "amountOfProduct",
            required = true, example = "7")
    @Positive(message = "Некорректное количество продуктов")
    @NotNull(message = "Некорректное количество продуктов")
    @Column(name = "amount_of_product")
    private Long amountOfProduct;

}
