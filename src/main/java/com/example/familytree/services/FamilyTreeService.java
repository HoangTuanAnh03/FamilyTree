package com.example.familytree.services;

import com.example.familytree.entities.FamilyTreeEntity;
import com.example.familytree.models.response.ListTreeResponse;
import com.example.familytree.models.response.InfoUser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FamilyTreeService {

    ListTreeResponse getListTreeByUserId(int userId);

    List<InfoUser> getListUserByFamilyTreeId(int familyTreeId, int userId);

    FamilyTreeEntity create(FamilyTreeEntity familyTree);
}
