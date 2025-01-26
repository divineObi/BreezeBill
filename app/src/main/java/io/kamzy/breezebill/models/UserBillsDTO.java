package io.kamzy.breezebill.models;

import java.util.Date;

import io.kamzy.breezebill.enums.BillStatus;

public class UserBillsDTO {
    private int user_bill_id;
    private  int bill_id;
    private BillStatus status;
    private double amount_paid;
    private String bill_name;
    private String description;
    private double unit_amount;
    private Date due_date;
    private String payment_account;

    public UserBillsDTO(int user_bill_id, int bill_id, BillStatus status, double amount_paid, String bill_name, String description, double unit_amount, String payment_account, Date due_date) {
        this.user_bill_id = user_bill_id;
        this.bill_id = bill_id;
        this.status = status;
        this.amount_paid = amount_paid;
        this.bill_name = bill_name;
        this.description = description;
        this.unit_amount = unit_amount;
        this.due_date = due_date;
        this.payment_account = payment_account;
    }

    public UserBillsDTO() {
    }

    public int getUser_bill_id() {
        return user_bill_id;
    }

    public void setUser_bill_id(int user_bill_id) {
        this.user_bill_id = user_bill_id;
    }

    public int getBill_id() {
        return bill_id;
    }

    public void setBill_id(int bill_id) {
        this.bill_id = bill_id;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public double getAmount_paid() {
        return amount_paid;
    }

    public void setAmount_paid(double amount_paid) {
        this.amount_paid = amount_paid;
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
}
