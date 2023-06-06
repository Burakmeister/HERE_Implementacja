package com.example.here;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ShareActionProvider;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.here.restapi.ApiInterface;
import com.example.here.restapi.Coordinates;
import com.example.here.restapi.RetrofitClient;
import com.example.here.restapi.TrainingStats;
import com.example.here.restapi.Username;
import com.example.here.routeCreator.RouteCreator;
import com.example.here.routeCreator.RouteCreatorTrainingSuspended;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;
import com.here.sdk.routing.Waypoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RunFragment extends Fragment {

    private View view;

    private TextView duration, avgSpeed, maxSpeed, distance, kcal;
    private MapView mapView;
    private Button startTraining;

    private RouteCreator routeCreator;

    private Spinner spinner;

    ProgressBar progressBar;
    LinearLayout linearLayout;
    TextView noStatsView;
    LinearLayout statsView;
    SharedPreferences sp;
    ApiInterface apiInterface;

    public RunFragment(){
        // require a empty public constructor
    }

    private void displayStatistics(int number) {

        progressBar.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);

        String token = sp.getString("token", "");

        Call<TrainingStats> statsCall = apiInterface.getTrainingStatistics("Token " + token, number);
        Call<List<Coordinates>> coordCall = apiInterface.getLastRoute("Token " + token);

        AtomicInteger finishedLoading = new AtomicInteger(0);
        statsCall.enqueue(new Callback<TrainingStats>() {
            @Override
            public void onResponse(Call<TrainingStats> call, Response<TrainingStats> response) {

                if(response.code() == 204) {
                    noStatsView.setVisibility(View.VISIBLE);
                    statsView.setVisibility(View.GONE);
                    if(finishedLoading.incrementAndGet() == 2)
                        stopLoading();
                    return;
                }

                noStatsView.setVisibility(View.GONE);
                statsView.setVisibility(View.VISIBLE);

                TrainingStats trainingStats = response.body();
                if(trainingStats!=null){
                    duration.setText(getString(R.string.duration, trainingStats.getDuration()));
                    distance.setText(getString(R.string.distanceText, trainingStats.getDistance()));
                    kcal.setText(getString(R.string.kcalText, trainingStats.getCalories()));
                    float avgSpeedVal = trainingStats.getDistance()/trainingStats.getDuration();
                    avgSpeed.setText(getString(R.string.avgSpeedText, avgSpeedVal));
                }

                if(finishedLoading.incrementAndGet() == 2)
                    stopLoading();
            }

            @Override
            public void onFailure(Call<TrainingStats> call, Throwable t) {

            }
        });

        coordCall.enqueue(new Callback<List<Coordinates>>() {
            @Override
            public void onResponse(Call<List<Coordinates>> call, Response<List<Coordinates>> response) {

                if(response.code() == 204) {
                    mapView.setVisibility(View.GONE);
                    if(finishedLoading.incrementAndGet() == 2)
                        stopLoading();
                    return;
                }

                mapView.setVisibility(View.VISIBLE);

                List<Coordinates> coordinates = response.body();

                if(coordinates!=null){
                    List<Waypoint> waypoints = new ArrayList<>();

                    for(Coordinates c : coordinates) {
                        waypoints.add(new Waypoint(
                                new GeoCoordinates(c.getX(), c.getY())
                        ));
                    }

                    routeCreator.createRoute(waypoints);
                }

                if(finishedLoading.incrementAndGet() == 2)
                    stopLoading();

            }

            @Override
            public void onFailure(Call<List<Coordinates>> call, Throwable t) {

            }
        });

    }

    private void stopLoading() {
        progressBar.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_run, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        linearLayout = view.findViewById(R.id.trainingData);
        noStatsView = view.findViewById(R.id.noStatsTextView);
        statsView = view.findViewById(R.id.statsView);

        sp = getActivity().getSharedPreferences("msb", Context.MODE_PRIVATE);

        apiInterface = RetrofitClient.getInstance().create(ApiInterface.class);

        this.mapView = view.findViewById(R.id.map_view);
        this.startTraining = view.findViewById(R.id.start_new_training);

        this.duration = view.findViewById(R.id.duration);
        this.avgSpeed = view.findViewById(R.id.avg_speed);
//        this.maxSpeed = view.findViewById(R.id.max_speed);
        this.distance = view.findViewById(R.id.distance);
        this.kcal = view.findViewById(R.id.kcal);

        this.startTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SetRouteActivity.class);
                startActivity(intent);
            }
        });

        this.mapView.onCreate(savedInstanceState);

        this.routeCreator = new RouteCreatorTrainingSuspended(mapView);

        this.spinner = view.findViewById(R.id.statistics_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // Ostatni trening
                        // Kod do wyświetlenia statystyk dla ostatniego treningu
                        displayStatistics(1);
                        break;
                    case 1: // Ostatnie 5 treningów
                        // Kod do wyświetlenia statystyk dla ostatnich 5 treningów
                        displayStatistics(5);
                        break;
                    case 2: // Ostatnie 10 treningów
                        // Kod do wyświetlenia statystyk dla ostatnich 10 treningów
                        displayStatistics(10);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nic nie rób
            }
        });

        return view;
    }

//    @Override
//    public void onPause() {
//        mapView.onPause();
//        super.onPause();
//    }
//
//    @Override
//    public void onResume() {
//        mapView.onResume();
//        super.onResume();
//    }
//
//    @Override
//    public void onDestroy() {
//        mapView.onDestroy();
//        super.onDestroy();
//    }

//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        mapView.onSaveInstanceState(outState);
//        super.onSaveInstanceState(outState);
//    }
}