package io.kamzy.breezebill.models;

import java.util.Date;

import io.kamzy.breezebill.enums.BillCategory;
import io.kamzy.breezebill.enums.BillStatus;
import io.kamzy.breezebill.enums.BillType;

public class Bills {
    private int bill_id;
    private int created_by;    //    created by
    private int group_id;
    private String bill_name;
    private String description;
    private double unit_amount;
    private double total_amount;
    private Date due_date;
    private String payment_account;
    private String payment_bank;
    private BillStatus status;
    private BillType bill_type;
    private String recipient_group;
    private BillCategory bill_category;


    public Bills(int bill_id, int created_by, int group_id, String bill_name, String description, double unit_amount, double total_amount, Date due_date, String payment_account, String payment_bank, BillStatus status, BillType bill_type, String recipient_group, BillCategory bill_category) {
        this.bill_id = bill_id;
        this.created_by = created_by;
        this.group_id = group_id;
        this.bill_name = bill_name;
        this.description = description;
        this.unit_amount = unit_amount;
        this.total_amount = total_amount;
        this.due_date = due_date;
        this.payment_account = payment_account;
        this.payment_bank = payment_bank;
        this.status = status;
        this.bill_type = bill_type;
        this.recipient_group = recipient_group;
        this.bill_category = bill_category;
    }

    public Bills() {
    }


    public int getBill_id() {
        return bill_id;
    }

    public void setBill_id(int bill_id) {
        this.bill_id = bill_id;
    }

    public int getCreated_by() {
        return created_by;
    }

    public void setCreated_by(int created_by) {
        this.created_by = created_by;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public String getBill_name() {
        return bill_name;
    }

    public void setBill_name(String bill_name) {
        this.bill_name = bill_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getUnit_amount() {
        return unit_amount;
    }

    public void setUnit_amount(double unit_amount) {
        this.unit_amount = unit_amount;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(double total_amount) {
        this.total_amount = total_amount;
    }

    public Date getDue_date() {
        return due_date;
    }

    public void setDue_date(Date due_date) {
        this.due_date = due_date;
    }

    public String getPayment_account() {
        return payment_account;
    }

    public void setPayment_account(String payment_account) {
        this.payment_account = payment_account;
    }

    public String getPayment_bank() {
        return payment_bank;
    }

    public void setPayment_bank(String payment_bank) {
        this.payment_bank = payment_bank;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public BillType getBill_type() {
        return bill_type;
    }

    public void setBill_type(BillType bill_type) {
        this.bill_type = bill_type;
    }

    public String getRecipient_group() {
        return recipient_group;
    }

    public void setRecipient_group(String recipient_group) {
        this.recipient_group = recipient_group;
    }

    public BillCategory getBill_category() {
        return bill_category;
    }

    public void setBill_category(BillCategory bill_category) {
        this.bill_category = bill_category;
    }
}
