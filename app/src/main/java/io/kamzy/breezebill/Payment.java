package io.kamzy.breezebill;

import static io.kamzy.breezebill.adapters.GroupAdapter.getUsersBillsAPI;
import static io.kamzy.breezebill.tools.Tools.baseURL;
import static io.kamzy.breezebill.tools.Tools.client;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.kamzy.breezebill.enums.BillStatus;
import io.kamzy.breezebill.models.UserBillsDTO;
import io.kamzy.breezebill.models.Wallet;
import io.kamzy.breezebill.tools.DataManager;
import io.kamzy.breezebill.tools.GsonHelper;
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
    GsonHelper gsonHelper = new GsonHelper();
    StringBuilder passcode = new StringBuilder();
    String inputPasscode;



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
                if (accountNumber.equals(DataManager.getInstance().getVAN().getVirtual_account_number())){
                    accountNumberEt.setError("You can't transfer to your own account");
                }else {
                    getAccountName("api/van/account_name", accountNumber, DataManager.getInstance().getToken());
                }
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
                    if (accountNumber.equals(DataManager.getInstance().getVAN().getVirtual_account_number())){
                        accountNumberEt.setError("You can't transfer to your own account");
                    }else {
                        getAccountName("api/van/account_name", accountNumber, DataManager.getInstance().getToken());
                    }
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
                showPasscodeDialogue(DataManager.getInstance().getToken());

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
                if (response.isSuccessful()){
                    String responseBody = response.body() != null ? response.body().string() : "null";
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
                                        finish();
                                    }

                                }

                                @Override
                                public void onFailure(Throwable t) {

                                }
                            });
                            getWalletAPI("api/wallet/get_wallet", DataManager.getInstance().getUsers().getId_number(), DataManager.getInstance().getToken());
                            Toast.makeText(ctx, status, Toast.LENGTH_LONG).show();
                        });
                    }
                }else {
                    String errorBody = response.body() != null ? response.body().string() : "No error message provided by the server.";
                    Log.i("Payment Status API status", errorBody);
                    JSONObject jsonResponse = new JSONObject(errorBody);
                    String error = jsonResponse.getString("error");
                    runOnUiThread(()-> Toast.makeText(ctx, error, Toast.LENGTH_LONG).show());
                }

            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void transferAPI(String endpoint, String accountNumber, String amount, String remark){
        FormBody.Builder formBody = new FormBody.Builder()
                .add("user_id", String.valueOf(DataManager.getInstance().getUsers().getUser_id()))
                .add("account_number", accountNumber)
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
                if (response.isSuccessful()){
                    String responseBody = response.body() != null ? response.body().string() : "null";
                    if (responseBody.equals("null")){
                        Log.i("Transfer Status", "No Response Gotten");
                    }else {
                        Log.i("Transfer Status", responseBody);
                        JSONObject jsonRespone = new JSONObject(responseBody);
                        String status = jsonRespone.getString("status");
                        runOnUiThread(()->{
                            getWalletAPI("api/wallet/get_wallet", DataManager.getInstance().getUsers().getId_number(), DataManager.getInstance().getToken());
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
                                    finish();

                                }

                                @Override
                                public void onFailure(Throwable t) {

                                }
                            });
                            Toast.makeText(ctx, status, Toast.LENGTH_LONG).show();
                        });
                    }
                }else {
                    String errorBody = response.body() != null ? response.body().string() : "No error message provided by the server.";
                    Log.i("Transfer API status", errorBody);
                    JSONObject jsonResponse = new JSONObject(errorBody);
                    String error = jsonResponse.getString("error");
                    runOnUiThread(()-> Toast.makeText(ctx, error, Toast.LENGTH_LONG).show());
                }

            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void showPasscodeDialogue(String token) {
        // Use Fragment's context for the BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ctx);

// Inflate the custom layout for the bottom sheet
        View bottomSheetView = LayoutInflater.from(ctx).inflate(
                R.layout.passcode_bottom_dialogue, // Custom layout file
                null // Use null since you'll set it as the dialog content
        );

// Access views from the bottomSheetView
        View[] dots = new View[]{
                bottomSheetView.findViewById(R.id.passcodeDot1),
                bottomSheetView.findViewById(R.id.passcodeDot2),
                bottomSheetView.findViewById(R.id.passcodeDot3),
                bottomSheetView.findViewById(R.id.passcodeDot4)
        };

// Set up keypad buttons
        int[] buttonIds = {
                R.id.passcodeBtn0, R.id.passcodeBtn1, R.id.passcodeBtn2, R.id.passcodeBtn3,
                R.id.passcodeBtn4, R.id.passcodeBtn5, R.id.passcodeBtn6, R.id.passcodeBtn7,
                R.id.passcodeBtn8, R.id.passcodeBtn9
        };

        View.OnClickListener numberClickListener = v -> {
            if (passcode.length() < 4) {
                passcode.append(((Button) v).getText().toString());
                updateDots(dots, bottomSheetDialog);
            }
        };

        for (int id : buttonIds) {
            bottomSheetView.findViewById(id).setOnClickListener(numberClickListener);
        }

// Backspace button
        bottomSheetView.findViewById(R.id.passcodeBtnBackspace).setOnClickListener(v -> {
            if (passcode.length() > 0) {
                passcode.deleteCharAt(passcode.length() - 1);
                updateDots(dots, bottomSheetDialog);
            }
        });

// Set the content view for the dialog and show it
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
//        closeButton.setOnClickListener(v -> {
//            // Dismiss the dialog after saving
//            bottomSheetDialog.dismiss();
//
//        });


    }


    private void getWalletAPI (String endpoint, String idNumber, String token){
        FormBody.Builder formbody = new FormBody.Builder()
                .add("id_number", idNumber);

        Request request = new Request.Builder()
                .url(baseURL + endpoint)
                .post(formbody.build())
                .addHeader("Authorization", "Bearer "+token)
                .build();

        new Thread(()->{
            try(Response response = client.newCall(request).execute()){
                int statusCode=response.code();
                Log.i("status code", String.valueOf(statusCode));
                String responseBody =response.body() != null ? response.body().string() : "null";
                if (response.isSuccessful()){
                    if (responseBody.equals("null")){
                        Log.i("Wallet Status", "Not Found");
                    }else {
                        JSONObject jsonRespone = new JSONObject(responseBody);
                        Wallet userWallet = gsonHelper.parseJSONtoWallet(jsonRespone.toString());
                        runOnUiThread(()->{
                            DataManager.getInstance().setWallet(userWallet);
                        });
                    }
                }else {
                    Log.i("Wallet API Status", "Error Connecting to Wallet Endpoint");
                }

            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void updateDots(View[] dots, BottomSheetDialog bottomSheetDialog) {
        for (int i = 0; i < dots.length; i++) {
            dots[i].setBackgroundResource(i < passcode.length() ? R.drawable.dot_filled : R.drawable.dot_empty);
        }

        if (passcode.length() == 4) {
            if (inputPasscode == null){
                inputPasscode = passcode.toString();
               if (inputPasscode.equals(DataManager.getInstance().getWallet().getCode())){
                   amount = AmountEt.getText().toString();
                   remark = RemarkEt.getText().toString();
                   accountNumber = accountNumberEt.getText().toString();

                   if (payment_type.equalsIgnoreCase("Bill Payment")){
//                   call pay bill API
                       payBillAPI("api/bills/pay", bill_id, amount, remark);
                   }else {
//                   call transfer API
                       transferAPI("api/wallet/transfer", accountNumber, amount, remark);
                   }
                   bottomSheetDialog.dismiss();
               }else {
                   Toast.makeText(ctx, "Incorrect Passcode", Toast.LENGTH_LONG).show();

               }
            }
        }
    }
}