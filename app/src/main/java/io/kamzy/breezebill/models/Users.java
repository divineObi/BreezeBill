package io.kamzy.breezebill.models;

import io.kamzy.breezebill.enums.UserRole;

public class Users {
    private int user_id;
    private String first_name;
    private String last_name;
    private String id_number;
    private String password_hash;
    private UserRole role;

    public Users(int user_id, String first_name, String last_name, String id_number, String password_hash, UserRole role) {
        this.user_id = user_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.id_number = id_number;
        this.password_hash = password_hash;
        this.role = role;
    }

    public Users() {
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getId_number() {
        return id_number;
    }

    public void setId_number(String id_number) {
        this.id_number = id_number;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
