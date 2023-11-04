package com.example.familytree.repositories;

import com.example.familytree.entities.FamilyTreeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyTreeRepo extends JpaRepository<FamilyTreeEntity, Integer> {
    FamilyTreeEntity findFirstByFamilyTreeId (int id);
}
