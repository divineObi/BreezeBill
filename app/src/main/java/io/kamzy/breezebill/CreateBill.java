package io.kamzy.breezebill;

import static io.kamzy.breezebill.tools.Tools.baseURL;
import static io.kamzy.breezebill.tools.Tools.client;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import io.kamzy.breezebill.enums.BillCategory;
import io.kamzy.breezebill.models.Groupss;
import io.kamzy.breezebill.tools.DataManager;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateBill extends AppCompatActivity {
    Context ctx;
    TextInputLayout billNameInput, billDescriptionInput, billCatInput, recipientGroupInput, unitAmountInput, dueDateInput, bankNameInput, accountNumberInput;
    TextInputEditText billNameEt, billDescriptionEt,unitAmountEt, dueDateEt, bankNameEt, accountNumberEt;
    AutoCompleteTextView recipientGroupEt, billCatEt;
    TextView accountNameTextView, expectedTotalTextView;
    Button createBillButton;
    String billName, billDescription, billCat, recipientGroup, unitAmount, dueDate, bankName, accountNumber, accountName;
    String [] recipientGroupsList, billCategoryList;
    List<Groupss> userGroups;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_bill);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ctx = this;
        billNameInput = findViewById(R.id.billNameInputLayout);
        billDescriptionInput = findViewById(R.id.billDescriptionInputLayout);
        billCatInput = findViewById(R.id.billCatInputLayout);
        recipientGroupInput = findViewById(R.id.recipientGroupInputLayout);
        unitAmountInput = findViewById(R.id.unitAmountInputLayout);
        dueDateInput = findViewById(R.id.dueDateInputLayout);
        bankNameInput = findViewById(R.id.bankNmaeInputLayout);
        accountNumberInput = findViewById(R.id.accountNumberInputLayout);
        billNameEt = findViewById(R.id.billNameEditText);
        billDescriptionEt = findViewById(R.id.billDescriptionEditText);
        unitAmountEt = findViewById(R.id.unitAmountEditText); dueDateEt = findViewById(R.id.dueDateEditText);
        bankNameEt = findViewById(R.id.bankNameEditText);
        accountNumberEt = findViewById(R.id.accountNumberEditText);
        recipientGroupEt = findViewById(R.id.recipientGroupEditText);
        billCatEt = findViewById(R.id.billCatEditText);
        accountNameTextView = findViewById(R.id.accountName);
        expectedTotalTextView = findViewById(R.id.expectedTotal);
        createBillButton = findViewById(R.id.createBillButton);
        backButton = findViewById(R.id.create_bill_back_button);

//        initialize bill category list
        billCategoryList = new String[]{String.valueOf(BillCategory.Compulsory), String.valueOf(BillCategory.Optional)};
        ArrayAdapter<String> billCatAdapter = new ArrayAdapter<>(
                ctx,
                android.R.layout.simple_dropdown_item_1line,
                billCategoryList
        );
        billCatEt.setAdapter(billCatAdapter);


//        initialize recipient group list
        userGroups = DataManager.getInstance().getUsersGroups();
        if (userGroups != null){
            recipientGroupsList = new String[userGroups.size()];
            for (int i = 0; i < userGroups.size(); i++) {
                recipientGroupsList[i] = userGroups.get(i).getGroup_name();
            }
            ArrayAdapter<String> recipientGroupAdapter = new ArrayAdapter<>(
                    ctx,
                    android.R.layout.simple_dropdown_item_1line,
                    recipientGroupsList
            );
            recipientGroupEt.setAdapter(recipientGroupAdapter);
        }else {
            Log.i("Recipient Group List", "No groups found");
        }


//        Track account number and fetch users Name


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

        unitAmountEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String amount = s.toString();
                if (!amount.isEmpty()){
                double unitAmount = Double.parseDouble(amount);
                for (Groupss groupss : userGroups){
                    if (groupss.getGroup_name().equals(recipientGroupEt.getText().toString())){
                        try {
                            int memberCount = groupss.getMember_count();
                            double totalAmount = unitAmount * memberCount;
                            expectedTotalTextView.setText("Expected total: " + formatAmount(totalAmount));
                            expectedTotalTextView.setVisibility(View.VISIBLE);
                        }catch (NumberFormatException n){
                            expectedTotalTextView.setText("Enter a valid amount!");
                            expectedTotalTextView.setTextColor(getResources().getColor(R.color.red));
                            expectedTotalTextView.setVisibility(View.VISIBLE);
                        }

                    }
                }

                } else {
                    expectedTotalTextView.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        recipientGroupEt.setOnItemClickListener((parent, view, position, id) -> {
            String amount = unitAmountEt.getText().toString();
            if (!amount.isEmpty()){
                double unitAmount = Double.parseDouble(amount);
                for (Groupss groupss : userGroups){
                    if (groupss.getGroup_name().equals(recipientGroupEt.getText().toString())){
                        try {
                            int memberCount = groupss.getMember_count();
                            double totalAmount = unitAmount * memberCount;
                            expectedTotalTextView.setText("Expected total: " + formatAmount(totalAmount));
                            expectedTotalTextView.setVisibility(View.VISIBLE);
                        }catch (NumberFormatException n){
                            expectedTotalTextView.setText("Enter a valid amount!");
                            expectedTotalTextView.setTextColor(getResources().getColor(R.color.red));
                            expectedTotalTextView.setVisibility(View.VISIBLE);
                        }

                    }
                }

            } else {
                expectedTotalTextView.setVisibility(View.GONE);
            }
        });

        createBillButton.setOnClickListener(view -> {
            billName = billNameEt.getText().toString();
            billDescription = billDescriptionEt.getText().toString();
            billCat = billCatEt.getText().toString();
            recipientGroup = recipientGroupEt.getText().toString();
            unitAmount = unitAmountEt.getText().toString();
            dueDate = dueDateEt.getText().toString();
            bankName = bankNameEt.getText().toString();
            accountNumber = accountNumberEt.getText().toString();
            accountName = accountNameTextView.getText().toString();

            if (billName.isEmpty()) billNameEt.setError("This Field is required");
            if (billCat.isEmpty()) billCatEt.setError("This Field is required");
            if (recipientGroup.isEmpty()) recipientGroupEt.setError("This Field is required");
            if (unitAmount.isEmpty()) unitAmountEt.setError("This Field is required");
            if (dueDate.isEmpty()) dueDateEt.setError("This Field is required");
            if (bankName.isEmpty()) bankNameEt.setError("This Field is required");
            if (accountNumber.isEmpty()) accountNumberEt.setError("This Field is required");

            if (accountName.equalsIgnoreCase("Account not found")){
                Toast.makeText(ctx, "Inavlid account number", Toast.LENGTH_LONG).show();
            }else {
                if (billDescription.isEmpty()) billDescription = "";
                if (!billName.isEmpty() && !billDescription.isEmpty() && !billCat.isEmpty() && !recipientGroup.isEmpty() && !unitAmount.isEmpty() && !dueDate.isEmpty() && !bankName.isEmpty() && !accountNumber.isEmpty()){
                    try {
                        createBillAPI("api/bills/create-bill", billName, billDescription, unitAmount, dueDate, accountNumber, bankName, recipientGroup, billCat);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        });

        backButton.setOnClickListener(v -> finish());
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

    public void createBillAPI(String endpoint, String...parameters) throws JSONException {
       JSONObject requestParams = new JSONObject();
       requestParams.put("created_by", DataManager.getInstance().getUsers().getUser_id())
               .put("bill_name", parameters[0])
               .put("description", parameters[1])
               .put("unit_amount",parameters[2])
               .put("due_date",parameters[3])
               .put("payment_account",parameters[4])
               .put("payment_bank",parameters[5])
               .put("recipient_group",parameters[6])
               .put("bill_category",parameters[7]);


        RequestBody requestBody = RequestBody.create(requestParams.toString(), MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(baseURL + endpoint)
                .post(requestBody)
                .addHeader("Authorization", "Bearer "+DataManager.getInstance().getToken())
                .build();

        new Thread(()->{
            try(Response response = client.newCall(request).execute()){
                int statusCode=response.code();
                Log.i("status code", String.valueOf(statusCode));
                String responseBody = response.body() != null ? response.body().string() : "null";
                if (response.isSuccessful()){
                    if (responseBody.equals("null")){
                        Log.i("Create Bill API", "No Response Gotten");
                    }else {
                        Log.i("create bill response body", responseBody);
                        JSONObject jsonRespone = new JSONObject(responseBody);
                        String status = jsonRespone.getString("status");
                        runOnUiThread(()->{
                            if (status.equalsIgnoreCase("Bill Created Successfully")) {
                              Toast.makeText(ctx, status, Toast.LENGTH_LONG).show();
                              finish();
                            } else {
                                Toast.makeText(ctx, status, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }else {
                    Log.i("Create Bill API status", "Error: Couldn't reach Bill Endpoint");
                }

            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }


    public static String formatAmount(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "NG")); // "NG" for Nigeria
        String formattedAmount = formatter.format(amount);
        // Remove the default currency symbol and prepend "N"
        return "N" + formattedAmount.substring(1); // Replace "$" or other symbols
    }

}