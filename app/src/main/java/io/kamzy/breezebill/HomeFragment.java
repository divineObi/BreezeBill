package io.kamzy.breezebill;

import static io.kamzy.breezebill.tools.Tools.baseURL;
import static io.kamzy.breezebill.tools.Tools.client;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import io.kamzy.breezebill.SharedViewModels.TokenSharedViewModel;
import io.kamzy.breezebill.SharedViewModels.UserSharedviewModel;
import io.kamzy.breezebill.SharedViewModels.VANShareViewModel;
import io.kamzy.breezebill.SharedViewModels.WalletSharedviewModel;
import io.kamzy.breezebill.adapters.UserBillAdapter;
import io.kamzy.breezebill.models.Bills;
import io.kamzy.breezebill.models.Virtual_account;
import io.kamzy.breezebill.tools.DataManager;
import io.kamzy.breezebill.tools.GsonHelper;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    TabLayout tabLayout;
    RecyclerView recyclerView;
    UserBillAdapter userBillAdapter;
    List<Bills> bills;
    ImageView hideBalance, notificationButton;
    TextView balanceAmount, greeting, accountNumber, transactionHistory;
    String CURRENT_BALANCE;
    boolean isBalanceHidden = false; // Track balance state
    UserSharedviewModel userSharedviewModel;
    WalletSharedviewModel walletSharedviewModel;
    TokenSharedViewModel tokenSharedViewModel;
    VANShareViewModel vanShareViewModel;
    ImageButton addFunds, createBill, createGroup, transfer;
    // Resource IDs for clarity
    final int HIDDEN_BALANCE_ICON = R.drawable.hide_balance;
    final int VISIBLE_BALANCE_ICON = R.drawable.eye;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tokenSharedViewModel = new ViewModelProvider(requireActivity()).get(TokenSharedViewModel.class);
        tokenSharedViewModel.getToken().observe(getViewLifecycleOwner(), token ->{

        greeting = view.findViewById(R.id.userGreeting);
        accountNumber = view.findViewById(R.id.UserAccountNumber);
        hideBalance = view.findViewById(R.id.hide_balance);
        balanceAmount = view.findViewById(R.id.balanceAmount);
        notificationButton = view.findViewById(R.id.notificationIcon);
        tabLayout = view.findViewById(R.id.tabLayout);
        addFunds = view.findViewById(R.id.fundWalletButton);
        transactionHistory = view.findViewById(R.id.TransactionHistoryLabel);
        createBill = view.findViewById(R.id.createBillButton);
        createGroup = view.findViewById(R.id.createGroupButton);
        transfer = view.findViewById(R.id.transferButton);
        recyclerView = view.findViewById(R.id.recyclerView);

        userSharedviewModel = new ViewModelProvider(requireActivity()).get(UserSharedviewModel.class);
        userSharedviewModel.getUserData().observe(getViewLifecycleOwner(), users -> {
            greeting.setText("Hi "+users.getFirst_name());
            getVanAPI("api/van/get/" + users.getUser_id(), token);


        walletSharedviewModel = new ViewModelProvider(requireActivity()).get(WalletSharedviewModel.class);
        walletSharedviewModel.getWalletData().observe(getViewLifecycleOwner(), wallet -> {
            balanceAmount.setText(String.valueOf(wallet.getBalance()));
            CURRENT_BALANCE = balanceAmount.getText().toString();
        });



        tabLayout.addTab(tabLayout.newTab().setText("My Bills"));
        tabLayout.addTab(tabLayout.newTab().setText("Others"));
        tabLayout.addTab(tabLayout.newTab().setText("View More"));

        // Load initial data for "My Bills"
        loadBills("My Bills");

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadBills(tab.getText().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        hideBalance.setOnClickListener(v ->{


            // Toggle the state
            isBalanceHidden = !isBalanceHidden;
            int iconResId = isBalanceHidden ? VISIBLE_BALANCE_ICON : HIDDEN_BALANCE_ICON;
            if (iconResId == VISIBLE_BALANCE_ICON){
                balanceAmount.setText("******");
            } else {
                balanceAmount.setText(CURRENT_BALANCE);
            }
            hideBalance.setImageResource(iconResId);
        });

        notificationButton.setOnClickListener(v ->{
            Intent intent = new Intent(getContext(), Notification.class);
            startActivity(intent);
        });

        createGroup.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CreateGroup.class);
            intent.putExtra("token", token);
            startActivity(intent);

        });

        createBill.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CreateBill.class);
            startActivity(intent);
        });

        transactionHistory.setOnClickListener(v ->{
            Intent intent = new Intent(getContext(), TransactionsPage.class);
            startActivity(intent);
        });

        });

        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        balanceAmount.setText(String.valueOf(DataManager.getInstance().getWallet().getBalance()));
        CURRENT_BALANCE = balanceAmount.getText().toString();
        int iconResId = isBalanceHidden ? VISIBLE_BALANCE_ICON : HIDDEN_BALANCE_ICON;
        if (iconResId == VISIBLE_BALANCE_ICON){
            balanceAmount.setText("******");
        } else {
            balanceAmount.setText(CURRENT_BALANCE);
        }
        hideBalance.setImageResource(iconResId);
    }

    // Load bills based on selected tab
    private void loadBills(String tabName) {
//        List<Bills> bills = new ArrayList<>();
//        if (tabName.equals("My Bills")) {
//            bills.add(new Bills("IFT 503 Manual", "3,000"));
//            bills.add(new Bills("Dept. Dues", "₦10,000"));
//        } else if (tabName.equals("Others")) {
//            bills.add(new Bills("Library Fine", "₦500"));
//            bills.add(new Bills("Sports Fee", "₦1,200"));
//        } else if (tabName.equals("View More")) {
//            bills.add(new Bills("Convocation Fee", "₦5,000"));
//            bills.add(new Bills("Health Fee", "₦2,500"));
//        }
//
//        // Update RecyclerView data
//        billAdapter.updateData(bills);
    }

    private void getVanAPI (String endpoint, String token){

        Request request = new Request.Builder()
                .url(baseURL + endpoint)
                .get()
                .addHeader("Authorization", "Bearer "+token)
                .build();

        new Thread(()->{
            try(Response response = client.newCall(request).execute()){
                int statusCode=response.code();
                Log.i("status code", String.valueOf(statusCode));
                String responseBody =response.body() != null ? response.body().string() : "null";
                if (response.isSuccessful()){
                    if (responseBody.equals("null")){
                        Log.i("VAN Status", "Not Found");
                    }else {
                        JSONObject jsonRespone = new JSONObject(responseBody);
                        GsonHelper gsonHelper = new GsonHelper();
                        Virtual_account VAN = gsonHelper.parseJSONtoVAN(jsonRespone.toString());
                        DataManager.getInstance().setVAN(VAN);
                        requireActivity().runOnUiThread(()->{
                            accountNumber.setText("Account Number: " + VAN.getVirtual_account_number());
                        });
                    }
                }else {
                    Log.i("VAN API Status", "Error Connecting to Wallet Endpoint");
                }

            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }


}