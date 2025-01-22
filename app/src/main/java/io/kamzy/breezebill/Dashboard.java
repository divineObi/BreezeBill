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
import java.util.List;

import io.kamzy.breezebill.SharedViewModels.GroupSharedViewModel;
import io.kamzy.breezebill.SharedViewModels.TokenSharedViewModel;
import io.kamzy.breezebill.SharedViewModels.UserSharedviewModel;
import io.kamzy.breezebill.SharedViewModels.WalletSharedviewModel;
import io.kamzy.breezebill.models.Groupss;
import io.kamzy.breezebill.models.Users;
import io.kamzy.breezebill.models.Wallet;
import io.kamzy.breezebill.tools.DataManager;
import io.kamzy.breezebill.tools.GsonHelper;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class Dashboard extends AppCompatActivity {

    Context ctx;
    BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    String token, IdNumber;
    static GsonHelper gsonHelper;
    UserSharedviewModel userSharedviewModel;
    WalletSharedviewModel walletSharedviewModel;
    TokenSharedViewModel tokenSharedViewModel;
    GroupSharedViewModel groupSharedViewModel;
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
         groupSharedViewModel = new ViewModelProvider(this).get(GroupSharedViewModel.class);
         tokenSharedViewModel = new ViewModelProvider(this).get(TokenSharedViewModel.class);
         tokenSharedViewModel.setToken(token);

//         get & save Users Data
        getUserAPI("api/users/get-user", IdNumber);
        getWalletAPI("api/wallet/get_wallet", IdNumber, token);

//        get All Groups & save
        getAllGroupsAPI("api/groups/all", token);

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
            return billFragment;
        } else if (itemId == R.id.group) {
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

    private void getUserAPI (String endpoint, String idNumber){
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
                       runOnUiThread(()->{
                           userSharedviewModel.setUserData(loggedInUser);
                           DataManager.getInstance().setUsers(loggedInUser);
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

    public static void getAllGroupsAPI(String endpoint, String token){

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
                        List<Groupss> allGroups = gsonHelper.parseJSONArrayToListGroups(String.valueOf(jsonRespone));
                        DataManager.getInstance().setAllGroups(allGroups);
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