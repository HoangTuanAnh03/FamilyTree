package com.example.familytree.services.Impls;

import com.example.familytree.entities.PersonEntity;
import com.example.familytree.entities.SpouseEntity;
import com.example.familytree.repositories.SpouseRepo;
import com.example.familytree.services.SpouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpouseServiceImpl implements SpouseService {
    private final SpouseRepo spouseRepo;

    @Override
    public void createFirstSpouse(PersonEntity person) {
        SpouseEntity newSpouse = SpouseEntity.create(
                0,
                null,
                null,
                0
        );

        if (person.getPersonGender()) {
            newSpouse.setHusbandId(person.getPersonId());
        } else {
            newSpouse.setWifeId(person.getPersonId());
        }
        spouseRepo.save(newSpouse);
    }

    @Override
    public void createFirstSpouse(SpouseEntity spouse, boolean gender) {
        SpouseEntity newSpouse = SpouseEntity.create(
                0,
                null,
                null,
                0
        );

        if (gender) {
            newSpouse.setHusbandId(spouse.getHusbandId());
        } else {
            newSpouse.setWifeId(spouse.getWifeId());
        }
        spouseRepo.save(newSpouse);
    }

    @Override
    public void createSpouse(SpouseEntity spouse) {
        spouseRepo.save(spouse);
    }
}
