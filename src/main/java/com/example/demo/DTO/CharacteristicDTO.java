package com.example.demo.DTO;

import com.example.demo.Entity.Characteristic;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CharacteristicDTO {

    @ApiModelProperty(notes = ":id Информации о продукте", name = "id", required = true, example = "13")
    private Long id;

    @ApiModelProperty(notes = "Заголовок", name = "title", required = true, example = "Ширина")
    private String title;

    @ApiModelProperty(notes = "Описание", name = "description", required = true, example = "2 метра")
    private String description;

    public static CharacteristicDTO create(Characteristic characteristic){
        CharacteristicDTO characteristicDTO = new CharacteristicDTO();
        characteristicDTO.setId(characteristic.getId());
        characteristicDTO.setTitle(characteristic.getTitle());
        characteristicDTO.setDescription(characteristic.getDescription());
        return characteristicDTO;
    }

    public static List<CharacteristicDTO> createList(List<Characteristic> list){
        List<CharacteristicDTO> newList = new ArrayList<>();
        for(Characteristic characteristic: list){
            newList.add(create(characteristic));
        }
        return newList;
    }

}
