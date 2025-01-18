package io.kamzy.breezebill;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Groups extends AppCompatActivity {
    Context ctx;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_groups);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ctx = this;
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Bottom Navigation
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.home) {
                    // Handle home tab
                    Intent intent = new Intent(ctx, Dashboard.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.bills) {
                    // Handle bills tab
                    return true;
                } else if (itemId == R.id.group) {
                    // Handle group tab
                    Intent intent = new Intent(ctx, Groups.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.profile) {
                    // Handle Profile tab
                    return true;
                }
                return false;
            }
        });
    }
}