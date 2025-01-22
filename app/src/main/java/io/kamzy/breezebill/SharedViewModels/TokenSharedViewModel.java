package io.kamzy.breezebill.SharedViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.kamzy.breezebill.models.Users;

public class TokenSharedViewModel extends ViewModel {
    private final MutableLiveData<String> token = new MutableLiveData<>();

    public void setToken(String data) {
        token.setValue(data);
    }

    public LiveData<String> getToken() {
        return token;
    }
}
