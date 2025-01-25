package io.kamzy.breezebill.SharedViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import io.kamzy.breezebill.models.Bills;
import io.kamzy.breezebill.models.Groupss;

public class BillShareViewModels extends ViewModel {
    MutableLiveData<List<Bills>> billData = new MutableLiveData<>();

    public void setBillData(List<Bills> bills) {
        billData.setValue(bills);
    }

    public LiveData<List<Bills>> getBillData() {
        return billData;
    }
}
