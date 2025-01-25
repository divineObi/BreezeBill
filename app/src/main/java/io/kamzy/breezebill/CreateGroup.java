package io.kamzy.breezebill;

import static io.kamzy.breezebill.tools.Tools.baseURL;
import static io.kamzy.breezebill.tools.Tools.client;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import io.kamzy.breezebill.enums.GroupType;
import io.kamzy.breezebill.models.Groupss;
import io.kamzy.breezebill.tools.DataManager;
import io.kamzy.breezebill.tools.GsonHelper;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateGroup extends AppCompatActivity {
    Context ctx;
    TextInputLayout groupNameInput, groupDescriptionInput, groupTypeInput, groupParentInput;
    EditText groupNameEditText, groupDescriptionEditText;
    AutoCompleteTextView groupTypeEditText, groupParentEditText;
    String[] groupParentList;
    Button createGroupButton;
    ImageView closeButton;
    String groupName, description, groupType, parentGroup, token;
    Dashboard dashboard;
    GsonHelper gsonHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_group);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ctx = this;
        token = getIntent().getStringExtra("token");
        groupNameInput = findViewById(R.id.groupNameInputLayout);
        groupDescriptionInput = findViewById(R.id.groupDescriptionInputLayout);
        groupTypeInput = findViewById(R.id.groupTypeInputLayout);
        groupParentInput = findViewById(R.id.groupParentInputLayout);
        groupNameEditText = findViewById(R.id.groupNameEditText);
        groupDescriptionEditText = findViewById(R.id.groupDescriptionEditText);
        groupTypeEditText = findViewById(R.id.groupTypeEditText);
        groupParentEditText = findViewById(R.id.groupParentEditText);
        createGroupButton = findViewById(R.id.createGroupButton);
        closeButton = findViewById(R.id.create_group_back_button);
        dashboard = new Dashboard();
        gsonHelper = new GsonHelper();
        String[] groupTypeList = {String.valueOf(GroupType.Super), String.valueOf(GroupType.Sub)};



        ArrayAdapter<String> groupTypeAdapter = new ArrayAdapter<>(
                ctx,
                android.R.layout.simple_dropdown_item_1line,
                groupTypeList
        );
        groupTypeEditText.setAdapter(groupTypeAdapter);

        List<Groupss> groupData = DataManager.getInstance().getAllGroups();
            if (groupData != null){
                groupParentList = new String[groupData.size()];
                for (int i = 0; i < groupData.size(); i++) {
                    groupParentList[i] = groupData.get(i).getGroup_name();
                }
                ArrayAdapter<String> parentGroupAdapter = new ArrayAdapter<>(
                        ctx,
                        android.R.layout.simple_dropdown_item_1line,
                        groupParentList
                );
                groupParentEditText.setAdapter(parentGroupAdapter);
            }else {
                Log.i("Parent Group List", "No groups found");
            }


        groupTypeEditText.setOnItemClickListener((parent, view, position, id) -> {
            String selectedGroupType = (String) parent.getItemAtPosition(position);
            if (selectedGroupType.equals("Super")){
                groupParentInput.setVisibility(View.GONE);
            }else {
                groupParentInput.setVisibility(View.VISIBLE);
            }
        });

        closeButton.setOnClickListener(v -> finish());

//        button click listener
        createGroupButton.setOnClickListener(v -> {
            groupName = groupNameEditText.getText().toString();
            description = groupDescriptionEditText.getText().toString();
            groupType = groupTypeEditText.getText().toString();

            if (groupParentEditText.getVisibility()==View.VISIBLE){
                parentGroup = groupParentEditText.getText().toString();
            } else {
                parentGroup = "null";
            }

            if (groupName.isEmpty()) groupNameInput.setError("Group name is required");
            if (description.isEmpty()) groupDescriptionInput.setError("Group description is required");
            if (groupType.isEmpty()) groupTypeInput.setError("Group type is required");
            if (groupType.equals("Sub") && parentGroup.isEmpty()) groupParentInput.setError("Parent group is required");

            try {
                createGroupAPI("api/groups/create", token, groupName, description, groupType, parentGroup, DataManager.getInstance().getUsers().getUser_id());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        });


    }

    private void createGroupAPI(String endpoint, String token, String groupName, String description, String groupType, String parentGroup, int user_id) throws JSONException {
            JSONObject requestParams =  new JSONObject();
                        requestParams.put("group_name", groupName)
                        .put("description", description)
                        .put("created_by", user_id)
                        .put("group_type", groupType);

            if (!parentGroup.equals("null")){
                    requestParams.put("parent_group", parentGroup);
            }

            RequestBody requestBody = RequestBody.create(
                    requestParams.toString(), MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(baseURL+endpoint)
                    .post(requestBody)
                    .addHeader("Authorization", "Bearer "+token)
                    .build();

            new Thread(()->{
                try(Response response = client.newCall(request).execute()){
                    int statusCode = response.code();
                    Log.i("statusCode", String.valueOf(statusCode));
                    String responseBody = response.body().string();
                    if (response.isSuccessful()){
                        JSONObject JSONresponseBody = new JSONObject(responseBody);
                        String GroupStatus = JSONresponseBody.getString("status");
                        runOnUiThread(()->{
                            switch (GroupStatus){
                                case "Group name already exists":
                                    groupNameEditText.setError(GroupStatus);
                                    break;
                                case "Group created successfully":
                                    Toast.makeText(ctx, GroupStatus, Toast.LENGTH_LONG).show();
                                    dashboard.getAllGroupsAPI("api/groups/all", token, new ApiCallback<List<Groupss>>() {
                                        @Override
                                        public void onSuccess(List<Groupss> groups) {
                                        }

                                        @Override
                                        public void onFailure(Throwable t) {
                                            Toast.makeText(ctx, "Failed to load groups", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    getUsersGroupsAPI("api/groups/"+DataManager.getInstance().getUsers().getUser_id()+"/user-groups", token);
                                    finish();
                                    break;
                                case "Error creating Group":
                                    Toast.makeText(ctx, GroupStatus+ ", Try again", Toast.LENGTH_LONG).show();
                                    break;
                            }
                        });
                    } else {
                        Log.i("Create Group API Status", "Error Reaching Group Endpoint");
                    }
                } catch (IOException | JSONException e) {
                    throw new RuntimeException(e);
                }
            }).start();


    }

    public void getUsersGroupsAPI(String endpoint, String token){

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
                    }else {
                        Log.i("Groups", responseBody);
                        JSONArray jsonRespone = new JSONArray(responseBody);
                        List<Groupss> usersGroups = gsonHelper.parseJSONArrayToListGroups(String.valueOf(jsonRespone));
                        runOnUiThread(()->{
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
}