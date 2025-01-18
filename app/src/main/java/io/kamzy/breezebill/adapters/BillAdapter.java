package io.kamzy.breezebill.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.kamzy.breezebill.Dashboard;
import io.kamzy.breezebill.R;
import io.kamzy.breezebill.models.Bills;

public class BillAdapter extends  RecyclerView.Adapter<BillAdapter.BillViewHolder> {
    private List<Bills> bills;
    Context context;

    public BillAdapter(List<Bills> bills, Context context) {
        this.bills = bills;
        this.context = context;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_item, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {

        Bills bill = bills.get(position);
        holder.billNameTextView.setText(bill.getBill_name());
        holder.billAmountTextView.setText((String.valueOf(bill.getAmount())));
        holder.payButton.setOnClickListener(v -> {
            Toast.makeText(context, "Pay Button Clicked", Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    public int getItemCount() {
        return bills.size();
    }

    public void updateData(List<Bills> newBills) {
        bills = newBills;
        notifyDataSetChanged();
    }

    static class BillViewHolder extends RecyclerView.ViewHolder{
        TextView billNameTextView, billAmountTextView;
        Button payButton;

        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            billNameTextView = itemView.findViewById(R.id.billName);
            billAmountTextView = itemView.findViewById(R.id.billAmount);
            payButton = itemView.findViewById(R.id.payButton);

        }
    }
}
