package com.example.familytree.services.Impls;

import com.example.familytree.advice.exceptions.DuplicateRecordException;
import com.example.familytree.entities.FamilyTreeEntity;
import com.example.familytree.entities.FamilyTreeUserEntity;
import com.example.familytree.repositories.FamilyTreeRepo;
import com.example.familytree.repositories.FamilyTreeUserRepo;
import com.example.familytree.services.FamilyTreeService;
import com.example.familytree.services.FamilyTreeUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FamilyTreeUserServiceImpl implements FamilyTreeUserService {
    private final FamilyTreeUserRepo familyTreeUserRepo;

    @Override
    public void create(FamilyTreeUserEntity familyTreeUserEntity) {
        int familyTreeId = familyTreeUserEntity.getFamilyTreeId();
        int userId = familyTreeUserEntity.getUserId();
        if (familyTreeUserRepo.existsByFamilyTreeIdAndUserId(familyTreeId, userId))
            throw new DuplicateRecordException("User muốn thêm đã ở trong cây rồi");

        FamilyTreeUserEntity newUser = FamilyTreeUserEntity.create(
                0,
                familyTreeId,
                userId,
                familyTreeUserEntity.getUserTreeStatus()
        );
        familyTreeUserRepo.save(newUser);
    }
}
