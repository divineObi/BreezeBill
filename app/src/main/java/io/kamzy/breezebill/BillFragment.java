package io.kamzy.breezebill;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import io.kamzy.breezebill.SharedViewModels.BillShareViewModels;
import io.kamzy.breezebill.SharedViewModels.UsersGroupsSharedViewModel;
import io.kamzy.breezebill.adapters.BillAdapter;
import io.kamzy.breezebill.adapters.GroupAdapter;
import io.kamzy.breezebill.models.Bills;
import io.kamzy.breezebill.models.Groupss;
import io.kamzy.breezebill.tools.DataManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BillFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BillFragment extends Fragment {
    LinearLayout noBillImage;
    TabLayout billTabs;
    RecyclerView billRecyclerView;
    List<Bills> billList;
    BillShareViewModels billShareViewModels;
    BillAdapter billAdapter;
    private String currentTab = "ACTIVE";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BillFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BillFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BillFragment newInstance(String param1, String param2) {
        BillFragment fragment = new BillFragment();
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
        View view = inflater.inflate(R.layout.fragment_bill, container, false);


        noBillImage = view.findViewById(R.id.no_bill_screen);
        billTabs = view.findViewById(R.id.billTabLayout);
        billTabs.addTab(billTabs.newTab().setText("ACTIVE"));
        billTabs.addTab(billTabs.newTab().setText("PAID"));
        billRecyclerView = view.findViewById(R.id.billRecyclerView);
        billList = DataManager.getInstance().getUnpaidBills();


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        billRecyclerView.setLayoutManager(layoutManager);
        billRecyclerView.setItemAnimator(new DefaultItemAnimator());
        billAdapter = new BillAdapter(requireContext(), billList, "ACTIVE");
        billRecyclerView.setAdapter(billAdapter);
        DividerItemDecoration divider = new DividerItemDecoration(billRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        billRecyclerView.addItemDecoration(divider);


        if (billList!=null && !billList.isEmpty()){
            billAdapter.updateData(billList, "ACTIVE");
        }else {
            billAdapter.updateData(new ArrayList<>(), "ACTIVE");
        }

        toggleEmptyState(billList);

        billTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getText().toString();
                loadBills(currentTab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        billShareViewModels = new ViewModelProvider(requireActivity()).get(BillShareViewModels.class);
        billShareViewModels.getBillData().observe(requireActivity(), bills -> {

            noBillImage = view.findViewById(R.id.no_bill_screen);
            billTabs = view.findViewById(R.id.billTabLayout);
            billRecyclerView = view.findViewById(R.id.billRecyclerView);
            billList = bills;


            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            billRecyclerView.setLayoutManager(layoutManager);
            billRecyclerView.setItemAnimator(new DefaultItemAnimator());
            billAdapter = new BillAdapter(requireContext(), billList, "ACTIVE");
            billRecyclerView.setAdapter(billAdapter);
            DividerItemDecoration divider = new DividerItemDecoration(billRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
            billRecyclerView.addItemDecoration(divider);


            if (billList!=null && !billList.isEmpty()){
                billAdapter.updateData(billList, "ACTIVE");
            }else {
                billAdapter.updateData(new ArrayList<>(), "ACTIVE");
            }

            toggleEmptyState(billList);

            billTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    currentTab = tab.getText().toString();
                    loadBills(currentTab);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

        });


    }




    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            TabLayout tabLayout = getActivity().findViewById(R.id.groupTabLayout);
            if (tabLayout != null) {
                int currentTabPosition = tabLayout.getSelectedTabPosition();
                Log.d("TabInfo", "Current Tab Position: " + currentTabPosition);
                // Perform actions based on the selected tab
                loadBills(currentTab);
            }
        }
    }

    private void loadBills(String selectedTab) {
        if (selectedTab.equalsIgnoreCase("ACTIVE")) {
            billList = DataManager.getInstance().getUnpaidBills();
            billAdapter.updateData(billList, "ACTIVE");
            toggleEmptyState(billList);

        } else if (selectedTab.equals("PAID")) {
            billList = DataManager.getInstance().getPaidBills();
            billAdapter.updateData(billList, "PAID");
            toggleEmptyState(billList);
        }
    }


    private void toggleEmptyState(List<Bills> billList) {
        if (billList != null && !billList.isEmpty()) {
            noBillImage.setVisibility(View.GONE);
            billRecyclerView.setVisibility(View.VISIBLE);
        } else {
            noBillImage.setVisibility(View.VISIBLE);
            billRecyclerView.setVisibility(View.GONE);
        }
    }
}