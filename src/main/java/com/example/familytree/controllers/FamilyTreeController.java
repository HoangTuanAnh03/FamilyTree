package com.example.familytree.controllers;

import com.example.familytree.entities.FamilyTreeEntity;
import com.example.familytree.entities.FamilyTreeUserEntity;
import com.example.familytree.entities.PersonEntity;
import com.example.familytree.entities.UserAccountEntity;
import com.example.familytree.models.ApiResult;
import com.example.familytree.models.dto.UserInfo;
import com.example.familytree.models.response.ListTreeResponse;
import com.example.familytree.repositories.FamilyTreeRepo;
import com.example.familytree.repositories.FamilyTreeUserRepo;
import com.example.familytree.repositories.UserAccountRepo;
import com.example.familytree.services.FamilyTreeService;
import com.example.familytree.shareds.Constants;
import com.example.familytree.utils.BearerTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/familyTree")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FamilyTreeController {

    private final UserAccountRepo userAccountRepo;
    private final FamilyTreeUserRepo familyTreeUserRepo;
    private final FamilyTreeRepo familyTreeRepo;
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
                familyTreeEntity.getFamilyTreeName(),
                null
        );
        FamilyTreeEntity newFamilyTree =  familyTreeService.create(familyTree);
        result = ApiResult.create(HttpStatus.OK, "Tạo thành công Family Tree", newFamilyTree);
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


    @GetMapping("/list")
    public ResponseEntity<ApiResult<?>> list(HttpServletRequest request) {
        ApiResult<?> result = null;

        String email = BearerTokenUtil.getUserName(request);
        UserAccountEntity userByEmail = userAccountRepo.findFirstByUserEmail(email);

        result = ApiResult.create(HttpStatus.OK, "lấy thành công danh sách cây của người dùng!", familyTreeService.getListTreeByUserId(userByEmail.getUserId()));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/listUser")
    public ResponseEntity<ApiResult<?>> listUser(@RequestParam int familyTreeId, HttpServletRequest request) {
        ApiResult<?> result = null;

        String email = BearerTokenUtil.getUserName(request);
        UserAccountEntity userByToken = userAccountRepo.findFirstByUserEmail(email);
        // Cây có tồn tại
        if (!familyTreeRepo.existsByFamilyTreeId(familyTreeId)) {
            result = ApiResult.create(HttpStatus.BAD_REQUEST, "Không tồn tại cây!", null);
            return ResponseEntity.ok(result);
        }

        // userBytoken có tồn tại trong cây không
        if (!familyTreeUserRepo.existsByFamilyTreeIdAndUserIdAndUserTreeStatus(familyTreeId, userByToken.getUserId(), true)) {
            result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.USER_DOES_NOT_EXITS_IN_TREE_ID, familyTreeId, userByToken.getUserId()), null);
            return ResponseEntity.ok(result);
        }

        result = ApiResult.create(HttpStatus.OK, "Lấy thành công User trong cây!", familyTreeService.getListUserByFamilyTreeId(familyTreeId, userByToken.getUserId()));
        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "/role")
    public ResponseEntity<ApiResult<?>> upRole(@RequestParam int familyTreeId, @RequestParam int userId, @RequestParam String action, HttpServletRequest request){
        ApiResult<?> result = null;

        String email = BearerTokenUtil.getUserName(request);
        UserAccountEntity userByToken = userAccountRepo.findFirstByUserEmail(email);
        FamilyTreeUserEntity familyTreeUserByToken = familyTreeUserRepo.findFirstByFamilyTreeIdAndUserId(familyTreeId, userByToken.getUserId());
        // userBytoken có tồn tại trong cây không
        if (familyTreeUserByToken == null) {
            result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.USER_DOES_NOT_EXITS_IN_TREE_ID, familyTreeId, userByToken.getUserId()), null);
            return ResponseEntity.ok(result);
        }

        // Tiính hợp lệ giữa familyId và user
        FamilyTreeUserEntity familyTreeUser = familyTreeUserRepo.findFirstByFamilyTreeIdAndUserId(familyTreeId, userId);
        if (familyTreeUser == null) {
            result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.USER_DOES_NOT_EXITS_IN_TREE_ID, familyTreeId, userId), null);
            return ResponseEntity.ok(result);
        }


        switch (action) {
            case "upRole" -> {
                // Chỉ có Role = 3 mới được thêm hoặc xoá quyền
                if (familyTreeUserByToken.getRoleId() != 3) {
                    result = ApiResult.create(HttpStatus.BAD_REQUEST, "User phải có roleId = 3 mới được thêm hoặc huỷ quyền!", null);
                    return ResponseEntity.ok(result);
                }
                if (familyTreeUser.getRoleId() != 1) {
                    result = ApiResult.create(HttpStatus.BAD_REQUEST, "Không thể thêm quyền với người có RoleId = 2 và 3!", null);
                    return ResponseEntity.ok(result);
                }
                // service
                familyTreeUser.setRoleId(2);
                familyTreeUserRepo.save(familyTreeUser);
                result = ApiResult.create(HttpStatus.OK, "Thêm quyền thành công!", familyTreeUser);
                return ResponseEntity.ok(result);
            }
            case "downRole" -> {
                // Chỉ có Role = 3 mới được thêm hoặc xoá quyền
                if (familyTreeUserByToken.getRoleId() != 3) {
                    result = ApiResult.create(HttpStatus.BAD_REQUEST, "User phải có roleId = 3 mới được thêm hoặc huỷ quyền!", null);
                    return ResponseEntity.ok(result);
                }
                if (familyTreeUser.getRoleId() != 2) {
                    result = ApiResult.create(HttpStatus.BAD_REQUEST, "Không thể huỷ quyền với người có RoleId = 1 và 3!", null);
                    return ResponseEntity.ok(result);
                }
                // service
                familyTreeUser.setRoleId(1);
                familyTreeUserRepo.save(familyTreeUser);
                result = ApiResult.create(HttpStatus.OK, "Huỷ quyền thành công!", familyTreeUser);
                return ResponseEntity.ok(result);
            }
            case "kick" -> {
                // Chỉ có Role = 2 và 3 mới được kick
                if (familyTreeUserByToken.getRoleId() == 1) {
                    result = ApiResult.create(HttpStatus.BAD_REQUEST, "Bạn là chỉ là thành viên không có quyền kick!", null);
                    return ResponseEntity.ok(result);
                }
                if (familyTreeUserByToken.getRoleId() <= familyTreeUser.getRoleId()){
                    result = ApiResult.create(HttpStatus.BAD_REQUEST, "Bạn không thể kick User có Role bằng hoặc lớn hơn mình!", null);
                    return ResponseEntity.ok(result);
                }

                familyTreeUserRepo.delete(familyTreeUser);
                result = ApiResult.create(HttpStatus.OK, "Xoá thành công User ra khỏi cây!", familyTreeUser);
                return ResponseEntity.ok(result);
            }
            default -> {
                result = ApiResult.create(HttpStatus.BAD_REQUEST, "Action không hợp lệ!", null);
                return ResponseEntity.ok(result);
            }
        }
    }
}
