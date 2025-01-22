package io.kamzy.breezebill;

import static io.kamzy.breezebill.tools.Tools.baseURL;
import static io.kamzy.breezebill.tools.Tools.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.kamzy.breezebill.SharedViewModels.WalletSharedviewModel;
import io.kamzy.breezebill.models.Users;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class Passcode extends AppCompatActivity {
    private StringBuilder passcode = new StringBuilder();
    private View[] dots;
    Context ctx;
    String token, IdNumber, inputPasscode;
    TextView passcodeTitle;
    WalletSharedviewModel walletSharedviewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_passcode);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ctx = this;
        token = getIntent().getStringExtra("token");
        IdNumber = getIntent().getStringExtra("idNumber");
        passcodeTitle = findViewById(R.id.passcodeTitle);

        // Initialize dots
        dots = new View[]{
                findViewById(R.id.dot1),
                findViewById(R.id.dot2),
                findViewById(R.id.dot3),
                findViewById(R.id.dot4)
        };

        // Set up keypad buttons
        int[] buttonIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
                R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7,
                R.id.btn8, R.id.btn9
        };

        View.OnClickListener numberClickListener = v -> {
            if (passcode.length() < 4) {
                passcode.append(((Button) v).getText().toString());
                updateDots();
            }
        };

        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(numberClickListener);
        }

        // Backspace button
        findViewById(R.id.btnBackspace).setOnClickListener(v -> {
            if (passcode.length() > 0) {
                passcode.deleteCharAt(passcode.length() - 1);
                updateDots();
            }
        });
    }

    private void updateDots() {
        for (int i = 0; i < dots.length; i++) {
            dots[i].setBackgroundResource(i < passcode.length() ? R.drawable.dot_filled : R.drawable.dot_empty);
        }

        if (passcode.length() == 4) {
            if (inputPasscode == null){
                inputPasscode = passcode.toString();
                passcodeTitle.setText("Confirm Passcode");
//            Reset and show error
                passcode.setLength(0);
                updateDots();
            } else {
                if (passcode.toString().equals(inputPasscode)){
                    saveWalletCode("api/wallet/save_code", inputPasscode, IdNumber, token);
                }else {
                    Toast.makeText(ctx, "Passcode does not match", Toast.LENGTH_SHORT).show();
                    passcode.setLength(0);
                    updateDots();
                }
            }
        }
    }

    private void saveWalletCode(String endpoint, String Passcode, String IdNumber, String token) {
        FormBody.Builder formBody = new FormBody.Builder()
                .add("id_number", IdNumber)
                .add("passcode", Passcode);

        Request request = new Request.Builder()
                .url(baseURL + endpoint)
                .post(formBody.build())
                .addHeader("Authorization", "Bearer "+token)
                .build();

        new Thread(()->{
            try(Response response = client.newCall(request).execute()){
                int statusCode=response.code();
                Log.i("status code", String.valueOf(statusCode));
                String responseBody =response.body() != null ? response.body().string() : "null";
                if (response.isSuccessful()){
                    if (responseBody.equals("null")){
                        Log.i("Code Save Status", "Failed");
                    }else {
                        Log.i("Code Save Status", "Successfull");
                        JSONObject jsonRespone = new JSONObject(responseBody);
                        String status = jsonRespone.getString("status");
                        runOnUiThread(()->{
                           Toast.makeText(ctx, status, Toast.LENGTH_LONG).show();
                           Intent intent = new Intent(ctx, Dashboard.class);
                           intent.putExtra("token", token);
                           intent.putExtra("idNumber", IdNumber);
                           startActivity(intent);
                        });
                    }
                }else {
                    Log.i("API Error", "Error Connecting to Wallet Endpoint");
                }

            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }
}