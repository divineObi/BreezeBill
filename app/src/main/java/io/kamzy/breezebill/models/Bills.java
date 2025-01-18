package io.kamzy.breezebill.models;

import java.util.Date;

import io.kamzy.breezebill.enums.BillCategory;
import io.kamzy.breezebill.enums.BillStatus;
import io.kamzy.breezebill.enums.BillType;

public class Bills {
      //    created by
    private String bill_name;
    private String amount;

    public Bills(String bill_name, String amount) {
        this.bill_name = bill_name;
        this.amount = amount;
    }

    public String getBill_name() {
        return bill_name;
    }

    public void setBill_name(String bill_name) {
        this.bill_name = bill_name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
