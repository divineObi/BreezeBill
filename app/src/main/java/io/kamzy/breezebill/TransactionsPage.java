package io.kamzy.breezebill;

import static androidx.core.content.ContentProviderCompat.requireContext;
import static io.kamzy.breezebill.tools.Tools.baseURL;
import static io.kamzy.breezebill.tools.Tools.client;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import io.kamzy.breezebill.adapters.GroupAdapter;
import io.kamzy.breezebill.adapters.TransactionAdapter;
import io.kamzy.breezebill.models.Groupss;
import io.kamzy.breezebill.models.Transactions;
import io.kamzy.breezebill.tools.ApiCallback;
import io.kamzy.breezebill.tools.DataManager;
import io.kamzy.breezebill.tools.GsonHelper;
import io.kamzy.breezebill.tools.TransactionAPICallback;
import okhttp3.Request;
import okhttp3.Response;

public class TransactionsPage extends AppCompatActivity {
    Context ctx;
    RecyclerView transactionRecyclerVew;
    LinearLayout noTransactionImage;
    List<Transactions> transactionsList;
    TransactionAdapter transactionAdapter;
    ImageView close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transactions);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ctx = this;
        transactionRecyclerVew = findViewById(R.id.transactionRecyclerView);
        noTransactionImage = findViewById(R.id.no_transaction_screen);
        close = findViewById(R.id.transaction_back_button);

        getTransactionsAPI("api/transaction/get/"+DataManager.getInstance().getUsers().getUser_id(), DataManager.getInstance().getToken(), new TransactionAPICallback<List<Transactions>>() {
            @Override
            public void onSuccess(List<Transactions> allTransactions) {
                transactionsList = allTransactions;
                // Set up RecyclerView
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ctx);
                transactionRecyclerVew.setLayoutManager(layoutManager);
                transactionRecyclerVew.setItemAnimator(new DefaultItemAnimator());
                transactionAdapter = new TransactionAdapter(transactionsList, ctx);
                transactionRecyclerVew.setAdapter(transactionAdapter);
                DividerItemDecoration divider = new DividerItemDecoration(transactionRecyclerVew.getContext(), LinearLayoutManager.VERTICAL);
                transactionRecyclerVew.addItemDecoration(divider);
                toggleEmptyState(transactionsList);

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

        close.setOnClickListener(v -> finish());
    }


    public void getTransactionsAPI(String endpoint, String token, TransactionAPICallback<List<Transactions>> callback){

        Request request = new Request.Builder()
                .url(baseURL + endpoint)
                .get()
                .addHeader("Authorization", "Bearer "+token)
                .build();

        new Thread(()->{
            try(Response response = client.newCall(request).execute()){
                int statusCode=response.code();
                Log.i("status code", String.valueOf(statusCode));
                String responseBody = response.body().string();
                if (response.isSuccessful()){
                    if (responseBody.equals("[]")){
                        Log.i("Transaction Status", "No transaction found");
                    }else {
                        Log.i("Transactions", responseBody);
                        JSONArray jsonRespone = new JSONArray(responseBody);
                        GsonHelper gsonHelper = new GsonHelper();
                        List<Transactions> allTransactions = gsonHelper.parseJSONArrayToListTransactions(String.valueOf(jsonRespone));
                        runOnUiThread(()->{
                            if (callback != null) {
                                callback.onSuccess(allTransactions);
                            }
                            DataManager.getInstance().setAllTransactions(allTransactions);
                        });
                    }
                }else {
                    Log.i("Group API status", "Error: Couldn't reach group Endpoint");
                }

            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void toggleEmptyState(List<Transactions> transactions){
        if (transactions!=null){
            noTransactionImage.setVisibility(View.GONE);
            transactionRecyclerVew.setVisibility(View.VISIBLE);

        }else {
            noTransactionImage.setVisibility(View.VISIBLE);
            transactionRecyclerVew.setVisibility(View.GONE);
        }
    }
}