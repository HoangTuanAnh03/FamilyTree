package com.example.familytree.controllers;

import com.example.familytree.entities.PersonEntity;
import com.example.familytree.entities.SpouseEntity;
import com.example.familytree.models.response.PersonDataV2;
import com.example.familytree.models.response.PersonInfoDisplay;
import com.example.familytree.models.response.PersonInfoSimplifiedInfoDis;
import com.example.familytree.repositories.PersonRepo;
import com.example.familytree.repositories.SpouseRepo;
import com.example.familytree.utils.GetPersonByCenter;
import com.example.familytree.utils.SearchPersonByName;
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
    @GetMapping(path = "/test2")
    ArrayList<PersonInfoDisplay> a(@RequestParam int ft,
                                             @RequestParam int pid){
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
        return GetPersonByCenter.getPersonSimplified2(ft, pid, listSpouse, listPerson); //Đầy đủ thông tin ArrayList<PersonInfoDisplay>
        //return GetPersonByCenter.getPersonSimplified2(ft, pid, listSpouse, listPerson); //Giản lược ArrayList<PersonInfoSimplifiedInfoDis>
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
    ArrayList<PersonEntity> p(@RequestParam int ft, @RequestParam int pid, @RequestParam int side){
        //default side = 3
        //Kiểm tra side khác 1, 2, 3. Nếu khác đưa về 3
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
        return GetPersonByCenter.sharingList(ft, pid, listSpouse, listPerson, side); //side = 3: ALL, side = 1: Lấy bên Ngoại, side = 2: Lấy bên nội
    }

    @GetMapping(path = "/test6")
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
}
