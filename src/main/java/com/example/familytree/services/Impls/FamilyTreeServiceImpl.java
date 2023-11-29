package com.example.familytree.services.Impls;

import com.example.familytree.entities.*;
import com.example.familytree.models.dto.UserInfo;
import com.example.familytree.models.response.ListTreeResponse;
import com.example.familytree.models.response.InfoUser;
import com.example.familytree.models.response.PersonDataV2;
import com.example.familytree.models.response.PersonInfoSimplifiedInfoDis;
import com.example.familytree.repositories.*;
import com.example.familytree.services.FamilyTreeService;
import com.example.familytree.utils.GetPersonByCenter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@RequiredArgsConstructor
public class FamilyTreeServiceImpl implements FamilyTreeService {
    private final FamilyTreeRepo familyTreeRepo;
    private final PersonRepo personRepo;
    private final SpouseRepo spouseRepo;
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
        for (FamilyTreeUserEntity familyTreeUser : listFamilyTreeUserEntity) {
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

    @Override
    @Transactional
    public FamilyTreeEntity copy(String name, PersonEntity person, int side, UserAccountEntity user) {
        // Lưu cây mới tạo vào db
        FamilyTreeEntity newFamilyTree = FamilyTreeEntity.create(
                0,
                user.getUserId(),
                name,
                person.getPersonId()
        );
        familyTreeRepo.save(newFamilyTree);
        // Thêm người vừa tạo cây vào bảng FamilyTreeUser với vai trò là chủ sở hữu cây
        FamilyTreeUserEntity newFamilyTreeUser = FamilyTreeUserEntity.create(
                0,
                newFamilyTree.getFamilyTreeId(),
                user.getUserId(),
                true,
                3,
                person.getPersonId()
        );
        familyTreeUserRepo.save(newFamilyTreeUser);

        // Copy Person
        List<PersonEntity> listPerson = getListSharingPerson(person.getFamilyTreeId(), person.getPersonId(), side);
        List<SpouseEntity> listSpouse = getListSharingSpouse(listPerson);
//        PersonEntity newPerson = PersonEntity.create(
//
//        );
//        em.createNativeQuery("SET IDENTITY_INSERT Person OFF").executeUpdate();
//        em.persist(newPerson);
//        em.createNativeQuery("SET IDENTITY_INSERT Person ON").executeUpdate();

        // Copy Spouse
        return newFamilyTree;
    }


    @Override
    public List<PersonEntity> getListSharingPerson(int ft, int pid, int side) {
        ArrayList<PersonEntity> list = new ArrayList<>(personRepo.findByFamilyTreeId(ft));
        ArrayList<PersonEntity> listPerson = new ArrayList<>();
        for (PersonEntity p : list) {
            if (!p.getPersonIsDeleted()) { //chua xoa
                listPerson.add(p);
            }
        }
        ArrayList<SpouseEntity> listSpouse = new ArrayList<>();
        for (PersonEntity p : listPerson) {
            int personId = p.getPersonId();
            listSpouse.addAll(spouseRepo.findByHusbandId(personId));
            listSpouse.addAll(spouseRepo.findByWifeId(personId));
        }
        Set<SpouseEntity> set = new LinkedHashSet<>(listSpouse);
        listSpouse.clear();
        listSpouse.addAll(set);
        return GetPersonByCenter.sharingListPerson(ft, pid, listSpouse, listPerson, side); //side = 3: ALL, side = 1: Lấy bên Ngoại, side = 2: Lấy bên nội
    }


    @Override
    public List<SpouseEntity> getListSharingSpouse(List<PersonEntity> listPerson) {
        List<SpouseEntity> listByHusband = new ArrayList<>();
        List<SpouseEntity> listByWife = new ArrayList<>();

        for (PersonEntity person : listPerson) {
            if (person.getPersonGender()) {
                listByHusband.addAll(spouseRepo.findByHusbandId(person.getPersonId()));
            } else {
                listByWife.addAll(spouseRepo.findByWifeId(person.getPersonId()));
            }
        }

        Set<SpouseEntity> resultSet = new HashSet<>(listByHusband);
        resultSet.addAll(listByWife);
        return new ArrayList<>(resultSet);
    }

    @Override
    public List<PersonInfoSimplifiedInfoDis> getPersonSimplified(int pid) {
        //pid: personid
        PersonEntity personById = personRepo.findFirstByPersonId(pid);
        int familyTreeId = personById.getFamilyTreeId();

        ArrayList<PersonEntity> list = new ArrayList<>(personRepo.findByFamilyTreeId(familyTreeId));
        ArrayList<PersonEntity> listPerson = new ArrayList<>();
        for (PersonEntity p : list) {
            if (!p.getPersonIsDeleted()) { //chua xoa
                listPerson.add(p);
            }
        }
        ArrayList<SpouseEntity> listSpouse = new ArrayList<>();
        for (PersonEntity p : listPerson) {
            int personId = p.getPersonId();
            listSpouse.addAll(spouseRepo.findByHusbandId(personId));
            listSpouse.addAll(spouseRepo.findByWifeId(personId));
        }
        Set<SpouseEntity> set = new LinkedHashSet<>(listSpouse);
        listSpouse.clear();
        listSpouse.addAll(set);
//        GetPersonByCenter.GetPersonByCenterDis(ft, pid, listSpouse, listPerson); //Đầy đủ thông tin ArrayList<PersonInfoDisplay>
        return GetPersonByCenter.getPersonSimplified(familyTreeId, pid, listSpouse, listPerson); //Giản lược ArrayList<PersonInfoSimplifiedInfoDis>
    }

    @Override
    public Map<Integer, PersonDataV2> getDataV2(int pid) {
        //pid: personid
        PersonEntity personById = personRepo.findFirstByPersonId(pid);
        int familyTreeId = personById.getFamilyTreeId();

        ArrayList<PersonEntity> list =  new ArrayList<>(personRepo.findByFamilyTreeId(familyTreeId));
        ArrayList<PersonEntity> listPerson = new ArrayList<>();
        for(PersonEntity p : list){
            if(!p.getPersonIsDeleted()){ //chua xoa
                listPerson.add(p);
            }
        }
        ArrayList<SpouseEntity> listSpouse = new ArrayList<>();
        for (PersonEntity p: listPerson) {
            int personId = p.getPersonId();
            listSpouse.addAll(spouseRepo.findByHusbandId(personId));
            listSpouse.addAll(spouseRepo.findByWifeId(personId));
        }
        Set<SpouseEntity> set = new LinkedHashSet<>(listSpouse);
        listSpouse.clear();
        listSpouse.addAll(set);
        return GetPersonByCenter.getDataV2(familyTreeId, pid, listSpouse, listPerson);
    }
}
