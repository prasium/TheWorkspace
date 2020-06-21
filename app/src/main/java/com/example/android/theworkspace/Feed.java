package com.example.android.theworkspace;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class Feed extends Fragment {


    private View view;
    String viewChoice;
    Spinner viewSet;
    public Feed() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_feed, container, false);
//        fetch();

        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.view_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        viewSet=view.findViewById(R.id.setView);
        viewSet.setAdapter(adapter);
        viewChoice="All";
        viewSet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                viewChoice= viewSet.getSelectedItem().toString();
                Log.d("tesjjt","abc"+viewChoice);
                Bundle bunde = new Bundle();
                Log.d("bebeee","o o "+viewChoice);
                bunde.putString("vie",viewChoice);
                Fragment childFragment = new FeedView();
                childFragment.setArguments(bunde);
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.child_fragment_container, childFragment).commit();

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                viewChoice="All";
               Log.d("dk","whzza");

            }
        });
        Log.d("idk","wha");

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Bundle bunde = new Bundle();
        Log.d("bebeee","o o "+viewChoice);
        bunde.putString("vie",viewChoice);
        Fragment childFragment = new FeedView();
        childFragment.setArguments(bunde);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.child_fragment_container, childFragment).commit();
    }

}





