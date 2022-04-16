package com.example.demo.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "st_characteristic")
public class Characteristic {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "characteristic_seq")
    @SequenceGenerator(name = "characteristic_seq", initialValue = 14398, allocationSize = 1)
    @ApiModelProperty(notes = ":id пользователя", name = "id", required = true, example = "13")
    private Long id;

    @ApiModelProperty(notes = "Заголовок", name = "title", required = true, example = "Ширина")
    @Column(name = "title")
    private String title;


    @ApiModelProperty(notes = "Описание", name = "description", required = true, example = "2 метра")
    @Column(name = "description")
    private String description;


    @ApiModelProperty(notes = "Продукт, к которому идёт описание", name = "product", required = true)
    @JsonIgnore
    @ManyToOne(cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH})
    @JoinColumn(name = "product_id")
    private Product product;

}
