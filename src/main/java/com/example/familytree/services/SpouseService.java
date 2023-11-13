package com.example.familytree.services;

import com.example.familytree.entities.PersonEntity;
import com.example.familytree.entities.SpouseEntity;
import com.example.familytree.models.dto.PersonDto;
import org.springframework.stereotype.Service;

@Service
public interface SpouseService {

    void createFirstSpouse(PersonEntity person);

    void createFirstSpouse(SpouseEntity spouse, boolean gender);

    void createSpouse(SpouseEntity spouse);
}
