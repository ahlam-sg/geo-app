package com.example.geo_app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CreditsFragment extends Fragment {

    private ListView listView;

    public CreditsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_credits, container, false);
        listView = rootView.findViewById(R.id.credits_list_view);
        String[] creditsArray = getResources().getStringArray(R.array.credits_array);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.credits_list_item, creditsArray);
        listView.setAdapter(arrayAdapter);
        return rootView;
    }
}