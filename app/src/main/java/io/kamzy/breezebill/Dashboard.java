package io.kamzy.breezebill;

import static io.kamzy.breezebill.tools.Tools.baseURL;
import static io.kamzy.breezebill.tools.Tools.client;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.kamzy.breezebill.SharedViewModels.UserBillShareViewModels;
import io.kamzy.breezebill.SharedViewModels.GroupSharedViewModel;
import io.kamzy.breezebill.SharedViewModels.TokenSharedViewModel;
import io.kamzy.breezebill.SharedViewModels.UserSharedviewModel;
import io.kamzy.breezebill.SharedViewModels.UsersGroupsSharedViewModel;
import io.kamzy.breezebill.SharedViewModels.VANShareViewModel;
import io.kamzy.breezebill.SharedViewModels.WalletSharedviewModel;
import io.kamzy.breezebill.enums.BillStatus;
import io.kamzy.breezebill.models.Groupss;
import io.kamzy.breezebill.models.UserBillsDTO;
import io.kamzy.breezebill.models.Users;
import io.kamzy.breezebill.models.Wallet;
import io.kamzy.breezebill.tools.ApiCallback;
import io.kamzy.breezebill.tools.UserBillsAPICallback;
import io.kamzy.breezebill.tools.DataManager;
import io.kamzy.breezebill.tools.GsonHelper;
import io.kamzy.breezebill.tools.UsersAPICallback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class Dashboard extends AppCompatActivity {

    Context ctx;
    BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    String token, IdNumber;
    GsonHelper gsonHelper;
    UserSharedviewModel userSharedviewModel;
    WalletSharedviewModel walletSharedviewModel;
    TokenSharedViewModel tokenSharedViewModel;
    GroupSharedViewModel groupSharedViewModel;
    UsersGroupsSharedViewModel usersGroupsSharedViewModel;
    UserBillShareViewModels userBillShareViewModels;
    VANShareViewModel vanShareViewModel;
    private Fragment homeFragment = new HomeFragment();
    private Fragment billFragment = new BillFragment();
    private Fragment groupFragment =  new GroupFragment();
    private Fragment profileFragment = new ProfileFragment();
    Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ctx = this;
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        fragmentManager = getSupportFragmentManager();
        token = getIntent().getStringExtra("token");
        IdNumber = getIntent().getStringExtra("idNumber");
        gsonHelper = new GsonHelper();

//        initialize Shared view Models
         userSharedviewModel = new ViewModelProvider(this).get(UserSharedviewModel.class);
         walletSharedviewModel = new  ViewModelProvider(this).get(WalletSharedviewModel.class);
         tokenSharedViewModel = new ViewModelProvider(this).get(TokenSharedViewModel.class);
         usersGroupsSharedViewModel = new ViewModelProvider(this).get(UsersGroupsSharedViewModel.class);
         userBillShareViewModels = new ViewModelProvider(this).get(UserBillShareViewModels.class);
         vanShareViewModel = new ViewModelProvider(this).get(VANShareViewModel.class);
         tokenSharedViewModel.setToken(token);

         DataManager.getInstance().setToken(token);

//         get & save Users Data
        getUserAPI("api/users/get-user", IdNumber, new UsersAPICallback<Users>() {
            @Override
            public void onSuccess(Users user) {
                //        get all bills, filter & save
                getUsersBillsAPI("api/bills/get-bills/"+user.getUser_id(), token, new UserBillsAPICallback<List<UserBillsDTO>>() {
                    @Override
                    public void onSuccess(List<UserBillsDTO> allBills) {;
                        List<UserBillsDTO> paidBills = new ArrayList<>();
                        List<UserBillsDTO> unpaidBills = new ArrayList<>();
                        for (UserBillsDTO bill : allBills){
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
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
        getWalletAPI("api/wallet/get_wallet", IdNumber, token);


//        get All Groups & save
        getAllGroupsAPI("api/groups/all", token, new ApiCallback<List<Groupss>>() {
            @Override
            public void onSuccess(List<Groupss> groups) {
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(ctx, "Failed to load groups", Toast.LENGTH_SHORT).show();
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            // Change status bar color
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.primary_100));
            // Optional: Set light or dark icons
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                View decor = window.getDecorView();
                int flags = decor.getSystemUiVisibility();
                if (/* Use light icons */ true) {
                    flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else {
                    flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                decor.setSystemUiVisibility(flags);
            }
        }


        // Set default fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, homeFragment, "Home")
                .commit();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, billFragment, "Bill").hide(billFragment)
                .commit();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, groupFragment, "Group").hide(groupFragment)
                .commit();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, profileFragment, "Profile").hide(profileFragment)
                .commit();

        activeFragment = homeFragment;

        walletSharedviewModel.getWalletData().observe(this, wallet -> {
            if (wallet.getCode()==null){
                Intent intent = new Intent(ctx, Passcode.class);
                intent.putExtra("token", token);
                intent.putExtra("idNumber", IdNumber);
                startActivity(intent);
            }
        });

        // Bottom Navigation
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Reset all icons to outline versions
                bottomNavigationView.getMenu().findItem(R.id.home).setIcon(R.drawable.home_outline);
                bottomNavigationView.getMenu().findItem(R.id.bills).setIcon(R.drawable.create_bill);
                bottomNavigationView.getMenu().findItem(R.id.group).setIcon(R.drawable.create_group);
                bottomNavigationView.getMenu().findItem(R.id.profile).setIcon(R.drawable.profile);
                handleNavigationItemSelected(item);
                return true;
            }
        });


    }


    // Use this:
    private boolean handleNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = getFragmentForMenuItem(item);

        if (selectedFragment != null) {
            showFragment(selectedFragment);
        }

        return true;
    }

    private Fragment getFragmentForMenuItem(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.home) {
            item.setIcon(R.drawable.home_filled);
            return homeFragment;
        } else if (itemId == R.id.bills) {
            item.setIcon(R.drawable.bill_filled);
            return billFragment;
        } else if (itemId == R.id.group) {
            item.setIcon(R.drawable.group_filled);
            return groupFragment;
        } else if (itemId == R.id.profile) {
            item.setIcon(R.drawable.profile_filled);
            return profileFragment;
        } else {
            return null;
        }
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(activeFragment).show(fragment);
        transaction.commit();
        activeFragment = fragment;
    }

    private void getUserAPI (String endpoint, String idNumber, UsersAPICallback<Users> callback){
        FormBody.Builder formbody = new FormBody.Builder()
                .add("id_number", idNumber);

        Request request = new Request.Builder()
                .url(baseURL + endpoint)
                .post(formbody.build())
                .build();

        new Thread(()->{
            try(Response response = client.newCall(request).execute()){
                int statusCode=response.code();
                Log.i("status code", String.valueOf(statusCode));
                String responseBody =response.body() != null ? response.body().string() : "null";
                if (response.isSuccessful()){
                    if (responseBody.equals("null")){
                    }else {
                        JSONObject jsonRespone = new JSONObject(responseBody);
                       Users loggedInUser = gsonHelper.parseJSONtoUsers(jsonRespone.toString());
                        if (callback != null) {
                            callback.onSuccess(loggedInUser);
                        }
                       runOnUiThread(()->{
                           userSharedviewModel.setUserData(loggedInUser);
                           DataManager.getInstance().setUsers(loggedInUser);
                           //         get Users Groups & save
                           getUsersGroupsAPI("api/groups/"+DataManager.getInstance().getUsers().getUser_id()+"/user-groups", token, new ApiCallback<List<Groupss>>() {
                               @Override
                               public void onSuccess(List<Groupss> groups) {
                               }

                               @Override
                               public void onFailure(Throwable t) {
                                   Toast.makeText(ctx, "Failed to load groups", Toast.LENGTH_SHORT).show();
                               }
                           });
                        });
                    }
                }else {

                }

            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void getWalletAPI (String endpoint, String idNumber, String token){
        FormBody.Builder formbody = new FormBody.Builder()
                .add("id_number", idNumber);

        Request request = new Request.Builder()
                .url(baseURL + endpoint)
                .post(formbody.build())
                .addHeader("Authorization", "Bearer "+token)
                .build();

        new Thread(()->{
            try(Response response = client.newCall(request).execute()){
                int statusCode=response.code();
                Log.i("status code", String.valueOf(statusCode));
                String responseBody =response.body() != null ? response.body().string() : "null";
                if (response.isSuccessful()){
                    if (responseBody.equals("null")){
                        Log.i("Wallet Status", "Not Found");
                    }else {
                        JSONObject jsonRespone = new JSONObject(responseBody);
                        Wallet userWallet = gsonHelper.parseJSONtoWallet(jsonRespone.toString());
                        runOnUiThread(()->{
                            walletSharedviewModel.setWalletData(userWallet);
                            DataManager.getInstance().setWallet(userWallet);
                        });
                    }
                }else {
                    Log.i("Wallet API Status", "Error Connecting to Wallet Endpoint");
                }

            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void getAllGroupsAPI(String endpoint, String token, ApiCallback<List<Groupss>> callback){

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
                        Log.i("Group Status", "No group found");
                    }else {
                        Log.i("Groups", responseBody);
                        JSONArray jsonRespone = new JSONArray(responseBody);
                        GsonHelper gsonHelper1 = new GsonHelper();
                        List<Groupss> allGroups = gsonHelper1.parseJSONArrayToListGroups(String.valueOf(jsonRespone));
                        if (callback != null) {
                            callback.onSuccess(allGroups);
                        }
                        runOnUiThread(()->{
                            DataManager.getInstance().setAllGroups(allGroups);
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

    public void getUsersGroupsAPI(String endpoint, String token, ApiCallback<List<Groupss>> callback){

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
                        GsonHelper gsonHelper1 = new GsonHelper();
                        List<Groupss> usersGroups = gsonHelper1.parseJSONArrayToListGroups(String.valueOf(jsonRespone));
                        if (callback != null) {
                            callback.onSuccess(usersGroups);
                        }
                        runOnUiThread(()->{
                            DataManager.getInstance().setUsersGroups(usersGroups);
                            usersGroupsSharedViewModel.setUsersGroupsList(usersGroups);
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

    public void getUsersBillsAPI(String endpoint, String token, UserBillsAPICallback<List<UserBillsDTO>> callback){

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
                        List<UserBillsDTO> userBills = gsonHelper1.parseJSONArrayToListUserBills(String.valueOf(jsonRespone));
                        if (callback != null) {
                            callback.onSuccess(userBills);
                        }
                        runOnUiThread(()->{
                            DataManager.getInstance().setAllBills(userBills);
                            userBillShareViewModels.setBillData(userBills);
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