package io.kamzy.breezebill;

import static io.kamzy.breezebill.tools.Tools.baseURL;
import static io.kamzy.breezebill.tools.Tools.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

import io.kamzy.breezebill.tools.DepartmentHelper;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdateProfile extends AppCompatActivity {
    TextInputEditText PhoneEditText,DOBEditText, classEditText, emailEditText;
    AutoCompleteTextView faculty_auto_complete_text_view, department_auto_complete_text_view, gender_auto_complete_text_view;
    String Phone, DOB, classYear, email, faculty, department, gender, idNumber;
    String [] faculty_list, gender_list;
    Context ctx;
    MaterialButton saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ctx = this;
        PhoneEditText = findViewById(R.id.PhoneEditText);
        DOBEditText = findViewById(R.id.DOBEditText);
        classEditText = findViewById(R.id.classEditText);
        emailEditText = findViewById(R.id.EmailEditText);
        faculty_auto_complete_text_view = findViewById(R.id.faculty_auto_complete_text_view);
        department_auto_complete_text_view = findViewById(R.id.department_auto_complete_text_view);
        gender_auto_complete_text_view = findViewById(R.id.gender_auto_complete_text_view);
        idNumber = getIntent().getStringExtra("id_number");
        saveButton = findViewById(R.id.saveButton);
        DepartmentHelper deptHelper = new DepartmentHelper(ctx);

        faculty_list = getResources().getStringArray(R.array.faculty_list);
        gender_list = getResources().getStringArray(R.array.gender_list);

        // Create an ArrayAdapter for the dropdown options & Set the adapter to the AutoCompleteTextView
        ArrayAdapter<String> facultyAdapter = new ArrayAdapter<>(
                ctx,
                android.R.layout.simple_dropdown_item_1line,
                faculty_list
        );
        faculty_auto_complete_text_view.setAdapter(facultyAdapter);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                ctx,
                android.R.layout.simple_dropdown_item_1line,
                gender_list
        );
        gender_auto_complete_text_view.setAdapter(genderAdapter);

        faculty_auto_complete_text_view.setOnItemClickListener((parent, view, position, id) -> {
            String selectedFaculty = (String) parent.getItemAtPosition(position);
            deptHelper.populateDepartmentDropdown(selectedFaculty, department_auto_complete_text_view);

        });

        saveButton.setOnClickListener(v ->{
            Phone = PhoneEditText.getText().toString();
            DOB = DOBEditText.getText().toString();
            classYear = classEditText.getText().toString();
            email = emailEditText.getText().toString();
            faculty = faculty_auto_complete_text_view.getText().toString();
            department = department_auto_complete_text_view.getText().toString();
            gender = gender_auto_complete_text_view.getText().toString();


            if (email.isEmpty()) emailEditText.setError("Email is required");
            if (Phone.isEmpty()) PhoneEditText.setError("Phone number is required");
            if (DOB.isEmpty()) DOBEditText.setError("Date of birth is required");
            if (classYear.isEmpty()) classEditText.setError("Class year is required");
            if (faculty.isEmpty()) faculty_auto_complete_text_view.setError("Faculty is required");
            if (department.isEmpty()) department_auto_complete_text_view.setError("Department is required");
            if (gender.isEmpty()) gender_auto_complete_text_view.setError("Gender is required");
            if (!email.isEmpty() && !Phone.isEmpty() && !DOB.isEmpty() && !classYear.isEmpty() && !faculty.isEmpty() && !department.isEmpty() && !gender.isEmpty()){
                try {
                    createProfileAPI("api/users/create_profile", email, Phone, DOB, gender, faculty, department, classYear);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    public void createProfileAPI (String endpoint, String...parameters) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id_number", idNumber)
                .put("email", parameters[0])
                .put("phone_number", parameters[1])
                .put("date_of_birth", parameters[2])
                .put("gender", parameters[3])
                .put("faculty", parameters[4])
                .put("department", parameters[5])
                .put("class_year", parameters[6]);

        RequestBody requestBody =  RequestBody.create(
                jsonObject.toString(), MediaType.get("application/json; charset=utf-8")
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
                    String profileStatus = JSONresponseBody.getString("status");
                    runOnUiThread(()->{
                                Toast.makeText(ctx, profileStatus, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ctx, MainActivity.class);
                                startActivity(intent);
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