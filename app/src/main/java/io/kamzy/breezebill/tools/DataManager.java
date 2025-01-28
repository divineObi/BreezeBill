package io.kamzy.breezebill.tools;

import java.util.List;

import io.kamzy.breezebill.models.Bills;
import io.kamzy.breezebill.models.Groupss;
import io.kamzy.breezebill.models.Transactions;
import io.kamzy.breezebill.models.UserBillsDTO;
import io.kamzy.breezebill.models.Users;
import io.kamzy.breezebill.models.Virtual_account;
import io.kamzy.breezebill.models.Wallet;

public class DataManager {
    private static DataManager instance;
    private List<Groupss> allGroups, usersGroups;
    private Users users;
    String token;
    List<UserBillsDTO> allBills;
    List<UserBillsDTO> paidBills;
    List<UserBillsDTO> unpaidBills;
    Wallet wallet;
    Virtual_account VAN;
    List<Transactions> allTransactions;

    private DataManager() {}

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public List<Groupss> getAllGroups() {
        return allGroups;
    }

    public void setAllGroups(List<Groupss> allGroups) {
        this.allGroups = allGroups;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public List<Groupss> getUsersGroups() {
        return usersGroups;
    }

    public void setUsersGroups(List<Groupss> usersGroups) {
        this.usersGroups = usersGroups;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<UserBillsDTO> getAllBills() {
        return allBills;
    }

    public void setAllBills(List<UserBillsDTO> allBills) {
        this.allBills = allBills;
    }

    public List<UserBillsDTO> getPaidBills() {
        return paidBills;
    }

    public void setPaidBills(List<UserBillsDTO> paidBills) {
        this.paidBills = paidBills;
    }

    public List<UserBillsDTO> getUnpaidBills() {
        return unpaidBills;
    }

    public void setUnpaidBills(List<UserBillsDTO> unpaidBills) {
        this.unpaidBills = unpaidBills;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public Virtual_account getVAN() {
        return VAN;
    }

    public void setVAN(Virtual_account VAN) {
        this.VAN = VAN;
    }

    public List<Transactions> getAllTransactions() {
        return allTransactions;
    }

    public void setAllTransactions(List<Transactions> allTransactions) {
        this.allTransactions = allTransactions;
    }
}
