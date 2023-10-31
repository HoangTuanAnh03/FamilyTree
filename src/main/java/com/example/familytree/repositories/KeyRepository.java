package com.example.familytree.repositories;

import com.example.familytree.entities.KeyTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeyRepository extends JpaRepository<KeyTokenEntity, Integer> {
    KeyTokenEntity findFirstByUserId(int userId);
}
