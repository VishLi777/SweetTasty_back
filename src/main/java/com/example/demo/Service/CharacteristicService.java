package com.example.demo.Service;

import com.example.demo.Entity.Characteristic;
import com.example.demo.Repositories.CharacteristicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CharacteristicService {

    @Autowired
    CharacteristicRepository characteristicRepository;

    /** Добавление новой дополнительной информации о продукте в БД */
    public void addCharacteristic(Characteristic characteristic){
        characteristicRepository.save(characteristic);
    }

}
