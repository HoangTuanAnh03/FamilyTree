package com.example.familytree.services.Impls;

import com.example.familytree.entities.FamilyTreeEntity;
import com.example.familytree.entities.FamilyTreeUserEntity;
import com.example.familytree.entities.UserAccountEntity;
import com.example.familytree.models.dto.UserInfo;
import com.example.familytree.models.response.ListTreeResponse;
import com.example.familytree.models.response.InfoUser;
import com.example.familytree.repositories.FamilyTreeRepo;
import com.example.familytree.repositories.FamilyTreeUserRepo;
import com.example.familytree.repositories.UserAccountRepo;
import com.example.familytree.services.FamilyTreeService;
import com.example.familytree.services.FamilyTreeUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FamilyTreeServiceImpl implements FamilyTreeService {
    private final FamilyTreeRepo familyTreeRepo;
    private final FamilyTreeUserService familyTreeUserService;
    private final FamilyTreeUserRepo familyTreeUserRepo;
    private final UserAccountRepo userAccountRepo;

    @Override
    public ListTreeResponse getListTreeByUserId(int userId) {
        UserAccountEntity user = userAccountRepo.findFirstByUserId(userId);
        UserInfo userInfo = UserInfo.create(
                user.getUserId(),
                user.getUserEmail(),
                user.getUserFullname()
        );
        List<FamilyTreeEntity> owner = familyTreeRepo.findByUserId(user.getUserId());

        List<FamilyTreeEntity> joined = new ArrayList<>();
        List<FamilyTreeUserEntity> listFamilyTreeJoin = familyTreeUserRepo.findByUserIdAndUserTreeStatus(user.getUserId(), true);
        for (FamilyTreeUserEntity familyTreeUser : listFamilyTreeJoin) {
            joined.add(familyTreeRepo.findFirstByFamilyTreeId(familyTreeUser.getFamilyTreeId()));
        }

        List<FamilyTreeUserEntity> listFamilyTreeRequest = familyTreeUserRepo.findByUserIdAndUserTreeStatus(user.getUserId(), false);
        List<FamilyTreeEntity> requestJoin = new ArrayList<>();
        for (FamilyTreeUserEntity familyTreeUser : listFamilyTreeRequest) {
            requestJoin.add(familyTreeRepo.findFirstByFamilyTreeId(familyTreeUser.getFamilyTreeId()));
        }

        return ListTreeResponse.create(
                userInfo,
                owner,
                joined,
                requestJoin
        );
    }

    @Override
    public List<InfoUser> getListUserByFamilyTreeId(int familyTreeId, int userId) {
        List<InfoUser> result = new ArrayList<>();
        List<FamilyTreeUserEntity> listFamilyTreeUserEntity = familyTreeUserRepo.findByFamilyTreeId(familyTreeId);
        for (FamilyTreeUserEntity familyTreeUser : listFamilyTreeUserEntity){
            UserAccountEntity user = userAccountRepo.findFirstByUserId(familyTreeUser.getUserId());
            InfoUser infoUser = InfoUser.create(
                    user.getUserId(),
                    user.getUserEmail(),
                    user.getUserId() == userId ? "Tôi" : user.getUserFullname(),
                    familyTreeUser.getRoleId(),
                    familyTreeUser.getUserTreeStatus(),
                    familyTreeUser.getPersonId() == null ? 0 : familyTreeUser.getPersonId(),
                    familyTreeUser.getFamilyTreeId()
            );
            result.add(infoUser);
        }

        return result;
    }

    @Override
    @Transactional
    public FamilyTreeEntity create(FamilyTreeEntity familyTree) {
        // Lưu cây mới tạo vào db
        FamilyTreeEntity newFamilyTree = FamilyTreeEntity.create(
                0,
                familyTree.getUserId(),
                familyTree.getFamilyTreeName(),
                null
        );
        familyTreeRepo.save(newFamilyTree);
        // Thêm người vừa tạo cây vào bảng FamilyTreeUser với vai trò là chủ sở hữu cây
        FamilyTreeUserEntity newFamilyTreeUser = FamilyTreeUserEntity.create(
                0,
                newFamilyTree.getFamilyTreeId(),
                familyTree.getUserId(),
                true,
                3,
                null
        );
        familyTreeUserRepo.save(newFamilyTreeUser);
        return newFamilyTree;
    }
}
