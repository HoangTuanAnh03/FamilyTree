package com.example.familytree.controllers;

import com.example.familytree.entities.PersonEntity;
import com.example.familytree.entities.SpouseEntity;
import com.example.familytree.models.response.PersonDataV2;
import com.example.familytree.models.response.PersonInfoDisplay;
import com.example.familytree.models.response.PersonInfoSimplifiedInfoDis;
import com.example.familytree.repositories.PersonRepo;
import com.example.familytree.repositories.SpouseRepo;
import com.example.familytree.services.FamilyTreeService;
import com.example.familytree.utils.GetPersonByCenter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
public class DisplayController {
    private final PersonRepo personRepo;
    private final SpouseRepo spouseRepo;
    private final FamilyTreeService familyTreeService;
    private final EntityManager em;

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
}
