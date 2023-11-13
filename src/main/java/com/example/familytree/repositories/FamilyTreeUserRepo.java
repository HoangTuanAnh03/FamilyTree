package com.example.familytree.repositories;

import com.example.familytree.entities.FamilyTreeUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FamilyTreeUserRepo extends JpaRepository<FamilyTreeUserEntity, Integer> {
    FamilyTreeUserEntity findFirstByFamilyTreeIdAndUserId(int familyTreeId, int userid);
    boolean existsByFamilyTreeIdAndUserId (int familyTreeId, int userid);
}
