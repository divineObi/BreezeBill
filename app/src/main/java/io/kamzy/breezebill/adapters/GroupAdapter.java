package io.kamzy.breezebill.adapters;

import static io.kamzy.breezebill.tools.Tools.baseURL;
import static io.kamzy.breezebill.tools.Tools.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.kamzy.breezebill.ApiCallback;
import io.kamzy.breezebill.Dashboard;
import io.kamzy.breezebill.GroupFragment;
import io.kamzy.breezebill.R;
import io.kamzy.breezebill.enums.BillStatus;
import io.kamzy.breezebill.models.Bills;
import io.kamzy.breezebill.models.Groupss;
import io.kamzy.breezebill.tools.BillsAPICallback;
import io.kamzy.breezebill.tools.DataManager;
import io.kamzy.breezebill.tools.GroupAPICallback;
import io.kamzy.breezebill.tools.GsonHelper;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.groupViewHolder>{
    Context ctx;
    private List<Groupss> groupList;
    private String listType;// "MY_GROUPS" or "EXPLORE_GROUPS"
    GroupFragment groupFragment = new GroupFragment();

    public GroupAdapter(Context ctx, List<Groupss> groupList, String listType) {
        this.ctx = ctx;
        this.groupList = groupList;
        this.listType = listType;
    }

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
            holder.actionButton.setOnClickListener(v -> {

                LayoutInflater inflater = LayoutInflater.from(ctx);
                View customView = inflater.inflate(R.layout.group_button_dialogue, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setView(customView);

                TextView title = customView.findViewById(R.id.dialogTitle);
                TextView message = customView.findViewById(R.id.dialogMessage);
                Button positiveButton = customView.findViewById(R.id.positiveButton);
                Button negativeButton = customView.findViewById(R.id.negativeButton);

                title.setText("Leave Group");
                message.setText("Exit " + groupss.getGroup_name());

                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);

                positiveButton.setOnClickListener(v1 -> {
                    dialog.dismiss();
                    leaveGroupAPI("api/groups/" + groupss.getGroup_id() + "/remove-member", DataManager.getInstance().getUsers().getUser_id(), new GroupAPICallback() {
                        @Override
                        public void onSuccess(String status) {
                            getUsersGroupsAPI("api/groups/"+DataManager.getInstance().getUsers().getUser_id()+"/user-groups", DataManager.getInstance().getToken(), new ApiCallback<List<Groupss>>() {
                                @Override
                                public void onSuccess(List<Groupss> groups) {
                                    //        get all bills, filter & save
                                   getUsersBillsAPI("api/bills/get-bills/"+DataManager.getInstance().getUsers().getUser_id(), DataManager.getInstance().getToken(), new BillsAPICallback<List<Bills>>() {
                                        @Override
                                        public void onSuccess(List<Bills> allBills) {;
                                            List<Bills> paidBills = new ArrayList<>();
                                            List<Bills> unpaidBills = new ArrayList<>();
                                            for (Bills bill : allBills){
                                                if (bill.getStatus().equals(BillStatus.paid)){
                                                    paidBills.add(bill);
                                                }else {
                                                    unpaidBills.add(bill);
                                                }

                                                if (!paidBills.isEmpty()){
                                                    DataManager.getInstance().setPaidBills(paidBills);
                                                }else {
                                                    DataManager.getInstance().setPaidBills(null);
                                                }

                                                if (!unpaidBills.isEmpty()){
                                                    DataManager.getInstance().setUnpaidBills(unpaidBills);
                                                }else {
                                                    DataManager.getInstance().setUnpaidBills(null);
                                                }
                                                Log.i("All Bills", allBills.toString());
                                                Log.i("Paid Bills", paidBills.toString());
                                                Log.i("Unpaid Bills", unpaidBills.toString());
                                            }

                                        }

                                        @Override
                                        public void onFailure(Throwable t) {

                                        }
                                    });

                                    ((Activity) ctx).runOnUiThread(() -> {
                                        // Update the RecyclerView
                                        groupList = groups;
                                        notifyDataSetChanged();
                                        groupFragment.loadGroups("MY_GROUPS");
                                    });
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    Toast.makeText(ctx, "Failed to load groups", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                        @Override
                        public void onFailure(Throwable t) {

                        }
                    });
//

                });

                negativeButton.setOnClickListener(v1 -> {
                    dialog.dismiss();
                });

                dialog.show();


            });
        }
        else if ("EXPLORE_GROUPS".equals(listType)) {
            holder.actionButton.setText("Join");
            holder.actionButton.setBackgroundTintList(
                    ColorStateList.valueOf(holder.itemView.getContext().getResources().getColor(R.color.primary_100))
            );
            holder.actionButton.setOnClickListener(v -> {

                LayoutInflater inflater = LayoutInflater.from(ctx);
                View customView = inflater.inflate(R.layout.group_button_dialogue, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setView(customView);

                TextView title = customView.findViewById(R.id.dialogTitle);
                TextView message = customView.findViewById(R.id.dialogMessage);
                Button positiveButton = customView.findViewById(R.id.positiveButton);
                Button negativeButton = customView.findViewById(R.id.negativeButton);

                title.setText("Join Group");
                message.setText("Join " + groupss.getGroup_name());

                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                positiveButton.setOnClickListener(v1 -> {
                    dialog.dismiss();
                    joinGroupAPI("api/groups/" + groupss.getGroup_id() + "/add-member", DataManager.getInstance().getUsers().getUser_id(), new GroupAPICallback() {
                        @Override
                        public void onSuccess(String status) {
                            getUsersGroupsAPI("api/groups/"+DataManager.getInstance().getUsers().getUser_id()+"/user-groups", DataManager.getInstance().getToken(), new ApiCallback<List<Groupss>>() {
                                @Override
                                public void onSuccess(List<Groupss> groups) {
                                    //        get all bills, filter & save
                                    getUsersBillsAPI("api/bills/get-bills/"+DataManager.getInstance().getUsers().getUser_id(), DataManager.getInstance().getToken(), new BillsAPICallback<List<Bills>>() {
                                        @Override
                                        public void onSuccess(List<Bills> allBills) {;
                                            List<Bills> paidBills = new ArrayList<>();
                                            List<Bills> unpaidBills = new ArrayList<>();
                                            for (Bills bill : allBills){
                                                if (bill.getStatus().equals(BillStatus.paid)){
                                                    paidBills.add(bill);
                                                }else {
                                                    unpaidBills.add(bill);
                                                }

                                                if (!paidBills.isEmpty()){
                                                    DataManager.getInstance().setPaidBills(paidBills);
                                                }else {
                                                    DataManager.getInstance().setPaidBills(null);
                                                }

                                                if (!unpaidBills.isEmpty()){
                                                    DataManager.getInstance().setUnpaidBills(unpaidBills);
                                                }else {
                                                    DataManager.getInstance().setUnpaidBills(null);
                                                }
                                                Log.i("All Bills", allBills.toString());
                                                Log.i("Paid Bills", paidBills.toString());
                                                Log.i("Unpaid Bills", unpaidBills.toString());
                                            }

                                        }

                                        @Override
                                        public void onFailure(Throwable t) {

                                        }
                                    });
                                    groupFragment.loadGroups("EXPLORE_GROUPS");
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    Toast.makeText(ctx, "Failed to load groups", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(Throwable t) {

                        }
                    });


                });

                negativeButton.setOnClickListener(v1 -> {
                    dialog.dismiss();
                });

                dialog.show();

            });
        }

        }
    @Override
    public int getItemCount() {
        return groupList.size();
    }



    public void updateData(List<Groupss> newGroupss, String SelectedTab) {
        if (newGroupss != null) {
            groupList = newGroupss;
        }
        this.listType = SelectedTab;
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




    public void joinGroupAPI (String endpoint, int userID, GroupAPICallback callback){
        FormBody.Builder formbody = new FormBody.Builder()
                .add("user_id", String.valueOf(userID));

        Request request = new Request.Builder()
                .url(baseURL + endpoint)
                .post(formbody.build())
                .addHeader("Authorization", "Bearer "+ DataManager.getInstance().getToken())
                .build();

        new Thread(()->{
            try(Response response = client.newCall(request).execute()){
                int statusCode=response.code();
                Log.i("status code", String.valueOf(statusCode));
                String responseBody = response.body().string();
                if (response.isSuccessful()){
                    JSONObject jsonRespone = new JSONObject(responseBody);
                    String status = jsonRespone.getString("status");
                    if (callback != null) {
                        callback.onSuccess(status);
                    }
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> Toast.makeText(ctx, status, Toast.LENGTH_LONG).show());
                }else {
                    Log.i("Wallet API Status", "Error Connecting to Wallet Endpoint");
                }

            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void leaveGroupAPI (String endpoint, int userID, GroupAPICallback callback){
        FormBody.Builder formbody = new FormBody.Builder()
                .add("user_id", String.valueOf(userID));

        Request request = new Request.Builder()
                .url(baseURL + endpoint)
                .delete(formbody.build())
                .addHeader("Authorization", "Bearer "+ DataManager.getInstance().getToken())
                .build();

        new Thread(()->{
            try(Response response = client.newCall(request).execute()){
                int statusCode=response.code();
                Log.i("status code", String.valueOf(statusCode));
                String responseBody = response.body().string();
                if (response.isSuccessful()){
                    JSONObject jsonRespone = new JSONObject(responseBody);
                    String status = jsonRespone.getString("status");
                    if (callback != null) {
                        callback.onSuccess(status);
                    }
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> Toast.makeText(ctx, status, Toast.LENGTH_LONG).show());
                }else {
                    Log.i("Wallet API Status", "Error Connecting to Wallet Endpoint");
                }

            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public static void getUsersGroupsAPI(String endpoint, String token, ApiCallback<List<Groupss>> callback){

        Request request = new Request.Builder()
                .url(baseURL + endpoint)
                .get()
                .addHeader("Authorization", "Bearer "+token)
                .build();

        new Thread(()->{
            try(Response response = client.newCall(request).execute()){
                int statusCode=response.code();
                Log.i("status code", String.valueOf(statusCode));
                String responseBody = response.body().string();
                if (response.isSuccessful()){
                    if (responseBody.equals("[]")){
                        Log.i("Users Group Status", "No group found");
                        DataManager.getInstance().setUsersGroups(null);
                        if (callback != null) {
                            callback.onSuccess(new ArrayList<>());
                        }
                    }else {
                        Log.i("Groups", responseBody);
                        JSONArray jsonRespone = new JSONArray(responseBody);
                        GsonHelper gsonHelper1 = new GsonHelper();
                        List<Groupss> usersGroups = gsonHelper1.parseJSONArrayToListGroups(String.valueOf(jsonRespone));
                        if (callback != null) {
                            callback.onSuccess(usersGroups);
                        }
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(() -> {
                            DataManager.getInstance().setUsersGroups(usersGroups);
                        });


                    }
                }else {
                    Log.i("Group API status", "Error: Couldn't reach group Endpoint");
                }

            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void getUsersBillsAPI(String endpoint, String token, BillsAPICallback<List<Bills>> callback){

        Request request = new Request.Builder()
                .url(baseURL + endpoint)
                .get()
                .addHeader("Authorization", "Bearer "+token)
                .build();

        new Thread(()->{
            try(Response response = client.newCall(request).execute()){
                int statusCode=response.code();
                Log.i("status code", String.valueOf(statusCode));
                String responseBody = response.body().string();
                if (response.isSuccessful()){
                    if (responseBody.equals("[]")){
                        Log.i("User's Bills Status", "No Bill found");
                    }else {
                        Log.i("Bills", responseBody);
                        JSONArray jsonRespone = new JSONArray(responseBody);
                        GsonHelper gsonHelper1 = new GsonHelper();
                        List<Bills> userBills = gsonHelper1.parseJSONArrayToListBills(String.valueOf(jsonRespone));
                        if (callback != null) {
                            callback.onSuccess(userBills);
                        }
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(() -> {
                            DataManager.getInstance().setAllBills(userBills);
                        });
                    }
                }else {
                    Log.i("Bills API status", "Error: Couldn't reach Bill Endpoint");
                }

            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

}
