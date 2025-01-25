package io.kamzy.breezebill;

import java.util.List;

import io.kamzy.breezebill.models.Groupss;

public interface ApiCallback<T> {
    void onSuccess(List<Groupss> groups);

    void onFailure(Throwable t);
}
