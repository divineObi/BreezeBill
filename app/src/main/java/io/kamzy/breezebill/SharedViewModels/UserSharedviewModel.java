package io.kamzy.breezebill.SharedViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.kamzy.breezebill.models.Users;

public class UserSharedviewModel extends ViewModel {
    private final MutableLiveData<Users> userData = new MutableLiveData<>();

    public void setUserData(Users user) {
        userData.setValue(user);
    }

    public LiveData<Users> getUserData() {
        return userData;
    }
}
