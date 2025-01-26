package io.kamzy.breezebill;

import static io.kamzy.breezebill.adapters.GroupAdapter.getUsersBillsAPI;
import static io.kamzy.breezebill.tools.Tools.baseURL;
import static io.kamzy.breezebill.tools.Tools.client;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.kamzy.breezebill.enums.BillStatus;
import io.kamzy.breezebill.models.UserBillsDTO;
import io.kamzy.breezebill.tools.DataManager;
import io.kamzy.breezebill.tools.UserBillsAPICallback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class Payment extends AppCompatActivity {
    Context ctx;
    TextInputLayout accountNumberInput, AmountInput, RemarkInput;
    TextInputEditText accountNumberEt, AmountEt, RemarkEt;
    Button confirmButton;
    String payment_type, accountNumber, amount, remark, bill_id, payment_account, accountName;
    TextView accountNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ctx = this;
        accountNumberInput = findViewById(R.id.AccountNumberInput);
        AmountInput = findViewById(R.id.AmountInput);
        RemarkInput = findViewById(R.id.RemarkInput);
        accountNumberEt = findViewById(R.id.AccountNumberEt);
        AmountEt = findViewById(R.id.AmountEt);
        RemarkEt = findViewById(R.id.RemarkEt);
        confirmButton = findViewById(R.id.PayButton);
        accountNameTextView = findViewById(R.id.PaymentaccountName);
        payment_type = getIntent().getStringExtra("payment_type");

        if ("Bill Payment".equalsIgnoreCase(payment_type)){
            bill_id = getIntent().getStringExtra("bill_id");
            amount = getIntent().getStringExtra("amount");
            remark = getIntent().getStringExtra("remark");
            payment_account = getIntent().getStringExtra("payment_account");
            accountNumberEt.setText(payment_account);
            AmountEt.setText(amount);
            RemarkEt.setText(remark);

            if(accountNumberEt.getText().toString() != null && accountNumberEt.getText().toString().length() == 10){
                accountNumber = accountNumberEt.getText().toString();
                getAccountName("api/van/account_name", accountNumber, DataManager.getInstance().getToken());
            }


        }

        accountNumberEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length() == 10){
                    accountNumber = s.toString();
                    getAccountName("api/van/account_name", accountNumber, DataManager.getInstance().getToken());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmButton.setOnClickListener(v -> {
           amount = AmountEt.getText().toString();
           accountNumber = accountNumberEt.getText().toString();
           accountName = accountNameTextView.getText().toString();
           remark = RemarkEt.getText().toString();

           if (amount.isEmpty()) AmountEt.setError("Enter Amount");
           if (accountNumber.isEmpty()) accountNumberEt.setError("Enter Account Number");
           if (remark.isEmpty()) remark = "";

           if (accountName.equalsIgnoreCase("Account not found")){
               Toast.makeText(ctx, "Account not found", Toast.LENGTH_LONG).show();
           }else {
               if (payment_type.equalsIgnoreCase("Bill Payment")){
//                   call pay bill API
                   payBillAPI("api/bills/pay", bill_id, amount, remark);
               }else {
//                   call tarnsfer API
               }
           }
        });

    }


    public void getAccountName(String endpoint, String accountNumber, String token){
        FormBody.Builder formBody = new FormBody.Builder()
                .add("account_number", accountNumber);

        Request request = new Request.Builder()
                .url(baseURL + endpoint)
                .post(formBody.build())
                .addHeader("Authorization", "Bearer "+token)
                .build();

        new Thread(()->{
            try(Response response = client.newCall(request).execute()){
                int statusCode=response.code();
                Log.i("status code", String.valueOf(statusCode));
                String responseBody = response.body() != null ? response.body().string() : "null";
                if (response.isSuccessful()){
                    if (responseBody.equals("null")){
                        Log.i("Fetch Account Name", "No Response Gotten");
                    }else {
                        Log.i("Account Name", responseBody);
                        JSONObject jsonRespone = new JSONObject(responseBody);
                        String account_name = jsonRespone.getString("accountName");
                        runOnUiThread(()->{
                            if (account_name.equalsIgnoreCase("Account not found")) {
                                accountNameTextView.setVisibility(View.VISIBLE);
                                accountNameTextView.setText(account_name);
                                accountNameTextView.setTextColor(getResources().getColor(R.color.red));
                            } else {
                                accountNameTextView.setVisibility(View.VISIBLE);
                                accountNameTextView.setText(account_name);
                                accountNameTextView.setTextColor(getResources().getColor(R.color.primary_100));
                            }
                        });
                    }
                }else {
                    Log.i("Account Name API status", "Error: Couldn't reach VAN Endpoint");
                }

            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void payBillAPI(String endpoint, String bill_id, String amount, String remark){
        FormBody.Builder formBody = new FormBody.Builder()
                .add("senderId", String.valueOf(DataManager.getInstance().getUsers().getUser_id()))
                .add("billId", bill_id)
                .add("amount", amount)
                .add("description", remark);

        Request request = new Request.Builder()
                .url(baseURL + endpoint)
                .post(formBody.build())
                .addHeader("Authorization", "Bearer "+DataManager.getInstance().getToken())
                .build();

        new Thread(()->{
            try(Response response = client.newCall(request).execute()){
                int statusCode=response.code();
                Log.i("status code", String.valueOf(statusCode));
                String responseBody = response.body() != null ? response.body().string() : "null";
                if (response.isSuccessful()){
                    if (responseBody.equals("null")){
                        Log.i("Payment Status", "No Response Gotten");
                    }else {
                        Log.i("Payment Status", responseBody);
                        JSONObject jsonRespone = new JSONObject(responseBody);
                        String status = jsonRespone.getString("status");
                        runOnUiThread(()->{
                            //        get all bills, filter & save
                            getUsersBillsAPI("api/bills/get-bills/"+DataManager.getInstance().getUsers().getUser_id(), DataManager.getInstance().getToken(), new UserBillsAPICallback<List<UserBillsDTO>>() {
                                @Override
                                public void onSuccess(List<UserBillsDTO> allBills) {;
                                    List<UserBillsDTO> paidBills = new ArrayList<>();
                                    List<UserBillsDTO> unpaidBills = new ArrayList<>();
                                    for (UserBillsDTO bill : allBills){
                                        if (bill.getStatus().equals(BillStatus.paid)){
                                            paidBills.add(bill);
                                        }else {
                                            unpaidBills.add(bill);
                                        }

                                        if (!paidBills.isEmpty()){
                                            DataManager.getInstance().setPaidBills(paidBills);
                                        }else {
                                            DataManager.getInstance().setPaidBills(null);
                                        }

                                        if (!unpaidBills.isEmpty()){
                                            DataManager.getInstance().setUnpaidBills(unpaidBills);
                                        }else {
                                            DataManager.getInstance().setUnpaidBills(null);
                                        }
                                        Log.i("All Bills", allBills.toString());
                                        Log.i("Paid Bills", paidBills.toString());
                                        Log.i("Unpaid Bills", unpaidBills.toString());
                                    }

                                }

                                @Override
                                public void onFailure(Throwable t) {

                                }
                            });

                            Toast.makeText(ctx, status, Toast.LENGTH_LONG).show();
                        });
                    }
                }else {
                    Log.i("Payment Status API status", "Error: Couldn't reach Payment Endpoint");
                    Log.i("Payment Status API status", responseBody);
                    runOnUiThread(()-> Toast.makeText(ctx, responseBody, Toast.LENGTH_LONG).show());
                }

            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}