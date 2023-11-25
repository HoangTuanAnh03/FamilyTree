package com.example.familytree.services;

import com.example.familytree.entities.PersonEntity;
import com.example.familytree.models.dto.PersonDto;
import com.example.familytree.models.dto.UpdatePersonDto;
import com.example.familytree.models.response.InfoAddPersonResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PersonService {
    PersonEntity createFirstPerson(PersonDto personDto);

    PersonEntity createParents(PersonDto parentDto, int personID);
    PersonEntity createChildren(PersonDto parentDto, int siblingId);

    PersonEntity createSpouse(PersonDto personDto, int personID);


    InfoAddPersonResponse getInfoPerson(int personId);

    List<PersonEntity> getListChild(int fatherId, int motherId);
    void createPerson(PersonDto personDto);

    void updatePerson(UpdatePersonDto updatePersonDto);

}
