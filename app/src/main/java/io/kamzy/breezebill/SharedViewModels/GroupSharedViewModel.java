package io.kamzy.breezebill.SharedViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import io.kamzy.breezebill.models.Groupss;

public class GroupSharedViewModel extends ViewModel {
    MutableLiveData<List<Groupss>> groupData = new MutableLiveData<>();

    public void setGroupData(List<Groupss> groups) {
        groupData.setValue(groups);
    }

    public MutableLiveData<List<Groupss>> getGroupData() {
        return groupData;
    }
}
