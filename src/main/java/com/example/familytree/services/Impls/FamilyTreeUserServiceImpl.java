package com.example.familytree.services.Impls;

import com.example.familytree.entities.FamilyTreeEntity;
import com.example.familytree.entities.FamilyTreeUserEntity;
import com.example.familytree.repositories.FamilyTreeRepo;
import com.example.familytree.services.FamilyTreeService;
import com.example.familytree.services.FamilyTreeUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FamilyTreeUserServiceImpl implements FamilyTreeUserService {
    private final FamilyTreeRepo familyTreeRepo;

    @Override
    public void create(FamilyTreeUserEntity familyTreeUserEntity) {

    }
}
