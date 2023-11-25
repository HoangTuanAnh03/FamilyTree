package com.example.familytree.controllers;

import com.example.familytree.entities.FamilyTreeEntity;
import com.example.familytree.entities.UserAccountEntity;
import com.example.familytree.models.ApiResult;
import com.example.familytree.repositories.FamilyTreeRepo;
import com.example.familytree.repositories.UserAccountRepo;
import com.example.familytree.services.FamilyTreeService;
import com.example.familytree.utils.BearerTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/familyTree")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FamilyTreeController {

    private final UserAccountRepo userAccountRepo;

    private final FamilyTreeService familyTreeService;
    @PostMapping("/create")
    public ResponseEntity<ApiResult<?>> create(HttpServletRequest request, @RequestBody FamilyTreeEntity familyTreeEntity) {
        ApiResult<?> result = null;
        String email = BearerTokenUtil.getUserName(request);

        UserAccountEntity userByEmail = userAccountRepo.findFirstByUserEmail(email);

        if (userByEmail == null || !userByEmail.getUserStatus()) {
            result = ApiResult.create(HttpStatus.BAD_REQUEST, "UserId trong Token không tồn tại hoặc chưa kích hoạt tài khoản!", null);
            return ResponseEntity.ok(result);
        }
        FamilyTreeEntity familyTree = FamilyTreeEntity.create(
                0,
                userByEmail.getUserId(),
                familyTreeEntity.getFamilyTreeName()
        );
        familyTreeService.create(familyTree);
        result = ApiResult.create(HttpStatus.OK, "Tạo thành công Family Tree", familyTree);
        return ResponseEntity.ok(result);
    }


//    @DeleteMapping("/delete")
//    public ResponseEntity<ApiResult<?>> delete(HttpServletRequest request, @RequestParam int id) {
//        ApiResult<?> result = null;
//
//        String email = BearerTokenUtil.getUserName(request);
//
//        return ResponseEntity.ok(result);
//    }
}