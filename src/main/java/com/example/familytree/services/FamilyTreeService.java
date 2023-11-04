package com.example.familytree.services;

import com.example.familytree.entities.FamilyTreeEntity;
import org.springframework.stereotype.Service;

@Service
public interface FamilyTreeService {
    void create(FamilyTreeEntity familyTree);
}
