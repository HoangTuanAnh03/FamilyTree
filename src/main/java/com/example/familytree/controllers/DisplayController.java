package com.example.familytree.controllers;

import com.example.familytree.entities.PersonEntity;
import com.example.familytree.entities.SpouseEntity;
import com.example.familytree.models.response.PersonDataV2;
import com.example.familytree.models.response.PersonInfoDisplay;
import com.example.familytree.models.response.PersonInfoSimplifiedInfoDis;
import com.example.familytree.repositories.PersonRepo;
import com.example.familytree.repositories.SpouseRepo;
import com.example.familytree.services.FamilyTreeService;
import com.example.familytree.shareds.Constants;
import com.example.familytree.utils.GetPersonByCenter;
import jakarta.persistence.Convert;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.sql.PreparedStatement;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
public class DisplayController {
    private final PersonRepo personRepo;
    private final SpouseRepo spouseRepo;
    private final FamilyTreeService familyTreeService;
    private final JdbcTemplate jdbcTemplate;

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
//        String jqlON = "SET IDENTITY_INSERT Person ON";
//        jdbcTemplate.execute(jqlON);
//        String sql = "INSERT INTO person(person_id, person_name, person_gender, person_DOB, person_job, person_religion, person_ethnic, person_DOD, person_address, parents_id, family_tree_id, person_status, person_rank, person_description, person_story, father_id, mother_id, person_is_deleted, person_created_at, person_updated_at, person_deleted_at, person_image, sibling_num, group_child_id ) " +
//                    "values(100, 'Nguoi 100', 1, 8, 1)";
//        jdbcTemplate.execute(sql);
//        String jqlOFF = "SET IDENTITY_INSERT Person OFF";
//        jdbcTemplate.execute(jqlOFF);
        PersonEntity person = personRepo.findFirstByPersonId(20);
        int range = 21;

        String jqlON = "SET IDENTITY_INSERT Person ON";
        jdbcTemplate.execute(jqlON);
        int personId = person.getPersonId() + range;
        int groupChild = person.getGroupChildId() + range;
        int gender = person.getPersonGender() ? 1 : 0;



        String sql = "INSERT INTO person(person_id, person_name, person_gender, person_DOB, person_job, person_religion, person_ethnic, person_DOD, person_address, family_tree_id, person_status, person_rank, person_description, person_story, person_is_deleted, person_image, sibling_num, group_child_id) " +
                "VALUES(" + personId + ",N'" + person.getPersonName() + "'," + gender + ",'" + person.getPersonDob() + "',N'" + person.getPersonJob() + "',N'" + person.getPersonReligion() + "',N'" + person.getPersonEthnic() + "','" + person.getPersonDod() + "',N'" + person.getPersonAddress() + "'," + 7 + "," + 1 + "," + person.getPersonRank() + ",N'" + person.getPersonDescription() + "',N'" + person.getPersonStory() + "'," + 0 + ",'" +  person.getPersonImage() + "'," + person.getSiblingNum() + "," + groupChild + ")";
        jdbcTemplate.execute(sql);
        String jqlOFF = "SET IDENTITY_INSERT Person OFF";
        jdbcTemplate.execute(jqlOFF);


        PersonEntity personCurrent = personRepo.findFirstByPersonId(personId);
        if (person.getFatherId() != null){
            personCurrent.setFatherId(person.getFatherId() + range);
        }
        if (person.getMotherId() != null){
            personCurrent.setMotherId(person.getMotherId() + range);
        }
        personRepo.save(personCurrent);


        return null;
    }
}
