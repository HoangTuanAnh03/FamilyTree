package com.example.familytree.services.Impls;

import com.example.familytree.entities.FamilyTreeEntity;
import com.example.familytree.entities.FamilyTreeUserEntity;
import com.example.familytree.repositories.FamilyTreeRepo;
import com.example.familytree.services.FamilyTreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FamilyTreeServiceImpl implements FamilyTreeService {
    private final FamilyTreeRepo familyTreeRepo;

    @Override
    public void create(FamilyTreeEntity familyTree) {
        // Lưu cây mới tạo vào db
        familyTreeRepo.save(familyTree);
        // Thêm người vừa tạo cây vào bảng FamilyTreeUser
//        FamilyTreeEntity currentFamilyTree = familyTreeRepo.findFirstByFamilyTreeId(familyTree.getFamilyTreeId());

    }
}
