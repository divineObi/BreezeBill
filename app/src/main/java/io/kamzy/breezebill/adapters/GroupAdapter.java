package io.kamzy.breezebill.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.kamzy.breezebill.R;
import io.kamzy.breezebill.models.Bills;
import io.kamzy.breezebill.models.Groupss;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.groupViewHolder>{
    Context ctx;
    private List<Groupss> groupList;
    private String listType; // "MY_GROUPS" or "EXPLORE_GROUPS"
    private OnButtonClickListener buttonClickListener;

    @NonNull
    @Override
    public groupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list_item, parent, false);
      return new groupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull groupViewHolder holder, int position) {
        Groupss groupss = groupList.get(position);
        holder.groupName.setText(groupss.getGroup_name());
        holder.groupDescription.setText(groupss.getDescription());

        // Set button text and logic dynamically
        if ("MY_GROUPS".equals(listType)) {
            holder.actionButton.setText("Exit");
            holder.actionButton.setBackgroundTintList(
                    ColorStateList.valueOf(holder.itemView.getContext().getResources().getColor(R.color.red))
            );
            holder.actionButton.setOnClickListener(v -> buttonClickListener.onButtonClick(groupss));
        } else if ("EXPLORE_GROUPS".equals(listType)) {
            holder.actionButton.setText("Join");
            holder.actionButton.setBackgroundTintList(
                    ColorStateList.valueOf(holder.itemView.getContext().getResources().getColor(R.color.primary_100))
            );
            holder.actionButton.setOnClickListener(v -> buttonClickListener.onButtonClick(groupss));
        }

        }
    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public interface OnButtonClickListener {
        void onButtonClick(Groupss groupss);
    }

    public GroupAdapter(Context ctx, List<Groupss> groupList, String listType, OnButtonClickListener buttonClickListener) {
        this.ctx = ctx;
        this.groupList = groupList;
        this.listType = listType;
        this.buttonClickListener = buttonClickListener;
    }

    public void updateData(List<Groupss> newGroupss) {
        groupList = newGroupss;
        notifyDataSetChanged();
    }
    static class groupViewHolder  extends RecyclerView.ViewHolder{
        TextView groupName, groupDescription;
        Button actionButton;
        public groupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.listGroupName);
            groupDescription = itemView.findViewById(R.id.listGroupdescription);
            actionButton = itemView.findViewById(R.id.groupActionButton);
        }
    }
}
