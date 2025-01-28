package io.kamzy.breezebill.SharedViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.kamzy.breezebill.models.Virtual_account;
import io.kamzy.breezebill.models.Wallet;

public class VANShareViewModel extends ViewModel {
    private MutableLiveData<Virtual_account> VAN = new MutableLiveData<>();

    public void setVAN(Virtual_account data) {
        VAN.setValue(data);
    }

    public LiveData<Virtual_account> getVAN() {
        return VAN;
    }
}
