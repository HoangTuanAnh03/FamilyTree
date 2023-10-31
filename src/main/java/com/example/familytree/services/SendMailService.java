package com.example.familytree.services;


import com.example.familytree.entities.UserAccountEntity;
import org.springframework.stereotype.Service;


@Service
public interface SendMailService {

    void forgetPasswordUser(UserAccountEntity user, String otp);

    void registerUser(UserAccountEntity user, String verificationCode);
}
