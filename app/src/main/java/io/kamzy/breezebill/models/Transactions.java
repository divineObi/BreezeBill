package io.kamzy.breezebill.models;

import java.util.Date;

import io.kamzy.breezebill.enums.PaymentMethod;
import io.kamzy.breezebill.enums.TransactionStatus;
import io.kamzy.breezebill.enums.TransactionType;

public class Transactions {
    private int transaction_id;
    private int sender_id;
    private int receiver_id;
    private String sender_name;
    private String receiver_name;
    private TransactionType type;
    private double amount;
    private String description;
    private PaymentMethod payment_method;
    private Integer related_bill_id;
    private TransactionStatus status;
    private Date created_at;

    public Transactions(int transaction_id, int sender_id, int receiver_id, String sender_name, String receiver_name, TransactionType type, double amount, String description, PaymentMethod payment_method, int related_bill_id, TransactionStatus status, Date created_at) {
        this.transaction_id = transaction_id;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.sender_name = sender_name;
        this.receiver_name = receiver_name;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.payment_method = payment_method;
        this.related_bill_id = related_bill_id;
        this.status = status;
        this.created_at = created_at;
    }

    public int getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(int transaction_id) {
        this.transaction_id = transaction_id;
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public int getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(int receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getReceiver_name() {
        return receiver_name;
    }

    public void setReceiver_name(String receiver_name) {
        this.receiver_name = receiver_name;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PaymentMethod getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(PaymentMethod payment_method) {
        this.payment_method = payment_method;
    }

    public Integer getRelated_bill_id() {
        return related_bill_id;
    }

    public void setRelated_bill_id(Integer related_bill_id) {
        this.related_bill_id = related_bill_id;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
