package io.kamzy.breezebill.models;

import io.kamzy.breezebill.enums.WalletStatus;

public class Wallet {
    private int wallet_id;
    private String id_number;
    private double balance;
    private String code;
    private WalletStatus wallet_status;

    public Wallet(int wallet_id, String id_number, double balance, String code, WalletStatus wallet_status) {
        this.wallet_id = wallet_id;
        this.id_number = id_number;
        this.balance = balance;
        this.code = code;
        this.wallet_status = wallet_status;
    }

    public Wallet() {
    }

    public int getWallet_id() {
        return wallet_id;
    }

    public void setWallet_id(int wallet_id) {
        this.wallet_id = wallet_id;
    }

    public String getId_number() {
        return id_number;
    }

    public void setId_number(String id_number) {
        this.id_number = id_number;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public WalletStatus getWallet_status() {
        return wallet_status;
    }

    public void setWallet_status(WalletStatus wallet_status) {
        this.wallet_status = wallet_status;
    }
}
