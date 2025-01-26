package io.kamzy.breezebill.tools;

import java.util.List;

import io.kamzy.breezebill.models.UserBillsDTO;

public interface UserBillsAPICallback<T> {
    void onSuccess(List<UserBillsDTO> allBills);

    void onFailure(Throwable t);
}
