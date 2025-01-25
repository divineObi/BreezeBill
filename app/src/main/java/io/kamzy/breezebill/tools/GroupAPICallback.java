package io.kamzy.breezebill.tools;

public interface GroupAPICallback<T> {
    void onSuccess(String status);

    void onFailure(Throwable t);

}
