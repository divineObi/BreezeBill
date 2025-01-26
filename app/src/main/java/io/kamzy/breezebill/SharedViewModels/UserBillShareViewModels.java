package io.kamzy.breezebill.SharedViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import io.kamzy.breezebill.models.UserBillsDTO;

public class UserBillShareViewModels extends ViewModel {
    MutableLiveData<List<UserBillsDTO>> billData = new MutableLiveData<>();

    public void setBillData(List<UserBillsDTO> bills) {
        billData.setValue(bills);
    }

    public LiveData<List<UserBillsDTO>> getBillData() {
        return billData;
    }
}
