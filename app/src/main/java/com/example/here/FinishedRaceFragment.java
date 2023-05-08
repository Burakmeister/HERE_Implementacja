package com.example.here;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.here.sdk.mapview.MapView;

public class FinishedRaceFragment extends Fragment {

    public FinishedRaceFragment() {
        // Required empty public constructor
    }

    private TextView raceTime;
    private TabLayout tabLayout;
    private MapView mapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_finished_race, container, false);

        raceTime = view.findViewById(R.id.race_time);
        tabLayout = view.findViewById(R.id.ranking_tab);
        mapView = view.findViewById(R.id.map_view);

        this.mapView.onCreate(savedInstanceState);
        // Tutaj zimplementowac aby pokazywało w podglądzie trasę wyścigu



        return view;
    }
}