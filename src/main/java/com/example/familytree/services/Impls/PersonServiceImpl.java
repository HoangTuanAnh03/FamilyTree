package com.example.familytree.services.Impls;

import com.example.familytree.entities.PersonEntity;
import com.example.familytree.entities.SpouseEntity;
import com.example.familytree.models.dto.PersonDto;
import com.example.familytree.models.response.InfoAddPersonResponse;
import com.example.familytree.repositories.PersonRepo;
import com.example.familytree.repositories.SpouseRepo;
import com.example.familytree.services.PersonService;
import com.example.familytree.shareds.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepo personRepo;
    private final SpouseRepo spouseRepo;

    @Override
    public InfoAddPersonResponse getInfoPerson(int personId) {
        PersonEntity personById = personRepo.findFirstByPersonId(personId);
        List<Integer> wife = new ArrayList<>();
        List<Integer> husband = new ArrayList<>();
        List<Integer> sibling = new ArrayList<>();
        List<Integer> children = new ArrayList<>();

        // Nữ 0 Nam 1
        if(personById.getPersonGender()){
            List<SpouseEntity> listSpouseByHusbandId = spouseRepo.findByHusbandIdAndSpouseStatus(personId, 1);
            for (SpouseEntity spouseEntity : listSpouseByHusbandId)
                wife.add(spouseEntity.getWifeId());
        } else {
            List<SpouseEntity> listSpouseByWifeId = spouseRepo.findByWifeIdAndSpouseStatus(personId, 1);
            for (SpouseEntity spouseEntity : listSpouseByWifeId)
                husband.add(spouseEntity.getHusbandId());
        }
        // Tìm anh chị em cùng parentID trong bảng Person
        if (personById.getParentsId() != null){
            List<PersonEntity> listPersonByParentsId = personRepo.findByParentsId(personById.getParentsId());
            for (PersonEntity personEntity : listPersonByParentsId)
                sibling.add(personEntity.getPersonId());
            // Xoá chính người có personId trong sibling
            sibling.removeIf(child -> child == personId);
        }
        // Tìm các con của mình
        // Phải tìm tất cả các spouseId của mình với mọi status
        // Tìm trong bảng Person với điều kiện parentsId bằng với listSpouseId tìm được bên trên
        List<SpouseEntity> listSpouseByHusbandIdOrWifeId;
        if(personById.getPersonGender()){
            listSpouseByHusbandIdOrWifeId = spouseRepo.findByHusbandId(personId);
        } else {
            listSpouseByHusbandIdOrWifeId = spouseRepo.findByWifeId(personId);
        }
        for (SpouseEntity spouseEntity : listSpouseByHusbandIdOrWifeId){
            List<PersonEntity> listPersonByParentID = personRepo.findByParentsId(spouseEntity.getSpouseId());
            for (PersonEntity personEntity : listPersonByParentID) {
                children.add(personEntity.getPersonId());
            }
        }

        InfoAddPersonResponse infoAddPersonResponse = new InfoAddPersonResponse();
        BeanUtils.copyProperties(personById, infoAddPersonResponse);
        infoAddPersonResponse.setWife(wife);
        infoAddPersonResponse.setHusband(husband);
        infoAddPersonResponse.setSibling(sibling);
        infoAddPersonResponse.setChildren(children);

        return infoAddPersonResponse;
    }

    @Override
    @Transactional
    public void createPerson(PersonDto personDto) {
        PersonEntity newPerson = PersonEntity.create(
                0,
                personDto.getPersonName(),
                personDto.getPersonGender(),
                personDto.getPersonDob(),
                personDto.getPersonJob(),
                personDto.getPersonReligion(),
                personDto.getPersonEthnic(),
                personDto.getPersonDod(),
                personDto.getPersonAddress(),
                personDto.getParentsId(),
                personDto.getFamilyTreeId(),
                personDto.getPersonStatus(),
                personDto.getPersonRank(),
                personDto.getPersonDescription(),
                personDto.getPersonStory(),
                personDto.getFatherId(),
                personDto.getMotherId(),
                false,
                Constants.getCurrentDay(),
                null,
                null,
                personDto.getPersonImage(),
                personDto.getSiblingNum(),
                personDto.getGroupChildId()
        );
        personRepo.save(newPerson);
    }
    @Override
    @Transactional
    public PersonEntity createFirstPerson(PersonDto personDto) {
        PersonEntity person = PersonEntity.create(
                0,
                personDto.getPersonName(),
                personDto.getPersonGender(),
                personDto.getPersonDob(),
                personDto.getPersonJob(),
                personDto.getPersonReligion(),
                personDto.getPersonEthnic(),
                personDto.getPersonDod(),
                personDto.getPersonAddress(),
                null,
                personDto.getFamilyTreeId(),
                personDto.getPersonStatus(),
                0,
                personDto.getPersonDescription(),
                personDto.getPersonStory(),
                null,
                null,
                false,
                Constants.getCurrentDay(),
                null,
                null,
                personDto.getPersonImage(),
                1.0,
                null
        );
        personRepo.save(person);
        person.setGroupChildId(person.getPersonId());
        personRepo.save(person);

        return person;
    }

    @Override
    @Transactional
    public PersonEntity createParents(PersonDto parentDto, int personID) {
        PersonEntity person = personRepo.findFirstByPersonId(personID);
        // Thêm vào bảng Person
        PersonEntity parentEntity =  createFirstPerson(parentDto);
        // Set lại đời rank
        parentEntity.setPersonRank(person.getPersonRank() + 1);
        personRepo.save(parentEntity);

        /* Trường hợp chưa có ai thì tạo Spouse mới bình thường */
        if (person.getFatherId() == null && person.getMotherId() == null && person.getParentsId() == null) {
            SpouseEntity spouseByParent = SpouseEntity.create(
                    0,
                    parentDto.getPersonGender() ? parentEntity.getPersonId() : null,
                    parentDto.getPersonGender() ? null : parentEntity.getPersonId(),
                    0
            );
            spouseRepo.save(spouseByParent);
            /* Update dữ liệu của Con trong bảng Person*/
            person.setParentsId(spouseByParent.getSpouseId());
            if (parentDto.getPersonGender()) {
                person.setFatherId(parentEntity.getPersonId());
            } else {
                person.setMotherId(parentEntity.getPersonId());
            }
            personRepo.save(person);
        }

        /* Trường hợp muốn thêm bố và nhưng đã có mẹ */
        if (parentDto.getPersonGender() && person.getMotherId() != null ) {
            // Tạo Spouse mới
            SpouseEntity newSpouse = SpouseEntity.create(
                    0,
                    parentEntity.getPersonId(),
                    person.getMotherId(),
                    1
            );
            spouseRepo.save(newSpouse);
            // Update dữ liệu của Con trong bảng Person
            person.setFatherId(parentEntity.getPersonId());
            personRepo.save(person);
        }
        /* Trường hợp muốn thêm mẹ và nhưng đã có bố tương tự bên trên */
        if (!parentDto.getPersonGender() && person.getFatherId() != null ) {
            // Tạo Spouse mới
            SpouseEntity newSpouse = SpouseEntity.create(
                    0,
                    person.getFatherId(),
                    parentEntity.getPersonId(),
                    1
            );
            spouseRepo.save(newSpouse);
            // Update dữ liệu của Con trong bảng Person
            person.setMotherId(parentEntity.getPersonId());
            personRepo.save(person);
        }

        return parentEntity;
    }

    @Override
    @Transactional
    public PersonEntity createChildren(PersonDto childrenDto, int siblingId) {
        Integer parentId = null;
        Integer rank = null;
        Integer fatherId = childrenDto.getFatherId();
        Integer motherId = childrenDto.getMotherId();
        double siblingNum = 1.0;
        Integer groupChildId = null;

        /* Cập nhật GroupSiblingId c*/
        PersonEntity siblingBySiblingId = personRepo.findFirstByPersonId(siblingId);
        if (siblingBySiblingId != null)
            groupChildId = siblingBySiblingId.getGroupChildId();

        /* Cập nhật SiblingNum */
        List<PersonEntity> listSibling = personRepo.findByGroupChildId(siblingId);
        if (!listSibling.isEmpty()) {
            for (PersonEntity sibling : listSibling) {
                if (sibling.getSiblingNum() > childrenDto.getSiblingNum()){
                    sibling.setSiblingNum(sibling.getSiblingNum() + 1);
                    personRepo.save(sibling);
                }
            }
            siblingNum = childrenDto.getSiblingNum() + 0.5;
        }


        /* Trường hợp có cả FatherId và MotherId */
        if (fatherId != null && motherId != null){
            SpouseEntity spouseByFatherAndMother = spouseRepo.findFirstByHusbandIdAndWifeId(fatherId, motherId);
            // Thêm dữ liệu vào trường parentId của Children
            parentId = spouseByFatherAndMother.getSpouseId();
            rank = personRepo.findFirstByPersonId(fatherId).getPersonRank() - 1;
        }
        /* Trươờng hợp chỉ có FatherId */
        if (fatherId != null && motherId == null){
            SpouseEntity spouseByFatherAndMother = spouseRepo.findFirstByHusbandIdAndWifeId(fatherId, null);
            // Thêm dữ liệu vào trường parentId của Children
            parentId = spouseByFatherAndMother.getSpouseId();
            rank = personRepo.findFirstByPersonId(fatherId).getPersonRank() - 1;
        }
        /* Trươờng hợp chỉ có MotherId */
        if (fatherId == null && motherId != null){
            SpouseEntity spouseByFatherAndMother = spouseRepo.findFirstByHusbandIdAndWifeId(null, motherId);
            // Thêm dữ liệu vào trường parentId của Children
            parentId = spouseByFatherAndMother.getSpouseId();
            rank = personRepo.findFirstByPersonId(motherId).getPersonRank() - 1;
        }

        // Thêm vào bảng Person
        PersonEntity newChildren =  PersonEntity.create(
                0,
                childrenDto.getPersonName(),
                childrenDto.getPersonGender(),
                childrenDto.getPersonDob(),
                childrenDto.getPersonJob(),
                childrenDto.getPersonReligion(),
                childrenDto.getPersonEthnic(),
                childrenDto.getPersonDod(),
                childrenDto.getPersonAddress(),
                parentId,
                childrenDto.getFamilyTreeId(),
                childrenDto.getPersonStatus(),
                rank,
                childrenDto.getPersonDescription(),
                childrenDto.getPersonStory(),
                fatherId,
                motherId,
                false,
                Constants.getCurrentDay(),
                null,
                null,
                childrenDto.getPersonImage(),
                siblingNum,
                groupChildId
        );

        personRepo.save(newChildren);

        /* Cập nhật lại groupChildId nếu nó là người con đầu tiên */
        if (siblingId == 0) {
            newChildren.setGroupChildId(newChildren.getPersonId());
            personRepo.save(newChildren);
        }

        /* Tạo mới bảng Spouse */
        SpouseEntity newSpouse = SpouseEntity.create(
                0,
                childrenDto.getPersonGender() ? newChildren.getPersonId() : null,
                childrenDto.getPersonGender() ? null : newChildren.getPersonId(),
                0
        );
        spouseRepo.save(newSpouse);

        return null;
    }



}
