package io.kamzy.breezebill;

import static io.kamzy.breezebill.tools.Tools.baseURL;
import static io.kamzy.breezebill.tools.Tools.client;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;

import io.kamzy.breezebill.models.Profile;
import io.kamzy.breezebill.tools.DepartmentHelper;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private TextInputEditText etIDNumber, etEmail, etPhone, etDateOfBirth, etClassYear;
    private AutoCompleteTextView etGender, etFaculty, etDepartment;
    private TextInputLayout etGenderInput, etFacultyInput, etDepartmentInput, IdNumberInput, emailInput, phoneInput, DOBInput, classYearInput;
    private Button btnUpdate, btnSave;
    DepartmentHelper deptHelper;
    String [] faculty_list, gender_list;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etIDNumber = view.findViewById(R.id.et_idNumber);
        etEmail = view.findViewById(R.id.et_email);
        etPhone = view.findViewById(R.id.et_phone);
        etDateOfBirth = view.findViewById(R.id.et_date_of_birth);
        etGender = view.findViewById(R.id.et_gender);
        etFaculty = view.findViewById(R.id.et_faculty);
        etDepartment = view.findViewById(R.id.et_department);
        etClassYear = view.findViewById(R.id.et_clss_year);
        etGenderInput = view.findViewById(R.id.et_genderTextInput);
        etDepartmentInput = view.findViewById(R.id.et_departmentTextInput);
        etFacultyInput = view.findViewById(R.id.et_facultyTextInput);
        IdNumberInput = view.findViewById(R.id.et_IdNumberInput);
        emailInput = view.findViewById(R.id.et_emailTextInput);
        classYearInput = view.findViewById(R.id.et_classYearInput);
        phoneInput = view.findViewById(R.id.et_phoneInut);
        DOBInput = view.findViewById(R.id.et_dateOfBirthInput);
        btnUpdate = view.findViewById(R.id.btn_update);
        btnSave = view.findViewById(R.id.btn_save);
        faculty_list = getResources().getStringArray(R.array.faculty_list);
        gender_list = getResources().getStringArray(R.array.gender_list);
        deptHelper = new DepartmentHelper(getContext());

        getProfileApI("api/users/get-profile", "20191152552");

        // Create an ArrayAdapter for the dropdown options & Set the adapter to the AutoCompleteTextView
        ArrayAdapter<String> facultyAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                faculty_list
        );
        etFaculty.setAdapter(facultyAdapter);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                gender_list
        );
        etGender.setAdapter(genderAdapter);

        etFaculty.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedFaculty = (String) parent.getItemAtPosition(position);
            deptHelper.populateDepartmentDropdown(selectedFaculty,etDepartment);
        });


        // Update Button Logic
        btnUpdate.setOnClickListener(v -> {
            toggleFormEditable(true);
            setDropdownEditable(true);
        });

        // Save Button Logic
        btnSave.setOnClickListener(v -> {
            // Save changes (implement your save logic here)
            toggleFormEditable(false);
            setDropdownEditable(false);
            clearFocusFromAllFields();
            resetTextInputStates();
        });

    }


    private void toggleFormEditable(boolean isEditable) {
        etIDNumber.setEnabled(isEditable);
        etEmail.setEnabled(isEditable);
        etPhone.setEnabled(isEditable);
        etDateOfBirth.setEnabled(isEditable);
        etGender.setEnabled(isEditable);
        etFaculty.setEnabled(isEditable);
        etDepartment.setEnabled(isEditable);
        etClassYear.setEnabled(isEditable);

        btnUpdate.setEnabled(!isEditable);
        btnSave.setEnabled(isEditable);

        if (isEditable) {
            btnUpdate.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.placeholder_text));
            btnSave.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.primary_100));
        } else {
            btnUpdate.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.primary_100));
            btnSave.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.placeholder_text));
        }
    }

    public void setDropdownEditable(boolean isEditable) {
        if (isEditable) {
            // Enable the dropdown
            etGenderInput.setEndIconMode(TextInputLayout.END_ICON_DROPDOWN_MENU); // Add dropdown icon
            etFacultyInput.setEndIconMode(TextInputLayout.END_ICON_DROPDOWN_MENU); // Add dropdown icon
            etDepartmentInput.setEndIconMode(TextInputLayout.END_ICON_DROPDOWN_MENU); // Add dropdown icon
        } else {
            // Disable the dropdown
            etGenderInput.setEndIconMode(TextInputLayout.END_ICON_NONE); // Add dropdown icon
            etFacultyInput.setEndIconMode(TextInputLayout.END_ICON_NONE); // Add dropdown icon
            etDepartmentInput.setEndIconMode(TextInputLayout.END_ICON_NONE); // Add dropdown icon
        }
    }

    // Method to clear focus
    private void clearFocusFromAllFields() {
        if (getActivity() != null) {
            // Get the currently focused view
            View currentFocus = getActivity().getCurrentFocus();

            // Check if there is a focused view
            if (currentFocus != null) {
                clearFocusAndHideKeyboard(currentFocus);
            }
        }
    }

    private void clearFocusAndHideKeyboard(View currentFocus) {
        // Clear focus from the currently focused view
        currentFocus.clearFocus();

        // Hide the keyboard
        hideKeyboard(currentFocus);
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void resetTextInputStates() {
        // Assuming you have TextInputLayouts like 'nameInputLayout', 'emailInputLayout', etc.
        IdNumberInput.clearFocus();
        IdNumberInput.setError(null);
        IdNumberInput.setHint(IdNumberInput.getHint()); // Refresh hint state

        emailInput.clearFocus();
        emailInput.setError(null);
        emailInput.setHint(emailInput.getHint());
        emailInput.setActivated(false);
        emailInput.setPressed(false);

        phoneInput.clearFocus();
        phoneInput.setError(null);
        phoneInput.setHint(phoneInput.getHint());

        DOBInput.clearFocus();
        DOBInput.setError(null);
        DOBInput.setHint(DOBInput.getHint());

        classYearInput.clearFocus();
        classYearInput.setError(null);
        classYearInput.setHint(classYearInput.getHint());

        etFacultyInput.clearFocus();
        etFacultyInput.setError(null);
        etFacultyInput.setHint(etFacultyInput.getHint());

        etDepartmentInput.clearFocus();
        etDepartmentInput.setError(null);
        etDepartmentInput.setHint(etDepartmentInput.getHint());

        etGenderInput.clearFocus();
        etGenderInput.setError(null);
        etGenderInput.setHint(etGenderInput.getHint());
        // Repeat for all TextInputLayouts if needed
    }

    public void getProfileApI(String endpoint, String id_number){
        FormBody.Builder formbody = new FormBody.Builder()
                .add("id_number", id_number);

        Request request = new Request.Builder()
                .url(baseURL + endpoint)
                .post(formbody.build())
                .build();

        new Thread(()->{
            try(Response response = client.newCall(request).execute()){
                int statusCode=response.code();
                Log.i("status code", String.valueOf(statusCode));
                String responseBody =response.body() != null ? response.body().string() : "null";
                if (response.isSuccessful()){
                    if (responseBody.equals("null")){

                    }else {
                        JSONObject jsonRespone = new JSONObject(responseBody);
                        Profile userProfile = parseJSONtoProfile(jsonRespone.toString());
                        getActivity().runOnUiThread(()->{
                            etIDNumber.setText(userProfile.getId_number());
                            etEmail.setText(userProfile.getEmail());
                            etPhone.setText(userProfile.getPhone_number());
                            etDateOfBirth.setText(String.valueOf(userProfile.getDate_of_birth()));
                            etGender.setText(userProfile.getGender().toString());
                            etFaculty.setText(userProfile.getFaculty());
                            etDepartment.setText(userProfile.getDepartment());
                            etClassYear.setText(userProfile.getClass_year());
                        });
                    }
                }else {

                }

            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private Profile parseJSONtoProfile(String jsonObjectString) {
        Gson gson = new Gson();
        Type type = new TypeToken<Profile>() {}.getType();
        return gson.fromJson(jsonObjectString, type);
    }
}