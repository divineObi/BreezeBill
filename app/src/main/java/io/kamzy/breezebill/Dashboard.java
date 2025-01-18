package io.kamzy.breezebill;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import io.kamzy.breezebill.adapters.BillAdapter;
import io.kamzy.breezebill.models.Bills;

public class Dashboard extends AppCompatActivity {

    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private BillAdapter billAdapter;
    Context ctx;
    List<Bills> bills;
    ImageView hideBalance;
    TextView balanceAmount;
    String CURRENT_BALANCE;
    boolean isBalanceHidden = false; // Track balance state

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
        hideBalance = findViewById(R.id.hide_balance);
        balanceAmount = findViewById(R.id.balanceAmount);
        CURRENT_BALANCE = balanceAmount.getText().toString();
        tabLayout = findViewById(R.id.tabLayout);
        recyclerView = findViewById(R.id.recyclerView);

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

        // Set up RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ctx);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        billAdapter = new BillAdapter(bills, ctx);
        recyclerView.setAdapter(billAdapter);

//       Add a DividerItemDecoration to control spacing
        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(divider);

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

            // Resource IDs for clarity
            final int HIDDEN_BALANCE_ICON = R.drawable.hide_balance;
            final int VISIBLE_BALANCE_ICON = R.drawable.eye;
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

    }

    // Load bills based on selected tab
    private void loadBills(String tabName) {
        List<Bills> bills = new ArrayList<>();
        if (tabName.equals("My Bills")) {
            bills.add(new Bills("IFT 503 Manual", "3,000"));
            bills.add(new Bills("Dept. Dues", "₦10,000"));
        } else if (tabName.equals("Others")) {
            bills.add(new Bills("Library Fine", "₦500"));
            bills.add(new Bills("Sports Fee", "₦1,200"));
        } else if (tabName.equals("View More")) {
            bills.add(new Bills("Convocation Fee", "₦5,000"));
            bills.add(new Bills("Health Fee", "₦2,500"));
        }

        // Update RecyclerView data
        billAdapter.updateData(bills);
    }
}