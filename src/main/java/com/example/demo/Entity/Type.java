package com.example.demo.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "st_type",
uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
public class Type {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "type_seq")
    @SequenceGenerator(name = "type_seq", initialValue = 8, allocationSize = 1)
    @ApiModelProperty(notes = ":id Типа", name = "id", required = true, example = "13")
    private Long id;

    @ApiModelProperty(notes = "Название Типа", name = "name", required = true, example = "Смартфоны")
    @Column(name = "name")
    private String name;


    @ApiModelProperty(notes = "Продукты, принадлежащие к данному Типу", name = "products")
    @JsonIgnore
    @OneToMany(mappedBy = "typeId", cascade = CascadeType.ALL)
    private List<Product> products;


    @ApiModelProperty(notes = "Категории, принадлежащие к данному Типу", name = "categories")
    @OneToMany(mappedBy = "typeId", cascade = CascadeType.ALL)
    private List<Category> categories;

}
