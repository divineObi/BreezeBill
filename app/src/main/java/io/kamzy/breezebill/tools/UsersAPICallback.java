package io.kamzy.breezebill.tools;

import java.util.List;

import io.kamzy.breezebill.models.Bills;
import io.kamzy.breezebill.models.Users;

public interface UsersAPICallback <T>{
    void onSuccess(Users user);

    void onFailure(Throwable t);
}
