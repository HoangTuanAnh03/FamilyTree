package com.example.familytree.repositories;

import com.example.familytree.entities.UserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccountEntity, Integer> {
    boolean existsByUserEmail(String email);
    UserAccountEntity findFirstByUserId(int id);
    UserAccountEntity findFirstByUserEmail(String email);

    Optional<UserAccountEntity> findByUserEmail(String email);

}
