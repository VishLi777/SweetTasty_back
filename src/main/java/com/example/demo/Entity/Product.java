package com.example.demo.Entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "st_product", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
    @SequenceGenerator(name = "product_seq", allocationSize = 1, initialValue = 2082)
    @ApiModelProperty(notes = ":id пользователя", name = "id", required = true, example = "13")
    private Long id;

    @ApiModelProperty(notes = "Название Продукта", name = "name", required = true, example = "EI540-A Чёрный")
    @Column(name = "name")
    private String name;


    @ApiModelProperty(notes = "Цена Продукта", name = "price", required = true, example = "500")
    @Column(name = "price")
    @PositiveOrZero(message = "Некорректная цена продукта")
    @NotNull(message = "Некорректная цена продукта")
    private String price;


    @ApiModelProperty(notes = "Путь до картинки", name = "pathFile", required = true,
            example = "3e9f88a8-6f12-4683-88c8-c6e6d3efc593fff.png")
    @Column(name = "path_file")
    private String pathFile;


    @ApiModelProperty(notes = "Поле, отвечающее на вопрос:Является ли pathFile именем картинки?(Если нет, значит эта " +
            "прямая ссылка из инета)", name = "isName", required = true, example = "true")
    @Column(name = "is_name")
    private Boolean isName;


    @ApiModelProperty(notes = "Тип, к которому принадлежит данный Продукт", name = "typeId", required = true)
    @ManyToOne
    @JoinColumn(name = "type_id")
    private Type typeId;


    @ApiModelProperty(notes = "Категория, к которой принадлежит данный Продукт", name = "categoryId", required = true)
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category categoryId;


    @ApiModelProperty(notes = "Дополнительная инфа о Продукте", name = "productInfos")
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Characteristic> productInfos;


    @ApiModelProperty(notes = "Строки заказов, к которым принадлежит данный Продукт", name = "order_product")
    @OneToOne(mappedBy = "product")
    private Order_product order_product;


    @ApiModelProperty(notes = "Дата создания Продукта", name = "dataOfCreate", required = true)
    @Column(name = "data_of_create")
    @DateTimeFormat
    private Long dataOfCreate;

}
