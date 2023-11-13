package com.example.familytree.utils;

import com.example.familytree.entities.PersonEntity;
import com.example.familytree.repositories.PersonRepo;
import com.example.familytree.repositories.SpouseRepo;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.Base64;
import java.util.List;


//@RequiredArgsConstructor
public class PersonUtil {
    private static PersonRepo personRepo;
    private static SpouseRepo spouseRepo;
    public static List<PersonEntity> getListChildByFather(int fatherId) {
        return personRepo.findByFatherId(fatherId);
    }

    public static List<PersonEntity> getListChildByMother(int motherId) {
        return personRepo.findByMotherId(motherId);
    }

    public static List<PersonEntity> getListChildByFatherIdAndMotherId(int fatherId, int motherId) {
        return personRepo.findByFatherIdAndMotherId(fatherId, motherId);
    }
}
