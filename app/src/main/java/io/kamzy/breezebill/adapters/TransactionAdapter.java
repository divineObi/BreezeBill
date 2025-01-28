package io.kamzy.breezebill.adapters;


import static io.kamzy.breezebill.CreateBill.formatAmount;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

import io.kamzy.breezebill.R;
import io.kamzy.breezebill.enums.TransactionStatus;
import io.kamzy.breezebill.enums.TransactionType;
import io.kamzy.breezebill.models.Transactions;
import io.kamzy.breezebill.tools.DataManager;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>{
    List<Transactions> transactionList;
    Context ctx;

    public TransactionAdapter(List<Transactions> transactionList, Context ctx) {
        this.transactionList = transactionList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_list_item, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transactions transactions = transactionList.get(position);
        holder.title.setText(transactions.getDescription());
        // Desired format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        // Format the date
        String formattedDate = dateFormat.format(transactions.getCreated_at());
        holder.date.setText(formattedDate);
        holder.amount.setText(formatAmount(transactions.getAmount()));
        holder.transactionStatus.setText(String.valueOf(transactions.getStatus()));

        switch (transactions.getStatus()){
            case successful:
                holder.transactionStatus.setTextColor(ContextCompat.getColor(ctx, R.color.green));
                break;
            case pending:
                holder.transactionStatus.setTextColor(ContextCompat.getColor(ctx, R.color.yellow));
                break;
            case failed:
                holder.transactionStatus.setTextColor(ContextCompat.getColor(ctx, R.color.red));
                break;
        }

        if (transactions.getType().equals(TransactionType.Bill_payment)){
            if (transactions.getSender_id() == DataManager.getInstance().getUsers().getUser_id()){
                holder.title.setText(transactions.getDescription() + " payment to " + transactions.getReceiver_name());
                holder.amount.setText("-"+formatAmount(transactions.getAmount()));
            }else if (transactions.getReceiver_id() == DataManager.getInstance().getUsers().getUser_id()){
                holder.title.setText(transactions.getDescription() + " payment from " + transactions.getSender_name());
                holder.amount.setText("+"+formatAmount(transactions.getAmount()));
                holder.amount.setTextColor(ContextCompat.getColor(ctx, R.color.primary_100));
            }
        }else {
            if (transactions.getSender_id() == DataManager.getInstance().getUsers().getUser_id()){
                holder.title.setText("Transfer to " + transactions.getReceiver_name());
                holder.amount.setText("-"+formatAmount(transactions.getAmount()));
            }else if (transactions.getReceiver_id() == DataManager.getInstance().getUsers().getUser_id()){
                holder.title.setText("Transfer from " + transactions.getSender_name());
                holder.amount.setText("+"+formatAmount(transactions.getAmount()));
                holder.amount.setTextColor(ContextCompat.getColor(ctx, R.color.primary_100));
            }
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public void updateData(List<Transactions> newTransactions) {
        if (newTransactions != null) {
            transactionList = newTransactions;
        }
        notifyDataSetChanged();
    }


    static class TransactionViewHolder extends RecyclerView.ViewHolder{
        TextView title, date, amount, transactionStatus;
        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.listTransactionName);
            date = itemView.findViewById(R.id.listTransactionDate);
            amount = itemView.findViewById(R.id.listTransactionAmount);
            transactionStatus = itemView.findViewById(R.id.listTransactionStatus);
        }

    }
}
