package com.example.familytree.controllers;


import com.example.familytree.entities.PersonEntity;
import com.example.familytree.entities.SpouseEntity;
import com.example.familytree.entities.TreeNodeEntity;
import com.example.familytree.entities.UserAccountEntity;
import com.example.familytree.models.ApiResult;
import com.example.familytree.models.dto.PersonDto;
import com.example.familytree.models.dto.UpdatePersonDto;
import com.example.familytree.repositories.*;
import com.example.familytree.services.PersonService;
import com.example.familytree.services.SpouseService;
import com.example.familytree.services.TreeNodeService;
import com.example.familytree.shareds.Constants;
import com.example.familytree.utils.BearerTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.List;

@RestController
@RequestMapping(path = "/person")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PersonController {
    private final PersonService personService;
    private final SpouseService spouseService;
    private final TreeNodeService treeNodeService;
    private final PersonRepo personRepo;
    private final SpouseRepo spouseRepo;
    private final TreeNodeRepo treeNodeRepo;
    private final UserAccountRepo userAccountRepo;
    private final FamilyTreeUserRepo familyTreeUserRepo;



    @GetMapping(path = "/getInfo")
    public ResponseEntity<ApiResult<?>> getInfoPerson(@Valid @RequestParam int personId, HttpServletRequest request) {
        ApiResult<?> result;
        // ktra người dùng có trong cây k
        PersonEntity personByPersonId = personRepo.findFirstByPersonId(personId);
        if (personByPersonId == null){
            result = ApiResult.create(HttpStatus.NOT_FOUND, MessageFormat.format(Constants.NOT_FOUND_PERSON, personId), null);
            return ResponseEntity.ok(result);
        }
        int familyTreeIdByPerson = personByPersonId.getFamilyTreeId();
        String username = BearerTokenUtil.getUserName(request);
        UserAccountEntity userByEmail = userAccountRepo.findFirstByUserEmail(username);

        if (!familyTreeUserRepo.existsByFamilyTreeIdAndUserId(familyTreeIdByPerson, userByEmail.getUserId())) {
            result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.USER_DOES_NOT_EXITS_IN_TREE, userByEmail.getUserId(), personId), null);
            return ResponseEntity.ok(result);
        }

        result = ApiResult.create(HttpStatus.OK, Constants.GET_INFO_PERSON_SUCCESS, personService.getInfoPerson(personId));
        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "/createFirst")
    public ResponseEntity<ApiResult<?>> createFirstPerson(@Valid @RequestBody PersonDto personDto, HttpServletRequest request) {
        ApiResult<?> result;
        // ktra người dùng có trong cây k
        String username = BearerTokenUtil.getUserName(request);
        UserAccountEntity userByEmail = userAccountRepo.findFirstByUserEmail(username);

        if (!familyTreeUserRepo.existsByFamilyTreeIdAndUserId(personDto.getFamilyTreeId(), userByEmail.getUserId())) {
            result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.USER_DOES_NOT_EXITS_IN_TREE, userByEmail.getUserId()), null);
            return ResponseEntity.ok(result);
        }

        /* Kiểm tra xem cây đó phải chưa có ai mới thêm được */
        if(personRepo.existsByFamilyTreeId(personDto.getFamilyTreeId())){
            result = ApiResult.create(HttpStatus.BAD_REQUEST, "Cây đã có Person không thể tạo người đầu tiên nữa!", personDto);
            return ResponseEntity.ok(result);
        }

        // Thêm vào bảng Person
        PersonEntity newPerson = personService.createFirstPerson(personDto);
        // Thêm vào bảng Spouse
        SpouseEntity spouseByParent = SpouseEntity.create(
                0,
                personDto.getPersonGender() ? newPerson.getPersonId() : null,
                personDto.getPersonGender() ? null : newPerson.getPersonId(),
                0
        );
        spouseRepo.save(spouseByParent);
        result = ApiResult.create(
                HttpStatus.OK,
                "Tạo người Preson đầu tiên trong cây thành công!",
                personDto
        );
        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "/createParents")
    public ResponseEntity<ApiResult<?>> createParents(@Valid @RequestBody PersonDto personDto, @RequestParam int personId, HttpServletRequest request) {
        ApiResult<?> result;
        // ktra người dùng có trong cây k
        String username = BearerTokenUtil.getUserName(request);
        UserAccountEntity userByEmail = userAccountRepo.findFirstByUserEmail(username);

        if (!familyTreeUserRepo.existsByFamilyTreeIdAndUserId(personDto.getFamilyTreeId(), userByEmail.getUserId())) {
            result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.USER_DOES_NOT_EXITS_IN_TREE, userByEmail.getUserId(), personId), null);
            return ResponseEntity.ok(result);
        }

        PersonEntity personById = personRepo.findFirstByPersonId(personId);
        if (personById == null){
            result = ApiResult.create(HttpStatus.NOT_FOUND, MessageFormat.format(Constants.NOT_FOUND_PERSON, personId), null);
            return ResponseEntity.ok(result);
        }
        // Ktra xem đã có bố mẹ chưa
        if (personDto.getPersonGender() && personById.getFatherId() != null) {
            result = ApiResult.create(HttpStatus.BAD_REQUEST, Constants.NOT_ADD_FATHER, null);
            return ResponseEntity.ok(result);
        }
        if (!personDto.getPersonGender() && personById.getMotherId() != null) {
            result = ApiResult.create(HttpStatus.BAD_REQUEST, Constants.NOT_ADD_MOTHER, null);
            return ResponseEntity.ok(result);
        }
        PersonEntity newPerson = personService.createParents(personDto, personId);
        result = ApiResult.create(HttpStatus.OK, Constants.ADD_PARENTS_SUCCESS, newPerson);
        return ResponseEntity.ok(result);
    }
    @PostMapping(path = "/createChildren")
    public ResponseEntity<ApiResult<?>> createChildren(@Valid @RequestBody PersonDto childrenDto, @RequestParam(defaultValue = "0", required = false) int siblingId, HttpServletRequest request) {
        ApiResult<?> result;
        // ktra người dùng có trong cây k
        String username = BearerTokenUtil.getUserName(request);
        UserAccountEntity userByEmail = userAccountRepo.findFirstByUserEmail(username);

        if (!familyTreeUserRepo.existsByFamilyTreeIdAndUserId(childrenDto.getFamilyTreeId(), userByEmail.getUserId())) {
            result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.USER_DOES_NOT_EXITS_IN_TREE, userByEmail.getUserId(), siblingId), null);
            return ResponseEntity.ok(result);
        }

        /* Ktra dữ liệu truyền vào có FatherId hoặc MotherId không */
        if (childrenDto.getFatherId() == null && childrenDto.getMotherId() == null){
            result = ApiResult.create(HttpStatus.BAD_REQUEST, Constants.FATHERID_OR_MOTHERID_NOT_NULL, childrenDto);
            return ResponseEntity.ok(result);
        }
        /* Ktra tính hợp lệ của FatherId hoặc MotherId */
        if (childrenDto.getFatherId() != null && !personRepo.existsByPersonIdAndPersonGender(childrenDto.getFatherId(), true)){
            result = ApiResult.create(HttpStatus.NOT_FOUND, MessageFormat.format(Constants.NOT_FOUND_FATHER, childrenDto.getFatherId()), childrenDto);
            return ResponseEntity.ok(result);
        }
        if (childrenDto.getMotherId() != null && !personRepo.existsByPersonIdAndPersonGender(childrenDto.getMotherId(), false)){
            result = ApiResult.create(HttpStatus.NOT_FOUND, MessageFormat.format(Constants.NOT_FOUND_MOTHER, childrenDto.getMotherId()), childrenDto);
            return ResponseEntity.ok(result);
        }
        // ktra có đúng là vợ chồng không
        if (childrenDto.getFatherId() != null && childrenDto.getMotherId() != null
                && !spouseRepo.existsByHusbandIdAndWifeId(childrenDto.getFatherId(), childrenDto.getMotherId())
        ){
            result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.NOT_HUSBAND_AND_WIFE, childrenDto.getFatherId(), childrenDto.getMotherId()), childrenDto);
            return ResponseEntity.ok(result);
        }
        // ktra xem có đúng là không có ace không
        if (siblingId == 0 ) {
            List<PersonEntity> listChild = null;
            if (childrenDto.getFatherId() != null && childrenDto.getMotherId() == null)
                listChild = personRepo.findByFatherId(childrenDto.getFatherId());
            else if (childrenDto.getFatherId() == null && childrenDto.getMotherId() != null)
                listChild = personRepo.findByMotherId(childrenDto.getMotherId());
            else
                listChild = personRepo.findByFatherIdAndMotherId(childrenDto.getFatherId(), childrenDto.getMotherId());
                // fatherid và motherid không thể null khi đi đến đây

            if (!listChild.isEmpty()){
                result = ApiResult.create(HttpStatus.BAD_REQUEST, "Bố hoặc mẹ có con phải chọn ACE khi thêm con!", childrenDto);
                return ResponseEntity.ok(result);
            }
        }
        // ktra có đúng con của vợ chồng đó không
        if (siblingId != 0){
            PersonEntity sibling = personRepo.findFirstByPersonId(siblingId);
            if (sibling == null){
                result = ApiResult.create(HttpStatus.NOT_FOUND, MessageFormat.format(Constants.NOT_FOUND_PERSON, siblingId), childrenDto);
                return ResponseEntity.ok(result);
            }
            if (sibling.getFatherId() != childrenDto.getFatherId()  && sibling.getMotherId() != childrenDto.getMotherId()){
                result = ApiResult.create(HttpStatus.BAD_REQUEST, Constants.SIBLING_NOT_A_CHILD, childrenDto);
                return ResponseEntity.ok(result);
            }

            // ktra số tt ACE có đúng trong khoảng không
            if (childrenDto.getSiblingNum() == null){
                result = ApiResult.create(HttpStatus.BAD_REQUEST, Constants.SIBLING_NOT_NULL, childrenDto);
                return ResponseEntity.ok(result);
            }
            double siblingNum = childrenDto.getSiblingNum();
            if (siblingNum != (sibling.getSiblingNum() - 0.5) && siblingNum != (sibling.getSiblingNum() + 0.5)){
                result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.RANGE_VALUE_SIBLINGID, sibling.getSiblingNum()), childrenDto);
                return ResponseEntity.ok(result);
            }
        }


        //         Gọi service
        PersonEntity newChildren =  personService.createChildren(childrenDto, siblingId);

        result = ApiResult.create(HttpStatus.OK, Constants.ADD_CHILD_SUCCESS, newChildren);
        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "/createSpouse")
    public ResponseEntity<ApiResult<?>> createSpouse(@Valid @RequestBody PersonDto personDto, @RequestParam int personId, HttpServletRequest request) {
        ApiResult<?> result = null;

        // ktra người dùng có trong cây k
        String username = BearerTokenUtil.getUserName(request);
        UserAccountEntity userByEmail = userAccountRepo.findFirstByUserEmail(username);

        if (!familyTreeUserRepo.existsByFamilyTreeIdAndUserId(personDto.getFamilyTreeId(), userByEmail.getUserId())) {
            result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.USER_DOES_NOT_EXITS_IN_TREE, userByEmail.getUserId(), personId), null);
            return ResponseEntity.ok(result);
        }

        PersonEntity personById = personRepo.findFirstByPersonId(personId);
        if (personById == null){
            result = ApiResult.create(HttpStatus.NOT_FOUND, MessageFormat.format(Constants.NOT_FOUND_PERSON, personId), null);
            return ResponseEntity.ok(result);
        }

        if (personById.getPersonGender() == personDto.getPersonGender()){
            result = ApiResult.create(HttpStatus.BAD_REQUEST, "Hai người cùng giới tính không thể kết hôn!", null);
            return ResponseEntity.ok(result);
        }

        personService.createSpouse(personDto, personId);

        result = ApiResult.create(HttpStatus.OK, "Thêm thành công vợ hoặc chồng", null);
        return ResponseEntity.ok(result);
    }
    @PutMapping(path = "/update")
    public ResponseEntity<ApiResult<?>> updatePerson(@Valid @RequestBody UpdatePersonDto newPerson, HttpServletRequest request){
        ApiResult<?> result = null;

        PersonEntity personById = personRepo.findFirstByPersonId(newPerson.getPersonId());
        if (personById == null){
            result = ApiResult.create(HttpStatus.NOT_FOUND, MessageFormat.format(Constants.NOT_FOUND_PERSON, newPerson.getPersonId()), newPerson);
            return ResponseEntity.ok(result);
        }

        // ktra người dùng có trong cây k
        String username = BearerTokenUtil.getUserName(request);
        UserAccountEntity userByEmail = userAccountRepo.findFirstByUserEmail(username);

        if (!familyTreeUserRepo.existsByFamilyTreeIdAndUserId(personById.getFamilyTreeId(), userByEmail.getUserId())) {
            result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.USER_DOES_NOT_EXITS_IN_TREE, userByEmail.getUserId(), newPerson.getPersonId()), null);
            return ResponseEntity.ok(result);
        }

        // Chưa có bố mẹ thì phải thêm rồi mới sửa
        if ((newPerson.getFatherId() != null || newPerson.getMotherId() != null)
            && (personById.getFatherId() == null && personById.getMotherId() == null)){
            result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.NO_PARENTS, newPerson.getPersonId()), newPerson);
            return ResponseEntity.ok(result);
        }
        if (newPerson.getFatherId() != null && personById.getFatherId() != null && newPerson.getFatherId() != personById.getFatherId()){
            result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.NOT_EDIT_FATHER, newPerson.getPersonId(), personById.getFatherId()), newPerson);
            return ResponseEntity.ok(result);
        }
        if (newPerson.getMotherId() != null && personById.getMotherId() != null && newPerson.getMotherId() != personById.getMotherId()){
            result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.NOT_EDIT_MOTHER, newPerson.getPersonId(), personById.getMotherId()), newPerson);
            return ResponseEntity.ok(result);
        }
        // Ktra xem bố hoặc mẹ được thêm vào phải là người mẹ kế, bố kế đã tồn tại trong cây
        if (newPerson.getMotherId() != null && personById.getMotherId() == null) {
            PersonEntity personByMotherId = personRepo.findFirstByPersonId(newPerson.getMotherId());
            if (personByMotherId == null){
                result = ApiResult.create(HttpStatus.NOT_FOUND, MessageFormat.format(Constants.NOT_FOUND_PERSON, newPerson.getMotherId()), newPerson);
                return ResponseEntity.ok(result);
            }
            if (!spouseRepo.existsByHusbandIdAndWifeId(personById.getFatherId(), newPerson.getMotherId())){
                result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.NOT_HUSBAND_AND_WIFE, personById.getFatherId(), newPerson.getMotherId()), newPerson);
                return ResponseEntity.ok(result);
            }
        }
        if (newPerson.getFatherId() != null && personById.getFatherId() == null) {
            PersonEntity personByFatherId = personRepo.findFirstByPersonId(newPerson.getFatherId());
            if (personByFatherId == null){
                result = ApiResult.create(HttpStatus.NOT_FOUND, MessageFormat.format(Constants.NOT_FOUND_PERSON, newPerson.getFatherId()), newPerson);
                return ResponseEntity.ok(result);
            }
            if (!spouseRepo.existsByHusbandIdAndWifeId(personById.getFatherId(), newPerson.getMotherId())){
                result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.NOT_HUSBAND_AND_WIFE, personById.getFatherId(), newPerson.getMotherId()), newPerson);
                return ResponseEntity.ok(result);
            }
        }

        // ktra có đúng con của vợ chồng đó không
        if (newPerson.getSiblingId() != null){
            PersonEntity sibling = personRepo.findFirstByPersonId(newPerson.getSiblingId());
            if (sibling == null){
                result = ApiResult.create(HttpStatus.NOT_FOUND, MessageFormat.format(Constants.NOT_FOUND_PERSON, newPerson.getSiblingId()), newPerson);
                return ResponseEntity.ok(result);
            }
            if ((sibling.getFatherId() != newPerson.getFatherId() && newPerson.getFatherId() != null)
                    && (sibling.getMotherId() != newPerson.getMotherId() && newPerson.getMotherId() != null)){
                result = ApiResult.create(HttpStatus.BAD_REQUEST, Constants.SIBLING_NOT_A_CHILD, newPerson);
                return ResponseEntity.ok(result);
            }

            // ktra số tt ACE có đúng trong khoảng không
            if (newPerson.getSiblingNum() == null){
                result = ApiResult.create(HttpStatus.BAD_REQUEST, Constants.SIBLING_NOT_NULL, newPerson);
                return ResponseEntity.ok(result);
            }
            double siblingNum = newPerson.getSiblingNum();
            if (siblingNum != (sibling.getSiblingNum() - 0.5) && siblingNum != (sibling.getSiblingNum() + 0.5)){
                result = ApiResult.create(HttpStatus.BAD_REQUEST, Constants.RANGE_VALUE_SIBLINGID, newPerson);
                return ResponseEntity.ok(result);
            }
        }
        personService.updatePerson(newPerson);
        result = ApiResult.create(HttpStatus.OK, Constants.UPDATE_PERSON_SUCCESS, newPerson);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping(path = "/delete")
    public ResponseEntity<ApiResult<?>> deletePerson(@RequestParam int personId, HttpServletRequest request) {
        ApiResult<?> result = null;
        // ktra người dùng có trong cây k
        PersonEntity personByPersonId = personRepo.findFirstByPersonId(personId);
        if (personByPersonId == null){
            result = ApiResult.create(HttpStatus.NOT_FOUND, MessageFormat.format(Constants.NOT_FOUND_PERSON, personId), null);
            return ResponseEntity.ok(result);
        }
        int familyTreeIdByPerson = personByPersonId.getFamilyTreeId();
        String username = BearerTokenUtil.getUserName(request);
        UserAccountEntity userByEmail = userAccountRepo.findFirstByUserEmail(username);

        if (!familyTreeUserRepo.existsByFamilyTreeIdAndUserId(familyTreeIdByPerson, userByEmail.getUserId())) {
            result = ApiResult.create(HttpStatus.BAD_REQUEST, MessageFormat.format(Constants.USER_DOES_NOT_EXITS_IN_TREE, userByEmail.getUserId(), personId), null);
            return ResponseEntity.ok(result);
        }

        // service
        personByPersonId.setPersonIsDeleted(true);
        personByPersonId.setPersonDeletedAt(Constants.getCurrentDay());
        personRepo.save(personByPersonId);


        result = ApiResult.create(HttpStatus.OK, "Xoá thành công Person", null);
        return ResponseEntity.ok(result);
    }
}
