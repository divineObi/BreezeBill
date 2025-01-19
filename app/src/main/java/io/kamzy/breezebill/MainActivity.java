package io.kamzy.breezebill;

import static io.kamzy.breezebill.tools.Tools.baseURL;
import static io.kamzy.breezebill.tools.Tools.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    TextInputEditText idNumberEditText, passwordEditText;
    TextView signUpTextButton, forgotPassword;
    String idNumber, password;
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
            idNumber = idNumberEditText.getText().toString();
            password = passwordEditText.getText().toString();

            if (idNumber.isEmpty()) idNumberEditText.setError("ID number is required");
            if (password.isEmpty()) passwordEditText.setError("Password is required");
            if (!idNumber.isEmpty() && !password.isEmpty()){
                callLoginAPI("api/users/login", idNumber, password);
            }
        });

        signUpTextButton.setOnClickListener(v -> {
            Intent intent = new Intent(ctx, Signup.class);
            startActivity(intent);

        });

    }

    private void callLoginAPI (String endpoint, String idNumber, String password){
        FormBody.Builder formBody = new FormBody.Builder()
                .add("id_number", idNumber)
                .add("password", password);

        Request request = new Request.Builder()
                .url(baseURL+endpoint)
                .post(formBody.build())
                .build();

        new Thread(()->{
            try(Response response = client.newCall(request).execute()){
                int statusCode = response.code();
                Log.i("statusCode", String.valueOf(statusCode));
                String responseBody = response.body().string();
                if (response.isSuccessful()){
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    String token = jsonResponse.getString("token");
                    Log.i("response", token);
                    runOnUiThread(()->{
                        Intent intent = new Intent(ctx, Dashboard.class);
                        intent.putExtra("token", token);
                        startActivity(intent);
                    });
                } else if (statusCode == 401){
                    runOnUiThread(()->{
                        Toast.makeText(ctx, "invalid username or password", Toast.LENGTH_LONG).show();
                    });
                } else {
                    Log.e("API call error", "error connecting to API");
                }
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}