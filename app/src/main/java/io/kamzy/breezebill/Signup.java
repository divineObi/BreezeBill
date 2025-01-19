package io.kamzy.breezebill;

import static io.kamzy.breezebill.tools.Tools.baseURL;
import static io.kamzy.breezebill.tools.Tools.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Signup extends AppCompatActivity {
    TextInputEditText FirstNameEditText, LastNameEditText, IdNumberEditText, passwordEditText, confirmPasswordEditText;
    String FirstName, LastName, IdNumber, password, confirmPassword;
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
            password = passwordEditText.getText().toString();
            confirmPassword = confirmPasswordEditText.getText().toString();

            if (FirstName.isEmpty()) FirstNameEditText.setError("First name is required");
            if (LastName.isEmpty()) LastNameEditText.setError("Last name is required");
            if (IdNumber.isEmpty()) IdNumberEditText.setError("ID number is required");
            if (password.isEmpty()) passwordEditText.setError("Password is required");
            if (confirmPassword.isEmpty()) confirmPasswordEditText.setError("This field is required");

            if (!FirstName.isEmpty() && !LastName.isEmpty() && !IdNumber.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()){
                if (password.equals(confirmPassword)) {
                    try {
                        signupAPI("api/users/signup", FirstName, LastName, IdNumber, password);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    confirmPasswordEditText.setError("Passwords do not match");
                }
            }
        });
    }


    public void signupAPI (String endpoint, String firstName, String lastName, String idNumber, String password) throws JSONException {
        JSONObject jsonObject = new JSONObject().
                put("first_name", firstName)
                .put("last_name", lastName)
                .put("id_number", idNumber)
                .put("password_hash", password);

        RequestBody requestBody = RequestBody.create(
                jsonObject.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(baseURL+endpoint)
                .post(requestBody)
                .build();

        new Thread(()->{
            try(Response response = client.newCall(request).execute()){
                int statusCode = response.code();
                Log.i("statusCode", String.valueOf(statusCode));
                String responseBody = response.body().string();
                if (response.isSuccessful()){
                    JSONObject JSONresponseBody = new JSONObject(responseBody);
                    String signUpStatus = JSONresponseBody.getString("status");
                    runOnUiThread(()->{
                        switch (signUpStatus){
                            case  "Signup Successful":
                                Toast.makeText(ctx, signUpStatus, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ctx, UpdateProfile.class);
                                intent.putExtra("id_number", IdNumber);
                                startActivity(intent);
                                break;
                            default:
                                Toast.makeText(ctx, signUpStatus, Toast.LENGTH_LONG).show();
                                break;
                        }
                    });
                } else {
                    Log.i("responseBody", responseBody);
                }
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }
}