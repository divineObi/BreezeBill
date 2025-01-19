package io.kamzy.breezebill.models;

import io.kamzy.breezebill.enums.UserRole;

public class Users {
    private int user_id;
    private String first_name;
    private String last_name;
    private String id_number;
    private String password_hash;
    private UserRole role;
}
