package io.kamzy.breezebill.SharedViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.kamzy.breezebill.models.Wallet;

public class WalletSharedviewModel extends ViewModel {
    private MutableLiveData<Wallet> walletData = new MutableLiveData<>();

    public void setWalletData(Wallet data) {
        walletData.setValue(data);
    }

    public LiveData<Wallet> getWalletData() {
        return walletData;
    }
}
