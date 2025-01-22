package io.kamzy.breezebill.tools;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import io.kamzy.breezebill.models.Groupss;
import io.kamzy.breezebill.models.Profile;
import io.kamzy.breezebill.models.Users;
import io.kamzy.breezebill.models.Wallet;

public class GsonHelper {

    public Profile parseJSONtoProfile(String jsonObjectString) {
        Gson gson = new Gson();
        Type type = new TypeToken<Profile>() {}.getType();
        return gson.fromJson(jsonObjectString, type);
    }

    public Users parseJSONtoUsers(String jsonObjectString) {
        Gson gson = new Gson();
        Type type = new TypeToken<Users>() {}.getType();
        return gson.fromJson(jsonObjectString, type);
    }

    public Wallet parseJSONtoWallet(String jsonObjectString) {
        Gson gson = new Gson();
        Type type = new TypeToken<Wallet>() {}.getType();
        return gson.fromJson(jsonObjectString, type);
    }


    public List<Groupss> parseJSONArrayToListGroups(String s) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Groupss>>() {}.getType();
        return gson.fromJson(s, type);
    }
}
