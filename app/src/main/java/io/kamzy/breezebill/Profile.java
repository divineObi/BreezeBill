package io.kamzy.breezebill;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import io.kamzy.breezebill.tools.DepartmentHelper;

public class Profile extends AppCompatActivity {
    TextInputEditText PhoneEditText,DOBEditText, classEditText;
    AutoCompleteTextView faculty_auto_complete_text_view, department_auto_complete_text_view, gender_auto_complete_text_view;
    String Phone, DOB, classYear, faculty, department, gender;
    String [] faculty_list, department_list, gender_list;
    Context ctx;
    MaterialButton saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ctx = this;
        PhoneEditText = findViewById(R.id.PhoneEditText);
        DOBEditText = findViewById(R.id.DOBEditText);
        classEditText = findViewById(R.id.classEditText);
        faculty_auto_complete_text_view = findViewById(R.id.faculty_auto_complete_text_view);
        department_auto_complete_text_view = findViewById(R.id.department_auto_complete_text_view);
        gender_auto_complete_text_view = findViewById(R.id.gender_auto_complete_text_view);
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
            Intent intent = new Intent(ctx, Passcode.class);
            startActivity(intent);
        });

    }
}