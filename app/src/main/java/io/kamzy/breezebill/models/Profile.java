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

    public Profile(int profile_id, String id_number, String email, String phone_number, Date date_of_birth, Gender gender, String faculty, String department, String class_year) {
        this.profile_id = profile_id;
        this.id_number = id_number;
        this.email = email;
        this.phone_number = phone_number;
        this.date_of_birth = date_of_birth;
        this.gender = gender;
        this.faculty = faculty;
        this.department = department;
        this.class_year = class_year;
    }

    public Profile() {
    }

    public int getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(int profile_id) {
        this.profile_id = profile_id;
    }

    public String getId_number() {
        return id_number;
    }

    public void setId_number(String id_number) {
        this.id_number = id_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public Date getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(Date date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getClass_year() {
        return class_year;
    }

    public void setClass_year(String class_year) {
        this.class_year = class_year;
    }
}
