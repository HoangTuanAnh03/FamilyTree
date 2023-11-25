package com.example.familytree.utils;

import com.example.familytree.entities.PersonEntity;
import com.example.familytree.entities.SpouseEntity;
import com.example.familytree.models.dto.PersonDisplayDto;
import com.example.familytree.models.dto.PersonDto;
import com.example.familytree.models.dto.PersonSimplifiedInfo;
import com.example.familytree.models.dto.SideDto;
import com.example.familytree.models.response.PersonDataV2;
import com.example.familytree.models.response.PersonInfoDisplay;
import com.example.familytree.models.response.PersonInfoSimplifiedInfoDis;
import com.example.familytree.repositories.PersonRepo;
import com.example.familytree.repositories.SpouseRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class GetPersonByCenter {
    private final PersonRepo personRepo;
    private final SpouseRepo spouseRepo;
    public static int getParentIdByPersonId(int personId, ArrayList<PersonEntity> listPerson){
        for(int i = 0; i < listPerson.size(); i++){
            if(listPerson.get(i).getPersonId() == personId){
                if(listPerson.get(i).getParentsId()==null) return 0;
                return listPerson.get(i).getParentsId();
            }
        }
        return 0;
    }
    public static int getFatherIdByPersonId(int personId, ArrayList<PersonEntity> listPerson){
        for(int i = 0; i < listPerson.size(); i++){
            if(listPerson.get(i).getPersonId() == personId){
                if(listPerson.get(i).getFatherId() == null) return 0;
                int fid = listPerson.get(i).getFatherId();
                PersonEntity father = listPerson.stream().filter(p -> p.getPersonId() == fid).findFirst().orElse(null);
                if(father == null){
                    return 0;
                }
                return listPerson.get(i).getFatherId();
            }
        }
        return 0;
    }
    public static int getMotherIdByPersonId(int personId, ArrayList<PersonEntity> listPerson){
        for(int i = 0; i < listPerson.size(); i++){
            if(listPerson.get(i).getPersonId() == personId){
                if(listPerson.get(i).getMotherId() == null) return 0;
                int mid = listPerson.get(i).getMotherId();
                PersonEntity mother = listPerson.stream().filter(p -> p.getPersonId() == mid).findFirst().orElse(null);
                if(mother == null){
                    return 0;
                }
                return listPerson.get(i).getMotherId();
            }
        }
        return 0;
    }
    public static String getGenderByPersonId(int personId, ArrayList<PersonEntity> listPerson){
        for(int i = 0; i < listPerson.size(); i++){
            if(listPerson.get(i).getPersonId() == personId){
                if(listPerson.get(i).getPersonGender()){
                    return "Male";
                }
                else{
                    return "Female";
                }
            }
        }
        return null;
    }
    public static void getTheMainTree(ArrayList<Integer> personIdInTheMainTree,
                                      int personId, ArrayList<PersonEntity> listPerson,
                                      ArrayList<SideDto> personWithSide,
                                      String side,
                                      int isFatherSide,
                                      Map<Integer, Integer> fatherSide){
        fatherSide.put(personId, isFatherSide);
        int motherId = getMotherIdByPersonId(personId, listPerson);
        int fatherId = getFatherIdByPersonId(personId, listPerson);
        System.out.println(personId + "  " + motherId + "  " + fatherId + "  ");

        personIdInTheMainTree.add(personId);
        personWithSide.add(SideDto.create(side, personId));
        if(motherId != 0){
            String mo = side + "1";
            if(isFatherSide == 0){
                getTheMainTree(personIdInTheMainTree, motherId, listPerson, personWithSide, mo, 2, fatherSide);
            }
            else{
                getTheMainTree(personIdInTheMainTree, motherId, listPerson, personWithSide, mo, isFatherSide, fatherSide);
            }
        }
        if(fatherId != 0){
            String fa = side + "0";
            if(isFatherSide == 0){
                getTheMainTree(personIdInTheMainTree, fatherId, listPerson, personWithSide, fa, 1, fatherSide);
            }
            else{
                getTheMainTree(personIdInTheMainTree, fatherId, listPerson, personWithSide, fa, isFatherSide, fatherSide);
            }
        }
    }
    public static String getVocative(PersonEntity personInCenter, PersonEntity person2, ArrayList<Integer> personInTheMainTree){
        return "";
    }
    //    public static int largerThan(PersonEntity person1, PersonEntity person2){
//        if(person1.getPersonId() == person2.getPersonId()){
//            return 0;
//        }
//        else if(person1.getGroupChildId() == person2.getGroupChildId()){
//            if(person1.getSiblingNum() < person2.getSiblingNum()){
//                return -1;
//            }
//            else{
//                return 1;
//            }
//        }
//        else{
//            return 199;
//        }
//    }
//    public static int isLarger(PersonEntity personInCenter, PersonEntity person2, ArrayList<Integer> personInTheMainTree){
//        int rank1 = personInCenter.getPersonRank();
//        int rank2 = person2.getPersonRank();
//        int rankTemp;
//        PersonEntity personSpouse;
//        if(!personInTheMainTree.contains(person2.getFatherId()) && !personInTheMainTree.contains(person2.getMotherId())){
//            ArrayList<SpouseEntity> s;
//            if(!person2.getPersonGender()) {
//                s = new ArrayList<SpouseEntity>(spouseRepo.findByHusbandId(person2.getPersonId()));
//            }
//            else{
//                s = new ArrayList<SpouseEntity>(spouseRepo.findByWifeId(person2.getPersonId()));
//            }
//            for(SpouseEntity se:s){
//                if(!person2.getPersonGender()){
//                    if(personInTheMainTree.contains(se.getWifeId())){
//                        personSpouse = personRepo.findFirstByPersonId(se.getWifeId());
//                        break;
//                    }
//                }//
//                else{
//                    if(personInTheMainTree.contains(se.getHusbandId())){
//                        personSpouse = personRepo.findFirstByPersonId(se.getHusbandId());
//                        break;
//                    }
//                }
//            }
//
//
//        }
//        PersonEntity personTemp1 = personInCenter;
//        PersonEntity personTemp2 = personInCenter;
//        if(rank1 == rank2){
//            if(largerThan(personInCenter, person2) <= 1){
//                return largerThan(personInCenter, person2);
//            }
//            else{
//
//            }
//        }
//        if (rank1 < rank2){
//            rankTemp = rank1;
//            while(rankTemp < rank2){
//                personTemp1 = personRepo.findFirstByPersonId(personTemp1.getFatherId());
//                personTemp2 = personRepo.findFirstByPersonId(personTemp2.getMotherId());
//            }
//        }
//        else{
//
//        }
//    }
//    public static String getVocative(PersonEntity personInCenter, PersonEntity person2, ArrayList<Integer> personInTheMainTree){
//        int rank1 = personInCenter.getPersonRank();
//        int rank2 = person2.getPersonRank();
//        switch (rank1 - rank2){
//            case -5:
//                return "Tiên tổ";
//            case -4:
//                return "Kỵ";
//            case -3:
//                return "Cố";
//            case -2:
//                if(person2.getPersonGender()){
//                    return "Ông";
//                }
//                return "Bà";
//            case -1:
//                if(personInCenter.getFatherId() == person2.getPersonId()) return "Bố";
//                else if (personInCenter.getMotherId() == person2.getPersonId()) return "Mẹ";
//                else{
//                    if(isLarger(personInCenter, person2, personInTheMainTree) == 1) return "Bác";
//                    else if(isLarger(personInCenter, person2, personInTheMainTree) == -1){
//                        if("Bên Mẹ" == ""){
//                            if(!personInTheMainTree.contains(person2.getFatherId()) && !personInTheMainTree.contains(person2.getMotherId())){
//                                if(!person2.getPersonGender()) return "Chú";
//                                else return "Mợ";
//                            }
//                            else{
//                                if(person2.getPersonGender()) return "Dì";
//                                else return "Cậu";
//                            }
//                        }
//                        if("Bên bố" == ""){
//                            if(!personInTheMainTree.contains(person2.getFatherId()) && !personInTheMainTree.contains(person2.getMotherId())){
//                                if(!person2.getPersonGender()) return "Chú";
//                                else return "Thím";
//                            }
//                            else{
//                                if(person2.getPersonGender()) return "Cô";
//                                else return "Chú";
//                            }
//                        }
//                    }
//                    return "";//bố, mẹ, bác, cô, dì, chú
//                }
//            case 0:
//                if(personInCenter.getGroupChildId() == person2.getGroupChildId()){
//                    if(personInCenter.getSiblingNum() > person2.getSiblingNum()){
//                        return "Em";
//                    }
//                    else if (personInCenter.getSiblingNum() < person2.getSiblingNum()){
//                        if(person2.getPersonGender()){
//                            return "Chị";
//                        }
//                        else{
//                            return "Anh";
//                        }
//                    }
//                }
//                else{
//                    return "";
//                }
//            case 1:
//                if(person2.getFatherId() == personInCenter.getPersonId() || person2.getMotherId() == personInCenter.getPersonId()){
//                    return "Con";
//                }
//                return "Cháu"; //Con, cháu
//            case 2:
//                return "Cháu"; //cháu
//            case 3:
//                ArrayList<PersonEntity> parents = new ArrayList<>();
//                parents.add(personRepo.findFirstByPersonId(person2.getFatherId()));
//                parents.add(personRepo.findFirstByPersonId(person2.getMotherId()));
//
//                ArrayList<PersonEntity> p = new ArrayList<>();
//                for(PersonEntity pa : parents){
//                    p.add(personRepo.findFirstByPersonId(pa.getFatherId()));
//                    p.add(personRepo.findFirstByPersonId(pa.getMotherId()));
//                }
//                ArrayList<PersonEntity> pp = new ArrayList<>();
//                for(PersonEntity pa : p){
//                    pp.add(personRepo.findFirstByPersonId(pa.getFatherId()));
//                    pp.add(personRepo.findFirstByPersonId(pa.getMotherId()));
//                }
//
//                if(pp.contains(personInCenter)){
//                    return "Chắt";
//                }
//                else{
//                    return "Cháu";
//                }
//            case 4:
//                ArrayList<PersonEntity> parents4 = new ArrayList<>();
//                parents4.add(personRepo.findFirstByPersonId(person2.getFatherId()));
//                parents4.add(personRepo.findFirstByPersonId(person2.getMotherId()));
//
//                ArrayList<PersonEntity> p4 = new ArrayList<>();
//                for(PersonEntity pa : parents4){
//                    p4.add(personRepo.findFirstByPersonId(pa.getFatherId()));
//                    p4.add(personRepo.findFirstByPersonId(pa.getMotherId()));
//                }
//                ArrayList<PersonEntity> pp4 = new ArrayList<>();
//                for(PersonEntity pa : p4){
//                    pp4.add(personRepo.findFirstByPersonId(pa.getFatherId()));
//                    pp4.add(personRepo.findFirstByPersonId(pa.getMotherId()));
//                }
//                ArrayList<PersonEntity> ppp4 = new ArrayList<>();
//                for(PersonEntity pa : pp4){
//                    ppp4.add(personRepo.findFirstByPersonId(pa.getFatherId()));
//                    ppp4.add(personRepo.findFirstByPersonId(pa.getMotherId()));
//                }
//                if(ppp4.contains(personInCenter)){
//                    return "Chút/Chít";
//                }
//                else{
//                    return "Cháu";
//                }
//            case 5:
//                ArrayList<PersonEntity> parents5 = new ArrayList<>();
//                parents5.add(personRepo.findFirstByPersonId(person2.getFatherId()));
//                parents5.add(personRepo.findFirstByPersonId(person2.getMotherId()));
//
//                ArrayList<PersonEntity> p5 = new ArrayList<>();
//                for(PersonEntity pa : parents5){
//                    p5.add(personRepo.findFirstByPersonId(pa.getFatherId()));
//                    p5.add(personRepo.findFirstByPersonId(pa.getMotherId()));
//                }
//                ArrayList<PersonEntity> pp5 = new ArrayList<>();
//                for(PersonEntity pa : p5){
//                    pp5.add(personRepo.findFirstByPersonId(pa.getFatherId()));
//                    pp5.add(personRepo.findFirstByPersonId(pa.getMotherId()));
//                }
//                ArrayList<PersonEntity> ppp5 = new ArrayList<>();
//                for(PersonEntity pa : pp5){
//                    ppp5.add(personRepo.findFirstByPersonId(pa.getFatherId()));
//                    ppp5.add(personRepo.findFirstByPersonId(pa.getMotherId()));
//                }
//                ArrayList<PersonEntity> pppp = new ArrayList<>();
//                for(PersonEntity pa : ppp5){
//                    pppp.add(personRepo.findFirstByPersonId(pa.getFatherId()));
//                    pppp.add(personRepo.findFirstByPersonId(pa.getMotherId()));
//                }
//                if(pppp.contains(personInCenter)){
//                    return "Chụt/Chuỵt";
//                }
//                else{
//                    return "Cháu";
//                }
//            default:
//                return "";
//        }
//    }
    public static ArrayList<Integer> getPersonIdBySpouseId(ArrayList<SpouseEntity> listSpouse, int personId, ArrayList<PersonEntity> personEntities){
        ArrayList<Integer> res = new ArrayList<Integer>();
        for(int i = 0; i < listSpouse.size(); i++){
            if(listSpouse.get(i).getHusbandId() != null && listSpouse.get(i).getHusbandId().intValue() == personId && listSpouse.get(i).getWifeId() != null){
                int wid = listSpouse.get(i).getWifeId().intValue();
                PersonEntity p = personEntities.stream().filter(pe -> pe.getPersonId() == wid).findFirst().orElse(null);
                if(p != null){
                    res.add(wid);
                }
            }
            if(listSpouse.get(i).getWifeId() != null && listSpouse.get(i).getWifeId().intValue() == personId && listSpouse.get(i).getHusbandId() != null){
                int hid = listSpouse.get(i).getHusbandId().intValue();
                PersonEntity p = personEntities.stream().filter(pe -> pe.getPersonId() == hid).findFirst().orElse(null);
                if(p != null){
                    res.add(hid);
                }
            }
        }
        return res;
    }
    public static ArrayList<Integer> getSpouseIds(ArrayList<SpouseEntity> listSpouse, int personId){
        ArrayList<Integer> spouseIds = new ArrayList<Integer>();
        for(int i = 0; i < listSpouse.size(); i++){
            if((listSpouse.get(i).getHusbandId() != null && personId == listSpouse.get(i).getHusbandId().intValue()) || (listSpouse.get(i).getWifeId() != null && personId == listSpouse.get(i).getWifeId().intValue())){
                spouseIds.add(listSpouse.get(i).getSpouseId());
            }
        }
        return spouseIds;
    }
    public static void getPerson(ArrayList<Integer> personsWithCenter,
                                 ArrayList<Integer> personIdInTheMainTree,
                                 ArrayList<PersonEntity> persons,
                                 ArrayList<SpouseEntity> spouses,
                                 Map<Integer, Integer> fatherSide) {
        Set<Integer> personTemp = new HashSet<>();
        for (int i = 0; i < personIdInTheMainTree.size(); i++) {

            int personId = personIdInTheMainTree.get(i);
            int isFatherSide = fatherSide.get(personId);
            int parentId = getParentIdByPersonId(personId, persons);
            if (parentId == 0) {
                boolean isChildOfCenter = false;
                for (int x : personsWithCenter) {
                    if (getParentIdByPersonId(x, persons) == personId) {
                        isChildOfCenter = true;
                        break;
                    }
                }
                if (!isChildOfCenter) {
                    personsWithCenter.addAll(getPersonIdBySpouseId(spouses, personId, persons));
                    ArrayList<Integer> spousePidList = getPersonIdBySpouseId(spouses, personId, persons);
                    for(int x: spousePidList){
                        if(fatherSide.get(x) == null){
                            fatherSide.put(x, isFatherSide);
                        }
                    }
                }
            }
            String gender = getGenderByPersonId(personId, persons);
            for (PersonEntity person : persons) {
                if ((gender == "Male" && person.getFatherId() != null && person.getFatherId().intValue() == personId) || (gender == "Female" && person.getMotherId() != null && person.getMotherId().intValue() == personId)) {

                    int childPersonId = person.getPersonId();
                    personsWithCenter.add(childPersonId);
                    fatherSide.putIfAbsent(childPersonId, isFatherSide);
                    personTemp.add(childPersonId);
                    ArrayList<Integer> spousePidList2 = getPersonIdBySpouseId(spouses, childPersonId, persons);
                    int isFatherSideBySpouse = fatherSide.get(childPersonId);
                    personsWithCenter.addAll(spousePidList2);
                    for(int x: spousePidList2){
                        fatherSide.putIfAbsent(x, isFatherSideBySpouse);
                    }
                }
            }
        }
        if (!personTemp.isEmpty()) {
            getPerson(personsWithCenter, new ArrayList<>(personTemp), persons, spouses, fatherSide);
        }
    }
    public static PersonInfoDisplay getInfor(ArrayList<Integer> personsWithCenter,
                                             int personId, ArrayList<PersonEntity> persons,
                                             ArrayList<SpouseEntity> spouses,
                                             ArrayList<PersonInfoDisplay> apiDisplay,
                                             ArrayList<SideDto> personWithSides,
                                             int personCenterId,
                                             Map<Integer, Integer> fatherSide){
        PersonEntity person1 = persons.stream().filter(person -> person.getPersonId() == personId).findFirst().orElse(null);
        SideDto personSide = personWithSides.stream().filter(s -> s.getPersonId() == personId).findFirst().orElse(null);
        String side = "";
        if(personSide != null)
            side = personSide.getSide();
        PersonInfoDisplay api;
        ArrayList<Integer> spouseIds = getSpouseIds(spouses, personId);
        ArrayList<Integer> personBySpouse = getPersonIdBySpouseId(spouses, personId, persons);
        int grId1 = personId;
        int grId2 = personId;
        int count = 0;
        for(int i = 0; i < personBySpouse.size(); i++){
            int person2 = personBySpouse.get(i);
            PersonInfoDisplay apiCheck = apiDisplay.stream().filter(apid -> apid.getGroupId() == personId).findFirst().orElse(null);
            if(apiCheck == null){
                for(int j = 0; j < personsWithCenter.size(); j++){
                    if(personBySpouse.get(i) == personsWithCenter.get(j)){
                        grId1 = person2;
                        grId2 = personId;
                        count++;
                        break;
                    }
                }
            }
            else{
                grId2 = apiCheck.getGroupId();
                grId1 = apiCheck.getGroupId();
                count = 199203;
            }
        }
        PersonEntity personCenter = persons.stream().filter(person -> person.getPersonId() == personCenterId).findFirst().orElse(null);
        String vocative = getVocative(personCenter, person1, personsWithCenter);
        PersonDisplayDto p = PersonDisplayDto.create(person1.getPersonName(), person1.getPersonGender()?"Male":"Female", person1.getPersonDob(), person1.getPersonDod(), person1.getParentsId(), person1.getFamilyTreeId(), person1.getPersonStatus(), person1.getPersonRank(), person1.getFatherId(), person1.getMotherId(), person1.getPersonImage(), person1.getSiblingNum(),person1.getGroupChildId());
        int isFatherSide = fatherSide.get(personId);
        if(count > 1){
            api =  PersonInfoDisplay.create(personId, person1.getParentsId(),p, spouseIds, grId2, side, person1.getPersonRank(), isFatherSide, vocative);
        }
        else{
            api =  PersonInfoDisplay.create(personId, person1.getParentsId(),p, spouseIds, grId1, side, person1.getPersonRank(), isFatherSide, vocative);
        }
        return api;
    }
    public static ArrayList<PersonInfoDisplay> GetPersonByCenterDis(int familyTreeId,
                                                                    int personCenterId,
                                                                    ArrayList<SpouseEntity> listSpouse,
                                                                    ArrayList<PersonEntity> listPerson){

        ArrayList<Integer> personIdInTheMainTree = new ArrayList<Integer>();
        ArrayList<SideDto> personWithSide = new ArrayList<SideDto>();
        Map<Integer, Integer> fatherSide = new HashMap<>();

        getTheMainTree(personIdInTheMainTree, personCenterId, listPerson, personWithSide, "", 0, fatherSide);
//        for(int j:personIdInTheMainTree){
//            System.out.print(j + " ");
//        }
//        System.out.println();
        ArrayList<Integer> personsWithCenter = new ArrayList<>(personIdInTheMainTree);
        getPerson(personsWithCenter, personIdInTheMainTree, listPerson, listSpouse, fatherSide);
//        for(int j:personsWithCenter){
//            System.out.print(j + " ");
//        }
//        System.out.println();

        Set<Integer> sett = new LinkedHashSet<>();
        sett.addAll(personsWithCenter);
        personsWithCenter.clear();
        personsWithCenter.addAll(sett);

        ArrayList<PersonInfoDisplay> apiDisplays = new ArrayList<PersonInfoDisplay>();
        for(int i = 0; i < personsWithCenter.size(); i++){
            apiDisplays.add(getInfor(personsWithCenter, personsWithCenter.get(i), listPerson, listSpouse, apiDisplays, personWithSide, personCenterId, fatherSide));
        }
        return apiDisplays;
    }
    public static PersonInfoSimplifiedInfoDis getInforSimplified(ArrayList<Integer> personsWithCenter,
                                                                 int personId, ArrayList<PersonEntity> persons,
                                                                 ArrayList<SpouseEntity> spouses,
                                                                 ArrayList<PersonInfoSimplifiedInfoDis> apiDisplay,
                                                                 ArrayList<SideDto> personWithSides,
                                                                 int personCenterId,
                                                                 Map<Integer, Integer> fatherSide){
        PersonEntity person1 = persons.stream().filter(person -> person.getPersonId() == personId).findFirst().orElse(null);
        SideDto personSide = personWithSides.stream().filter(s -> s.getPersonId() == personId).findFirst().orElse(null);
        String side = "";
        if(personSide != null)
            side = personSide.getSide();
        PersonInfoSimplifiedInfoDis api;
        ArrayList<Integer> spouseIds = getSpouseIds(spouses, personId);
        ArrayList<Integer> personBySpouse = getPersonIdBySpouseId(spouses, personId, persons);
        int grId1 = personId;
        int grId2 = personId;
        int count = 0;
        for(int i = 0; i < personBySpouse.size(); i++){
            int person2 = personBySpouse.get(i);
            PersonInfoSimplifiedInfoDis apiCheck = apiDisplay.stream().filter(apid -> apid.getGroupId() == personId).findFirst().orElse(null);
            if(apiCheck == null){
                for(int j = 0; j < personsWithCenter.size(); j++){
                    if(personBySpouse.get(i) == personsWithCenter.get(j)){
                        grId1 = person2;
                        grId2 = personId;
                        count++;
                        break;
                    }
                }
            }
            else{
                grId2 = apiCheck.getGroupId();
                grId1 = apiCheck.getGroupId();
                count = 199203;
            }
        }
        PersonEntity personCenter = persons.stream().filter(person -> person.getPersonId() == personCenterId).findFirst().orElse(null);
        String vocative = getVocative(personCenter, person1, personsWithCenter);
        int isFatherSide = fatherSide.get(personId);
        PersonSimplifiedInfo p = PersonSimplifiedInfo.create(person1.getPersonName(),
                                                                person1.getPersonGender()?"Male":"Female",
                                                                person1.getPersonDob(),
                                                                person1.getPersonDod(),
                                                                person1.getPersonStatus());
        if(count > 1){
            api =  PersonInfoSimplifiedInfoDis.create(personId, person1.getParentsId(), p, spouseIds, grId2, side, person1.getPersonRank(), isFatherSide, vocative);
        }
        else{
            api =  PersonInfoSimplifiedInfoDis.create(personId, person1.getParentsId(), p, spouseIds, grId1, side, person1.getPersonRank(), isFatherSide, vocative);
        }
        return api;
    }
    public static ArrayList<PersonInfoSimplifiedInfoDis> getPersonSimplified(int familyTreeId, int personCenterId, ArrayList<SpouseEntity> listSpouse, ArrayList<PersonEntity> listPerson){
        ArrayList<Integer> personIdInTheMainTree = new ArrayList<Integer>();
        ArrayList<SideDto> personWithSide = new ArrayList<SideDto>();
        Map<Integer, Integer> fatherSide = new HashMap<>();

        getTheMainTree(personIdInTheMainTree, personCenterId, listPerson, personWithSide, "", 0, fatherSide);
//        for(int j:personIdInTheMainTree){
//            System.out.print(j + " ");
//        }
//        System.out.println();
        ArrayList<Integer> personsWithCenter = new ArrayList<>(personIdInTheMainTree);
        getPerson(personsWithCenter, personIdInTheMainTree, listPerson, listSpouse, fatherSide);
//        for(int j:personsWithCenter){
//            System.out.print(j + " ");
//        }
//        System.out.println();

        Set<Integer> sett = new LinkedHashSet<>();
        sett.addAll(personsWithCenter);
        personsWithCenter.clear();
        personsWithCenter.addAll(sett);

        ArrayList<PersonInfoSimplifiedInfoDis> apiDisplays = new ArrayList<PersonInfoSimplifiedInfoDis>();
        for(int i = 0; i < personsWithCenter.size(); i++){
            apiDisplays.add(getInforSimplified(personsWithCenter, personsWithCenter.get(i), listPerson, listSpouse, apiDisplays, personWithSide, personCenterId, fatherSide));
        }
        return apiDisplays;
    }
    public static Map<Integer, PersonDataV2> getDataV2(int familyTreeId, int personCenterId, ArrayList<SpouseEntity> listSpouse, ArrayList<PersonEntity> listPerson){
        Map<Integer, PersonDataV2> apiDislays = new HashMap<>();
        ArrayList<PersonInfoDisplay> listPersonByCenter = GetPersonByCenterDis(familyTreeId, personCenterId, listSpouse, listPerson);
        for(PersonInfoDisplay p : listPersonByCenter){
            int personId = p.getId();
            Integer parentId = p.getParentId();
            Integer fatherId = p.getInfo().getFatherId();
            Integer motherId = p.getInfo().getMotherId();
            ArrayList<Integer> spousePersonIds = getPersonIdBySpouseId(listSpouse, personId, listPerson);
            ArrayList<Integer> childrenIds = new ArrayList<>();
            for(int j = 0; j < listPersonByCenter.size(); j++) {
                PersonInfoDisplay p2 = listPersonByCenter.get(j);
                if ((p2.getInfo().getFatherId() != null && p2.getInfo().getFatherId() == personId) || (p2.getInfo().getMotherId() != null && p2.getInfo().getMotherId() == personId))
                    childrenIds.add(p2.getId());
            }
            PersonDataV2 personDataV2 = PersonDataV2.create(p, fatherId, motherId, spousePersonIds, childrenIds);
            apiDislays.put(personId, personDataV2);
        }
        return apiDislays;
    }
}
