package com.example.familytree.services;

import com.example.familytree.entities.FamilyTreeEntity;
import com.example.familytree.entities.FamilyTreeUserEntity;
import org.springframework.stereotype.Service;

@Service
public interface FamilyTreeUserService {
    void create(FamilyTreeUserEntity familyTreeUserEntity);
}
