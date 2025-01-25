package io.kamzy.breezebill.tools;

import java.util.List;

import io.kamzy.breezebill.models.Bills;

public interface BillsAPICallback <T> {
    void onSuccess(List<Bills> allBills);

    void onFailure(Throwable t);
}
