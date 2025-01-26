package io.kamzy.breezebill.adapters;

import static io.kamzy.breezebill.CreateBill.formatAmount;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.kamzy.breezebill.Payment;
import io.kamzy.breezebill.R;
import io.kamzy.breezebill.models.UserBillsDTO;
import io.kamzy.breezebill.tools.DataManager;

public class UserBillAdapter extends  RecyclerView.Adapter<UserBillAdapter.BillViewHolder> {
    private List<UserBillsDTO> bills;
    Context context;
    private String listType;

    public UserBillAdapter(Context context, List<UserBillsDTO> bills, String listType) {
        this.context = context;
        this.bills = bills;
        this.listType = listType;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_item, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {

        UserBillsDTO bill = bills.get(position);
        holder.billNameTextView.setText(bill.getBill_name());
        holder.billAmountTextView.setText(formatAmount(bill.getUnit_amount()));

        if (listType.equals("ACTIVE")){
            holder.payButton.setOnClickListener(v -> {
                Toast.makeText(context, "Pay Button Clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, Payment.class);
                intent.putExtra("payment_type", "Bill Payment");
                intent.putExtra("bill_id", String.valueOf(bill.getBill_id()));
                intent.putExtra("amount", String.valueOf(bill.getUnit_amount()));
                intent.putExtra("remark", bill.getBill_name());
                intent.putExtra("payment_account", bill.getPayment_account());
                Handler handler = new Handler(Looper.getMainLooper());
                context.startActivity(intent);
            });
        }else {
            holder.payButton.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return bills.size();
    }

    public void updateData(List<UserBillsDTO> newBills, String listType) {
        if (newBills != null) {
            bills = newBills;
        }else {
            bills = new ArrayList<>();
        }
        this.listType = listType;
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
