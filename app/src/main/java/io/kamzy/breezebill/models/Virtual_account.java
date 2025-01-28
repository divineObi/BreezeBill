package io.kamzy.breezebill.models;

public class Virtual_account {
    private int van_id;
    private int user_id;
    private String virtual_account_number;

    public Virtual_account(int van_id, int user_id, String virtual_account_number) {
        this.van_id = van_id;
        this.user_id = user_id;
        this.virtual_account_number = virtual_account_number;
    }

    public int getVan_id() {
        return van_id;
    }

    public void setVan_id(int van_id) {
        this.van_id = van_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getVirtual_account_number() {
        return virtual_account_number;
    }

    public void setVirtual_account_number(String virtual_account_number) {
        this.virtual_account_number = virtual_account_number;
    }
}
