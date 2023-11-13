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
public class FamilyTreeServiceImpl implements FamilyTreeService {
    private final FamilyTreeRepo familyTreeRepo;
    private final FamilyTreeUserService familyTreeUserService;

    @Override
    public void create(FamilyTreeEntity familyTree) {
        // Lưu cây mới tạo vào db
        FamilyTreeEntity newFamilyTree = FamilyTreeEntity.create(
                0,
                familyTree.getUserId(),
                familyTree.getFamilyTreeName()
        );
        familyTreeRepo.save(newFamilyTree);
        // Thêm người vừa tạo cây vào bảng FamilyTreeUser
        FamilyTreeUserEntity newFamilyTreeUser = FamilyTreeUserEntity.create(
                0,
                newFamilyTree.getFamilyTreeId(),
                familyTree.getUserId(),
                true
        );
        familyTreeUserService.create(newFamilyTreeUser);
    }
}
