package com.example.demo.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "st_category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq")
    @SequenceGenerator(name = "category_seq", initialValue = 214, allocationSize = 1)
    @ApiModelProperty(notes = ":id Категории", name = "id", required = true, example = "13")
    private Long id;

    @ApiModelProperty(notes = "Название Категории", name = "name", required = true, example = "Apple")
    @Column(name = "name")
    private String name;


    @ApiModelProperty(notes = "Продукты, которые принадлежат данной категории", name = "products")
    @JsonIgnore
    @OneToMany(mappedBy = "categoryId", cascade = CascadeType.ALL)
    private List<Product> products;


    @ApiModelProperty(notes = "Тип, к которому принадлежит данная Категория", name = "typeId", required = true)
    @ManyToOne
    @JoinColumn(name = "type_id")
    private Type typeId;

}
