package io.kamzy.breezebill.tools;

import java.util.List;

import io.kamzy.breezebill.models.Groupss;
import io.kamzy.breezebill.models.Users;

public class DataManager {
    private static DataManager instance;
    private List<Groupss> allGroups;
    private Users users;

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
}
