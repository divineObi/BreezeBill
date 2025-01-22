package io.kamzy.breezebill;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

import io.kamzy.breezebill.adapters.GroupAdapter;
import io.kamzy.breezebill.models.Groupss;
import io.kamzy.breezebill.tools.DataManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupFragment extends Fragment {
    LinearLayout noGroupImage;
    TabLayout groupTabs;
    RecyclerView groupRecyclerView;
    List<Groupss> groupList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupFragment newInstance(String param1, String param2) {
        GroupFragment fragment = new GroupFragment();
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
        return inflater.inflate(R.layout.fragment_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        noGroupImage = view.findViewById(R.id.no_group_screen);
        groupTabs = view.findViewById(R.id.groupTabLayout);
        groupRecyclerView = view.findViewById(R.id.groupRecyclerView);

        if (groupList != null){
            noGroupImage.setVisibility(View.GONE);
            groupRecyclerView.setVisibility(View.VISIBLE);
        } else {
            noGroupImage.setVisibility(View.VISIBLE);
            groupRecyclerView.setVisibility(View.GONE);
        }

        // Set up RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        groupRecyclerView.setLayoutManager(layoutManager);
        groupRecyclerView.setItemAnimator(new DefaultItemAnimator());

        groupTabs.addTab(groupTabs.newTab().setText("MY GROUPS"));
        groupTabs.addTab(groupTabs.newTab().setText("EXPLORE GROUPS"));

        groupTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadGroups(tab.getText().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//        Load initial Group List
        loadGroups("MY GROUPS");


    }

    private void loadGroups(String selectedTab) {
        if (selectedTab.equals("MY GROUPS")){
//            groupList = DataManager.getInstance().getAllGroups();
            if (groupList != null){
                noGroupImage.setVisibility(View.GONE);
                groupRecyclerView.setVisibility(View.VISIBLE);
                GroupAdapter groupAdapter = new GroupAdapter(requireContext(), groupList,  "MY_GROUPS", groupss -> {


                });
                groupRecyclerView.setAdapter(groupAdapter);
                groupAdapter.updateData(groupList);
                DividerItemDecoration divider = new DividerItemDecoration(groupRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
                groupRecyclerView.addItemDecoration(divider);
            } else {
                noGroupImage.setVisibility(View.VISIBLE);
                groupRecyclerView.setVisibility(View.GONE);
            }
        } else if (selectedTab.equals("EXPLORE GROUPS")){
            groupList = DataManager.getInstance().getAllGroups();
            if (groupList != null) {
                noGroupImage.setVisibility(View.GONE);
                groupRecyclerView.setVisibility(View.VISIBLE);
                GroupAdapter groupAdapter = new GroupAdapter(requireContext(), groupList, "EXPLORE_GROUPS", groupss -> {

                });
                groupRecyclerView.setAdapter(groupAdapter);
                groupAdapter.updateData(groupList);
                DividerItemDecoration divider = new DividerItemDecoration(groupRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
                groupRecyclerView.addItemDecoration(divider);
            } else {
                noGroupImage.setVisibility(View.VISIBLE);
                groupRecyclerView.setVisibility(View.GONE);
            }
        }
    }
}