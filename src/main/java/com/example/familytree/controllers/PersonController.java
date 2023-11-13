package com.example.familytree.controllers;


import com.example.familytree.entities.PersonEntity;
import com.example.familytree.entities.TreeNodeEntity;
import com.example.familytree.models.ApiResult;
import com.example.familytree.models.dto.PersonDto;
import com.example.familytree.repositories.PersonRepo;
import com.example.familytree.repositories.SpouseRepo;
import com.example.familytree.repositories.TreeNodeRepo;
import com.example.familytree.services.PersonService;
import com.example.familytree.services.SpouseService;
import com.example.familytree.services.TreeNodeService;
import com.example.familytree.shareds.Constants;
import com.example.familytree.utils.PersonUtil;
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


    @GetMapping(path = "/getInfo")
    public ResponseEntity<ApiResult<?>> getInfoPerson(@Valid @RequestParam int personId) {
        ApiResult<?> result = null;
        if (!personRepo.existsByPersonId(personId)) {
            result = ApiResult.create(HttpStatus.NOT_FOUND, MessageFormat.format(Constants.NOT_FOUND_PERSON, personId), null);
        } else {
            result = ApiResult.create(HttpStatus.OK, "Lấy thông tin thành công!", personService.getInfoPerson(personId));
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "/createFirst")
    public ResponseEntity<ApiResult<?>> createFirstPerson(@Valid @RequestBody PersonDto personDto) {
        ApiResult<?> result = null;
        /* Kiểm tra xem cây đó phải chưa có ai mới thêm được */
        if(personRepo.existsByFamilyTreeId(personDto.getFamilyTreeId())){
            result = ApiResult.create(HttpStatus.OK, "Cây đã có Person không thể tạo người đầu tiên nữa!", personDto);
            return ResponseEntity.ok(result);
        }

        // Thêm vào bảng Person
        PersonEntity newPerson = personService.createFirstPerson(personDto);
        // Thêm vào bảng Spouse
        spouseService.createFirstSpouse(newPerson);
        // Thêm vào bảng TreeNode
        TreeNodeEntity treeNode = TreeNodeEntity.create(
                0,
                personDto.getFamilyTreeId(),
                newPerson.getPersonId(),
                1,
                2,
                newPerson.getPersonId()
        );
        treeNodeRepo.save(treeNode);
        result = ApiResult.create(
                HttpStatus.OK,
                "Tạo người Preson đầu tiên trong cây thành công!",
                personDto
        );
        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "/createParents")
    public ResponseEntity<ApiResult<?>> createParents(@Valid @RequestBody PersonDto personDto, @RequestParam int personId) {
        ApiResult<?> result = null;
        PersonEntity personById = personRepo.findFirstByPersonId(personId);
        if (personById == null){
            result = ApiResult.create(HttpStatus.NOT_FOUND, MessageFormat.format(Constants.NOT_FOUND_PERSON, personId), null);
            return ResponseEntity.ok(result);
        }
        // Ktra xem đã có bố mẹ chưa
        if (personDto.getPersonGender() && personById.getFatherId() != null) {
            result = ApiResult.create(HttpStatus.OK, "Đã có bố rồi không thể thêm bố nữa!", null);
            return ResponseEntity.ok(result);
        }
        if (!personDto.getPersonGender() && personById.getMotherId() != null) {
            result = ApiResult.create(HttpStatus.OK, "Đã có mẹ rồi không thể thêm mẹ nữa!", null);
            return ResponseEntity.ok(result);
        }
        PersonEntity newPerson = personService.createParents(personDto, personId);
        result = ApiResult.create(HttpStatus.OK, "Thêm thành công Bố hoặc Mẹ!", newPerson);
        return ResponseEntity.ok(result);
    }
    @PostMapping(path = "/createChildren")
    public ResponseEntity<ApiResult<?>> createChildren(@Valid @RequestBody PersonDto childrenDto, @RequestParam(defaultValue = "0", required = false) int siblingId) {
        ApiResult<?> result = null;

        /* Ktra dữ liệu truyền vào có FatherId hoặc MotherId không */
        if (childrenDto.getFatherId() == null && childrenDto.getMotherId() == null){
            result = ApiResult.create(HttpStatus.BAD_REQUEST, "Khi thêm con phải có một trong 2 trường FatherId hoặc MotherId!", childrenDto);
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
            result = ApiResult.create(HttpStatus.BAD_REQUEST, "FatherID và MotherID không phải vợ chồng!", childrenDto);
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
                result = ApiResult.create(HttpStatus.OK, "Bố hoặc mẹ có con phải chọn ACE khi thêm con!", childrenDto);
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
            if (sibling.getFatherId() != childrenDto.getFatherId()  &&
                    (sibling.getMotherId() != childrenDto.getMotherId() || sibling.getMotherId() == null)){
                result = ApiResult.create(HttpStatus.BAD_REQUEST, "Sibling ID không phải con của FatherID và MotherID!", childrenDto);
                return ResponseEntity.ok(result);
            }

            // ktra số tt ACE có đúng trong khoảng không
            if (childrenDto.getSiblingNum() == null){
                result = ApiResult.create(HttpStatus.OK, "SiblingNum không được để trống!", childrenDto);
                return ResponseEntity.ok(result);
            }
            double siblingNum = childrenDto.getSiblingNum();
            if (siblingNum != (sibling.getSiblingNum() - 0.5) && siblingNum != (sibling.getSiblingNum() + 0.5)){
                result = ApiResult.create(HttpStatus.OK, "SiblingNum phải là giá trị" + sibling.getSiblingNum() +" +- 0.5", childrenDto);
                return ResponseEntity.ok(result);
            }
        }


        //         Gọi service
        PersonEntity newChildren =  personService.createChildren(childrenDto, siblingId);

        result = ApiResult.create(HttpStatus.OK, "Thêm thành công Con!", newChildren);
        return ResponseEntity.ok(result);
    }
}
