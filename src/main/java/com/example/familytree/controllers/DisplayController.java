package com.example.familytree.controllers;

import com.example.familytree.entities.*;
import com.example.familytree.models.ApiResult;
import com.example.familytree.models.response.PersonDataV2;
import com.example.familytree.models.response.PersonInfoDisplay;
import com.example.familytree.models.response.PersonInfoSimplifiedInfoDis;
import com.example.familytree.repositories.*;
import com.example.familytree.services.FamilyTreeService;
import com.example.familytree.services.LinkSharingService;
import com.example.familytree.shareds.Constants;
import com.example.familytree.utils.BearerTokenUtil;
import com.example.familytree.utils.GetPersonByCenter;
import com.example.familytree.utils.SearchPersonByName;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
public class DisplayController {
    private final PersonRepo personRepo;
    private final SpouseRepo spouseRepo;
    private final FamilyTreeService familyTreeService;
    private final EntityManager em;
    private final LinkSharingRepo linkSharingRepo;
    private final FamilyTreeUserRepo familyTreeUserRepo;
    private final LinkSharingService linkSharingService;
    private final UserAccountRepo userAccountRepo;


    @GetMapping(path = "/test2")
    List<PersonInfoSimplifiedInfoDis> a(@RequestParam int ft,
                                             @RequestParam int pid,
                                             HttpServletRequest request){


        //ft: familytreeid
        //pid: personid
        //CHECK person có trong cây ko
        //check người thực hiện có trong cây ko
        ArrayList<PersonEntity> list =  new ArrayList<>(personRepo.findByFamilyTreeId(ft));
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
        Set<SpouseEntity> set = new LinkedHashSet<>();
        set.addAll(listSpouse);
        listSpouse.clear();
        listSpouse.addAll(set);
        //return GetPersonByCenter.GetPersonByCenterDis(ft, pid, listSpouse, listPerson); //Đầy đủ thông tin ArrayList<PersonInfoDisplay>
        return GetPersonByCenter.getPersonSimplified(ft, pid, listSpouse, listPerson); //Giản lược ArrayList<PersonInfoSimplifiedInfoDis>
    }
    @GetMapping("/test3")
    Map<String, ArrayList<String>> b(){
        Map<String, ArrayList<String>> z= new HashMap<>();
        ArrayList<String> s = new ArrayList<>();
        s.add("TDF");
        s.add("Thai Dang");
        s.add("123123");
        z.put("1", s);
        return z;
    }
    @GetMapping("/test4")
    Map<Integer, PersonDataV2> c(@RequestParam int ft, @RequestParam int pid){
        ArrayList<PersonEntity> list =  new ArrayList<>(personRepo.findByFamilyTreeId(ft));
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
        Set<SpouseEntity> set = new LinkedHashSet<>();
        set.addAll(listSpouse);
        listSpouse.clear();
        listSpouse.addAll(set);
        return GetPersonByCenter.getDataV2(ft, pid, listSpouse, listPerson);
    }
    @GetMapping("/test5")
    List<PersonEntity> p(@RequestParam int ft, @RequestParam int pid, @RequestParam int side){
        return familyTreeService.getListSharingPerson(ft, pid, side)  ;
    }
    @GetMapping("/test6")
    List<SpouseEntity> q(@RequestParam int pid, @RequestParam int side){
        PersonEntity person = personRepo.findFirstByPersonId(pid);

        List<PersonEntity> listPerson = familyTreeService.getListSharingPerson(person.getFamilyTreeId(), pid, side);
        return familyTreeService.getListSharingSpouse(listPerson);
    }
    @GetMapping("/test7")
    List<SpouseEntity> s(){

        em.createNativeQuery("SET IDENTITY_INSERT Person OFF").executeUpdate();

        PersonEntity newPerson = PersonEntity.create(
            100,
                "Người thứ 100",
                false,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                true,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        personRepo.save(newPerson);
//        em.persist(newPerson);
        em.createNativeQuery("SET IDENTITY_INSERT Person ON").executeUpdate();
        return null;
    }
    @GetMapping(path = "/personSearch") //Tìm kiếm
    public List<PersonEntity> searchPerson(@RequestParam int familyTreeId, @RequestParam(defaultValue = "") String keyword){
        ArrayList<PersonEntity> list =  new ArrayList<>(personRepo.findByFamilyTreeId(familyTreeId));
        ArrayList<PersonEntity> listPerson = new ArrayList<>();
        for(PersonEntity p : list){
            if(!p.getPersonIsDeleted()){ //chua xoa
                listPerson.add(p);
            }
        }
        return SearchPersonByName.searchPerson(listPerson, keyword);
    }
    @GetMapping(path = "/getFamilyIdByCode")
    Map<String, Integer> getFamilyIdByCode(@RequestParam(defaultValue = "") String code, HttpServletRequest request){
        LinkSharingEntity linkSharingEntity = linkSharingRepo.findFirstByLink(code);
        Map<String, Integer> res = new HashMap<>();
        if(linkSharingEntity != null && !linkSharingService.isTimeOutRequired(linkSharingEntity, Constants.LINK_SHARING_DURATION)){
            res.put("fid", linkSharingEntity.getFamilyTreeId());
            res.put("pid", linkSharingEntity.getPersonId());
            int fid = linkSharingEntity.getFamilyTreeId();
            int uid = linkSharingEntity.getUserId();
            // ktra người dùng có trong cây k
            String username = BearerTokenUtil.getUserName(request);
            UserAccountEntity userByEmail = userAccountRepo.findFirstByUserEmail(username);
            if(userByEmail == null){
                res.put("UserStatus", -1);
            }else{
                FamilyTreeUserEntity familyTreeUser = familyTreeUserRepo.findFirstByFamilyTreeIdAndUserId(fid, userByEmail.getUserId());
                if(familyTreeUser == null) res.put("UserStatus", -1);
                else if(familyTreeUser.getUserTreeStatus()) res.put("UserStatus", 1);
                else res.put("UserStatus", 0);
            }
        }
        return res;
    }
    @GetMapping(path = "/checkStatusUser")
    Map<String, Integer> checkStatusUser(@RequestParam Integer fid, HttpServletRequest request){
        Map<String, Integer> res = new HashMap<>();
        String username = BearerTokenUtil.getUserName(request);
        UserAccountEntity userByEmail = userAccountRepo.findFirstByUserEmail(username);
        if(userByEmail == null){
            res.put("UserStatus", -1);
        }else{
            FamilyTreeUserEntity familyTreeUser = familyTreeUserRepo.findFirstByFamilyTreeIdAndUserId(fid, userByEmail.getUserId());
            if(familyTreeUser == null) res.put("UserStatus", -1);
            else if(familyTreeUser.getUserTreeStatus()) res.put("UserStatus", 1);
            else res.put("UserStatus", 0);
        }
        return res;
    }

    @GetMapping(path = "/getDataV2ByCode")
    ResponseEntity<ApiResult<?>> getDataV2(@RequestParam(defaultValue = "") String code){
        ApiResult<?> result;
        LinkSharingEntity linkSharingEntity = linkSharingRepo.findFirstByLink(code);
        if (linkSharingEntity == null){
            result = ApiResult.create(HttpStatus.NOT_FOUND, "Code không hợp lệ", null);
            return ResponseEntity.ok(result);
        }
        else{

            result = ApiResult.create(HttpStatus.OK, "Lấy danh sách hiển thị Person thành công!", familyTreeService.getDataV2(linkSharingEntity.getFamilyTreeId(), linkSharingEntity.getPersonId(), 199203));
            return ResponseEntity.ok(result);
        }
    }
}
