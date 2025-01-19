package io.kamzy.breezebill;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import io.kamzy.breezebill.adapters.BillAdapter;
import io.kamzy.breezebill.models.Bills;

public class Dashboard extends AppCompatActivity {

    Context ctx;
    BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    String token;

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
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();


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
            return new HomeFragment();
        } else if (itemId == R.id.bills) {
            return new BillFragment();
        } else if (itemId == R.id.group) {
            return new GroupFragment();
        } else if (itemId == R.id.profile) {
            item.setIcon(R.drawable.profile_filled);
            return new ProfileFragment();
        } else {
            return null;
        }
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

}