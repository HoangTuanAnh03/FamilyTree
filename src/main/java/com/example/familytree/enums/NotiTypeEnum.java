package com.example.familytree.enums;

import com.example.familytree.shareds.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotiTypeEnum {
    CREATE_PERSON(Constants.CREATE_PERSON_TYPE),
    UPDATE_PERSON(Constants.UPDATE_PERSON_TYPE),
    DELETE_PERSON(Constants.DELETE_PERSON_TYPE);

    private final String description;
}