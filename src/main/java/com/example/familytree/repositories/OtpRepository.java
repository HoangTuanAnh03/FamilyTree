package com.example.familytree.repositories;

import com.example.familytree.entities.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRepository extends JpaRepository<OtpEntity, Integer> {
    OtpEntity findFirstByOtpCode(String code);
    OtpEntity findFirstByEmail(String email);
    OtpEntity findFirstByUserId(int userId);



    boolean existsByEmail(String email);
}
