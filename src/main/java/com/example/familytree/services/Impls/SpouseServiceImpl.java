package com.example.familytree.services.Impls;

import com.example.familytree.entities.PersonEntity;
import com.example.familytree.entities.SpouseEntity;
import com.example.familytree.repositories.SpouseRepo;
import com.example.familytree.services.SpouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpouseServiceImpl implements SpouseService {
    private final SpouseRepo spouseRepo;


}
