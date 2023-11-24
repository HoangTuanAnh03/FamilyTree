package com.example.familytree.services.Impls;

import com.example.familytree.entities.FamilyTreeEntity;
import com.example.familytree.entities.FamilyTreeUserEntity;
import com.example.familytree.entities.UserAccountEntity;
import com.example.familytree.models.dto.UserInfo;
import com.example.familytree.models.response.InfoUser;
import com.example.familytree.models.response.ListTreeResponse;
import com.example.familytree.repositories.FamilyTreeRepo;
import com.example.familytree.repositories.FamilyTreeUserRepo;
import com.example.familytree.repositories.UserAccountRepo;
import com.example.familytree.services.FamilyTreeService;
import com.example.familytree.services.FamilyTreeUserService;
import com.example.familytree.services.LinkSharingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LinkSharingServiceImpl implements LinkSharingService {


}
