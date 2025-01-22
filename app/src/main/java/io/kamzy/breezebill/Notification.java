package io.kamzy.breezebill;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Notification extends AppCompatActivity {
    Context ctx;
    ImageView close, no_notification_image;
    TextView notification_message, notification_subtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ctx = this;
        close = findViewById(R.id.notification_back_button);
        no_notification_image = findViewById(R.id.no_notifications_icon);
        notification_message = findViewById(R.id.no_notifications_message);
        notification_subtext = findViewById(R.id.no_notifications_subtext);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            // Optional: Set light or dark icons
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                View decor = window.getDecorView();
                int flags = decor.getSystemUiVisibility();
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                decor.setSystemUiVisibility(flags);
            }
        }

        boolean hasNotification = false;
        close.setOnClickListener(v -> finish());

        if (hasNotification){
            no_notification_image.setVisibility(View.GONE);
            notification_message.setVisibility(View.GONE);
            notification_subtext.setVisibility(View.GONE);
        }else{
            no_notification_image.setVisibility(View.VISIBLE);
            notification_message.setVisibility(View.VISIBLE);
            notification_subtext.setVisibility(View.VISIBLE);
        }

    }
}