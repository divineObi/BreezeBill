package io.kamzy.breezebill.SharedViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import io.kamzy.breezebill.tools.ApiCallback;
import io.kamzy.breezebill.adapters.GroupAdapter;
import io.kamzy.breezebill.models.Groupss;
import io.kamzy.breezebill.tools.DataManager;

public class UsersGroupsSharedViewModel extends ViewModel {
    MutableLiveData<List<Groupss>> usersGroupsList = new MutableLiveData<>();

    public void setUsersGroupsList(List<Groupss> groups) {
        usersGroupsList.setValue(groups);
    }

    public LiveData<List<Groupss>> getUsersGroupsList() {
        return usersGroupsList;
    }


    public void refreshGroups() {
        if (DataManager.getInstance().getUsers() != null){
            // Simulate an API call or database query
            GroupAdapter.getUsersGroupsAPI("api/groups/" + DataManager.getInstance().getUsers().getUser_id() + "/user-groups", DataManager.getInstance().getToken(), new ApiCallback<List<Groupss>>() {
                @Override
                public void onSuccess(List<Groupss> groups) {
                    List<Groupss> newGroups = DataManager.getInstance().getUsersGroups(); // Replace with actual logic
                    usersGroupsList.postValue(newGroups);
                }

                @Override
                public void onFailure(Throwable t) {

                }
            });
        }

    }
}
