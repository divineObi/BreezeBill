package io.kamzy.breezebill.tools;

import java.util.List;

import io.kamzy.breezebill.models.Transactions;
import io.kamzy.breezebill.models.UserBillsDTO;

public interface TransactionAPICallback<T> {
    void onSuccess(List<Transactions> allTransactions);

    void onFailure(Throwable t);
}
