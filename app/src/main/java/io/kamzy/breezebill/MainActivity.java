package io.kamzy.breezebill;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {
    TextInputEditText idNumberEditText, passwordEditText;
    TextView signUpTextButton, forgotPassword;
    CheckBox rememberMe;
    MaterialButton loginbutton;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ctx = this;
        idNumberEditText = findViewById(R.id.IdNumberEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signUpTextButton = findViewById(R.id.signup_text_button);
        forgotPassword = findViewById(R.id.forgotPassword);
        rememberMe = findViewById(R.id.rememberMeCheckbox);
        loginbutton = findViewById(R.id.loginButton);

        loginbutton.setOnClickListener(v -> {
            String idNumber = idNumberEditText.getText().toString();
            String password = passwordEditText.getText().toString();

//                perform login Logic
            Intent intent = new Intent(ctx, Profile.class);
            startActivity(intent);
        });

        signUpTextButton.setOnClickListener(v -> {
            Intent intent = new Intent(ctx, Signup.class);
            startActivity(intent);
        });

    }
}