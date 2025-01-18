package io.kamzy.breezebill;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class Signup extends AppCompatActivity {
    TextInputEditText FirstNameEditText, LastNameEditText, IdNumberEditText, EmailEditText, passwordEditText, confirmPasswordEditText;
    String FirstName, LastName, IdNumber, Email, password, confirmPassword;
    CheckBox rememberMe;
    MaterialButton signupButton;
    TextView loginTextButton;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ctx = this;
        FirstNameEditText = findViewById(R.id.FirstNameEditText);
        LastNameEditText = findViewById(R.id.LastNameEditText);
        IdNumberEditText = findViewById(R.id.IdNumberEditText);
        EmailEditText = findViewById(R.id.EmailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        rememberMe = findViewById(R.id.rememberMeCheckbox);
        signupButton = findViewById(R.id.signupButton);
        loginTextButton = findViewById(R.id.login_text_button);

        loginTextButton.setOnClickListener(v ->{
            Intent intent = new Intent(ctx, MainActivity.class);
            startActivity(intent);
        });

        signupButton.setOnClickListener(v ->{
            FirstName = FirstNameEditText.getText().toString();
            LastName = LastNameEditText.getText().toString();
            IdNumber = IdNumberEditText.getText().toString();
            Email = EmailEditText.getText().toString();
            password = passwordEditText.getText().toString();
            confirmPassword = confirmPasswordEditText.getText().toString();

//            perform signup logic
            Intent intent = new Intent(ctx, Profile.class);
            startActivity(intent);
        });
    }
}