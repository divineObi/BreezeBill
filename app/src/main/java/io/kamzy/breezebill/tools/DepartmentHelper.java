package io.kamzy.breezebill.tools;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.HashMap;
import java.util.Map;

import io.kamzy.breezebill.R;

public class DepartmentHelper {
    private final Context context;
    private final Map<String, Integer> facultyDepartmentMap;

    public DepartmentHelper(Context context) {
        this.context = context;
        this.facultyDepartmentMap = initializeFacultyDepartmentMap();
    }

    private Map<String, Integer> initializeFacultyDepartmentMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put("SICT", R.array.SICT_list);
        map.put("SOPS", R.array.SOPS_list);
        // Add more faculties and their corresponding array resources here
        return map;
    }

    public void populateDepartmentDropdown(String selectedFaculty, AutoCompleteTextView departmentAutoCompleteTextView) {
        String[] departmentList = getDepartmentList(selectedFaculty);

        if (departmentList != null) {
            CustomArrayAdapter departmentAdapter = new CustomArrayAdapter(
                    context,
                    android.R.layout.simple_dropdown_item_1line,
                    departmentList
            );
            departmentAutoCompleteTextView.setAdapter(departmentAdapter);
        } else {
            // Handle the case where the selected faculty is not found
            // For example, you can clear the adapter or show an error message
            departmentAutoCompleteTextView.setAdapter(null);
        }
    }

    private String[] getDepartmentList(String selectedFaculty) {
        Integer arrayResourceId = facultyDepartmentMap.get(selectedFaculty);
        if (arrayResourceId != null) {
            return context.getResources().getStringArray(arrayResourceId);
        }
        return null;
    }
}
