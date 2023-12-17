package com.example.familytree.utils;

import com.example.familytree.entities.PersonEntity;

    import java.text.Normalizer;
    import java.util.ArrayList;
    import java.util.Collection;
    import java.util.List;
    import java.util.regex.Pattern;
    import java.util.stream.Collectors;

public class SearchPersonByName {
    public static String changeUnicode(String str){
        String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        temp = pattern.matcher(temp).replaceAll(""); //loại bỏ
        return temp.replaceAll("đ", "d").replaceAll("Đ", "D");
    }
    public static List<PersonEntity> searchPerson(ArrayList<PersonEntity> listPerson, String keyword){
        if(keyword == null || keyword.isEmpty() || keyword.trim().replaceAll(" +", " ").isEmpty() || keyword.trim().replaceAll(" +", " ").equals(" ")) return new ArrayList<PersonEntity>();
        List<PersonEntity> res = listPerson.stream().filter(p -> changeUnicode(p.getPersonName()).replaceAll(" +"," ").trim().contains(changeUnicode(keyword).replaceAll(" +"," ").trim())).collect(Collectors.toList());
        return res;
    }
}
