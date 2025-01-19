package io.kamzy.breezebill.models;

import java.util.Date;

import io.kamzy.breezebill.enums.Gender;

public class Profile {
    private int profile_id;
    private String id_number;
    private String email;
    private String phone_number;
    private Date date_of_birth;
    private Gender gender;
    private String faculty;
    private String department;
    private String class_year;
}
