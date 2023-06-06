package com.example.here;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.here.models.UserData;
import com.example.here.restapi.ApiInterface;
import com.example.here.restapi.Name;
import com.example.here.restapi.RegisterCredentials;
import com.example.here.restapi.RetrofitClient;
import com.example.here.restapi.Token;
import com.example.here.restapi.UserMass;
import com.example.here.routeCreator.PlatformPositioningProvider;
import com.here.sdk.core.Color;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.GeoPolyline;
import com.here.sdk.core.LanguageCode;
import com.here.sdk.core.Location;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.mapview.LocationIndicator;
import com.here.sdk.mapview.MapPolyline;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;
import com.here.sdk.routing.BicycleOptions;
import com.here.sdk.routing.CalculateRouteCallback;
import com.here.sdk.routing.Route;
import com.here.sdk.routing.RoutingEngine;
import com.here.sdk.routing.RoutingError;
import com.here.sdk.routing.Waypoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OngoingActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private Button endButton;
    private Button pauseReturnButton;
    private TextView curSpeedTextView;
    private TextView avgSpeedTextView;
    private TextView maxSpeedTextView;
    private TextView distanceTextView;
    private TextView timeTextView;
    private TextView kcalTextView;
    private TextView distanceLeft;
    private TextView timeLeft;
    private MapView mapView;

    private int timeInSec;
    private boolean trainingActive = true;

    private double curSpeed = 0;
    private double avgSpeed = 0;
    private double maxSpeed = 0;
    private double distance = 0;
    private int kcal = 0;

    private PlatformPositioningProvider platformPositioningProvider;
    private LocationIndicator locationIndicator;
    private Route route;
    private RoutingEngine routingEngine = null;
    private List<Waypoint> currentWaypoints = new ArrayList<>();
    List<Waypoint> traceWaypoints = new ArrayList<>();

    double[] traceInDoubles;

    private float mass;
    private boolean isTraining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ApiInterface apiInterface  = RetrofitClient.getInstance().create(ApiInterface.class);
        SharedPreferences sp = getSharedPreferences("msb", MODE_PRIVATE);
        String token = sp.getString("token", "");

        Call<UserMass> massCall = apiInterface.getMass("Token " + token);
        massCall.enqueue(new Callback<UserMass>() {
            @Override
            public void onResponse(Call<UserMass> call, Response<UserMass> response) {
                Log.d("api: " , "response");

                if (response.isSuccessful()) {
                    Float mass_downloaded = response.body().getMass();
                    mass = mass_downloaded.floatValue();
                    Log.d("api: " , "responseSuccess");
                } else {
                  mass = 70;
                  Log.d("api: " , "responseFailed");
                }
            }

            @Override
            public void onFailure(Call<UserMass> call, Throwable t) {
                mass = 70;
                Log.d("api: " , "failure");
            }
        });

        MapView.setPrimaryLanguage(LanguageCode.PL_PL);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        this.platformPositioningProvider = new PlatformPositioningProvider(OngoingActivity.this);

        traceInDoubles = getIntent().getDoubleArrayExtra("coords");
        isTraining = getIntent().getBooleanExtra("isTraining", true);
        setContentView(R.layout.training_activity);

        progressBar = findViewById(R.id.progressBar);
        this.mapView = findViewById(R.id.map_view);
        this.endButton = findViewById(R.id.end_trening);
        this.pauseReturnButton = findViewById(R.id.return_pause_training);
        this.timeTextView = findViewById(R.id.time);
        this.curSpeedTextView = findViewById(R.id.cur_speed);
        this.avgSpeedTextView = findViewById(R.id.avg_speed);
        this.maxSpeedTextView = findViewById(R.id.max_speed);
        this.distanceTextView = findViewById(R.id.distance);
        this.distanceLeft = findViewById(R.id.distance_left);
        this.kcalTextView = findViewById(R.id.kcal);
        this.timeLeft = findViewById(R.id.time_left);
        this.mapView.onCreate(savedInstanceState);

        this.pauseReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (trainingActive) {
                    pauseReturnButton.setText(R.string.paused);
                    pauseReturnButton.setBackgroundColor(getResources().getColor(R.color.orange));
                } else {
                    pauseReturnButton.setText(R.string.started);
                    pauseReturnButton.setBackgroundColor(getResources().getColor(R.color.green));
                }
                trainingActive = !trainingActive;
            }
        });
        this.endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isTraining){
                    // zapisywanie statystyk treningu
                    Intent intent = new Intent(OngoingActivity.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    // zapisywanie statystyk wyscigu
                }

            }
        });

        try {
            routingEngine = new RoutingEngine();
        } catch (InstantiationErrorException e) {
            throw new RuntimeException(e);
        }

        startTrainingActivity();
        displayTrainingData();
    }

    private void initTrace(double[] array) {
        Waypoint start = new Waypoint(
                new GeoCoordinates(platformPositioningProvider.getLastKnownLocation().getLatitude(),
                        platformPositioningProvider.getLastKnownLocation().getLongitude()));

        currentWaypoints.add(start);
        if(array != null){
            traceWaypoints.add(start);
            for(int i=0; i<array.length; i++){
                traceWaypoints.add(new Waypoint(new GeoCoordinates(array[i], array[++i])));
            }
            routingEngine.calculateRoute(
                    traceWaypoints,
                    new BicycleOptions(),
                    new CalculateRouteCallback() {
                        @Override
                        public void onRouteCalculated(@Nullable RoutingError routingError, @Nullable List<Route> routes) {
                            if (routingError == null) {
                                route = routes.get(0);
                                GeoPolyline routeGeoPolyline = route.getGeometry();

                                float widthInPixels = 10;
                                MapPolyline routeMapPolyline = new MapPolyline(routeGeoPolyline,
                                        widthInPixels,
                                        Color.valueOf(1,0,0)); // RGBA

                                mapView.getMapScene().addMapPolyline(routeMapPolyline);
                                loadMapScene();
                            }
                        }
                    });
        }
    }

    private void addLocationIndicator(GeoCoordinates geoCoordinates,
                                      LocationIndicator.IndicatorStyle indicatorStyle) {
        locationIndicator = new LocationIndicator();
        locationIndicator.setLocationIndicatorStyle(indicatorStyle);


        Location location = new Location(geoCoordinates);
        location.time = new Date();
        location.bearingInDegrees = 180d;

        locationIndicator.updateLocation(location);

        mapView.addLifecycleListener(locationIndicator);
    }

    private void startTrainingActivity() {
        initTrace(traceInDoubles);
        platformPositioningProvider.startLocating(new PlatformPositioningProvider.PlatformLocationListener() {
            @Override
            public void onLocationUpdated(android.location.Location location) {
                Location loc = convertLocation(location);
                loc.time = new Date();
                locationIndicator.updateLocation(loc);

                mapView.getCamera().lookAt(new GeoCoordinates(location.getLatitude(), location.getLongitude()));

                if(locationIndicator != null)
                    locationIndicator.updateLocation(LocationConverter.convertToHERE(location));

                if (trainingActive) {
                    if(route!=null){
                        distanceLeft.setText("Pozostało: " + (Math.round(route.getLengthInMeters()/10.0)/100.0 - Math.round(distance/10.0)/100.0)+ " km");
                        if(avgSpeed>0)
                            timeLeft.setText(Math.round(route.getLengthInMeters()/avgSpeed/60) + " minut");
                        if(distance > 0.75 * route.getLengthInMeters() && currentWaypoints.get(currentWaypoints.size()-1).coordinates.distanceTo(traceWaypoints.get(traceWaypoints.size()-1).coordinates)<50){
                            if(isTraining){
                                // zapisywanie statystyk treningu
                                Intent intent = new Intent(OngoingActivity.this, MainActivity.class);
                                startActivity(intent);
                            }else{
                                // zapisywanie statystyk wyscigu
                                // widok zakończonego wyścigu
                            }
                        }
                    }
                    currentWaypoints.add(new Waypoint(loc.coordinates));

                    routingEngine.calculateRoute(
                            currentWaypoints.subList(currentWaypoints.size()-2, currentWaypoints.size()),
                            new BicycleOptions(),
                            new CalculateRouteCallback() {
                                @Override
                                public void onRouteCalculated(@Nullable RoutingError routingError, @Nullable List<Route> routes) {
                                    if (routingError == null) {
                                        Route route = routes.get(0);
                                        GeoPolyline routeGeoPolyline = route.getGeometry();

                                        float widthInPixels = 10;
                                        MapPolyline routeMapPolyline = new MapPolyline(routeGeoPolyline,
                                                widthInPixels,
                                                Color.valueOf(0,1,0)); // RGBA

                                        mapView.getMapScene().addMapPolyline(routeMapPolyline);
                                        loadMapScene();
                                    }
                                }
                            });

                    distance += currentWaypoints.get(currentWaypoints.size()-1).coordinates.distanceTo(currentWaypoints.get(currentWaypoints.size()-2).coordinates);
                    avgSpeed = Math.round(distance/timeInSec*36)/10.0;
                    curSpeed = Math.round(location.getSpeed()*36)/10.0;
                    kcal = (int) (mass*timeInSec/60*(0.6345*avgSpeed*avgSpeed+0.7563*avgSpeed+36.725)/(3600));
                    if (curSpeed > maxSpeed)
                        maxSpeed = curSpeed;
                    displayTrainingData();
                }
            }
        });

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            this.addLocationIndicator(new GeoCoordinates(platformPositioningProvider.getLastKnownLocation().getLatitude(),
                            platformPositioningProvider.getLastKnownLocation().getLongitude()),
                    LocationIndicator.IndicatorStyle.PEDESTRIAN);
        }
        else {
            Log.d("PERM", "NO PERMISSIONS");
            finish();
        }
        new TimeMeasure().start();
        loadMapScene();
    }

    private Location convertLocation(android.location.Location nativeLocation) {
        GeoCoordinates geoCoordinates = new GeoCoordinates(
                nativeLocation.getLatitude(),
                nativeLocation.getLongitude(),
                nativeLocation.getAltitude());

        Location location = new Location(geoCoordinates);

        if (nativeLocation.hasBearing()) {
            location.bearingInDegrees = (double) nativeLocation.getBearing();
        }

        if (nativeLocation.hasSpeed()) {
            location.speedInMetersPerSecond = (double) nativeLocation.getSpeed();
        }

        if (nativeLocation.hasAccuracy()) {
            location.horizontalAccuracyInMeters = (double) nativeLocation.getAccuracy();
        }

        return location;
    }

    private void loadMapScene() {
        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, mapError -> {
            if (mapError == null) {
                mapView.getCamera().lookAt(
                        new GeoCoordinates(platformPositioningProvider.getLastKnownLocation().getLatitude(), platformPositioningProvider.getLastKnownLocation().getLongitude())); //start
            } else {
                Log.d("loadMapScene()", "Loading map failed: mapError: " + mapError.name());
            }
        });
    }

    private void displayTrainingData() {
        this.curSpeedTextView.setText(getString(R.string.curSpeedText, curSpeed));
        this.avgSpeedTextView.setText(getString(R.string.avgSpeedText, avgSpeed));
        this.maxSpeedTextView.setText(getString(R.string.maxSpeedText, maxSpeed));
        if(distance<1000){
            this.distanceTextView.setText(getString(R.string.distanceText, distance));
        }
        else{
            this.distanceTextView.setText(getString(R.string.distanceTextKm, Math.round(distance/10)/100.0));
        }
        this.kcalTextView.setText(getString(R.string.kcalText, kcal));
    }

    @Override
    protected void onPause() {
        if(this.mapView != null)
            this.mapView.onPause();
        platformPositioningProvider.stopLocating();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(this.mapView != null)
            this.mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if(this.mapView != null)
            this.mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        if(this.mapView != null)
            this.mapView.onSaveInstanceState(outState);
    }

//    private void saveRaceStats() {
//        startLoading();
//        ApiInterface apiInterface = RetrofitClient.getInstance().create(ApiInterface.class);
//        Call<Token> call = apiInterface.register(new RegisterCredentials(firstName, lastName, username, email, password));
//        call.enqueue(new Callback<Token>() {
//            @Override
//            public void onResponse(Call<Token> call, Response<Token> response) {
//                if (response.isSuccessful()) {
//                    String token = response.body().getToken();
//                    sp.edit().putString("token", token).commit();
//                    sp.edit().putBoolean("logged",true).apply();
//                    goToUserDataCreationActivity(username);
//                    stopLoading();
//                } else {
//                    stopLoading();
//                    Toast.makeText(getApplicationContext(), response.message(),Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Token> call, Throwable t) {
//                //handle network problems
//            }
//        });
//    }
//
//    private void saveTrainingStats() {
//
//        startLoading();
//
//        ApiInterface apiInterface = RetrofitClient.getInstance().create(ApiInterface.class);
//        Call<Token> call = apiInterface.register(new RegisterCredentials(firstName, lastName, username, email, password));
//        call.enqueue(new Callback<Token>() {
//            @Override
//            public void onResponse(Call<Token> call, Response<Token> response) {
//                if (response.isSuccessful()) {
//                    String token = response.body().getToken();
//                    sp.edit().putString("token", token).commit();
//                    sp.edit().putBoolean("logged",true).apply();
//                    stopLoading();
//                } else {
//                    stopLoading();
//                    Toast.makeText(getApplicationContext(), response.message(),Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Token> call, Throwable t) {
//                //handle network problems
//            }
//        });
//    }

    private void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void stopLoading() {
        progressBar.setVisibility(View.GONE);
    }

    private class TimeMeasure extends Thread{
        @Override
        public void run() {
            try {
                while(true)
                    if(trainingActive){
                        timeInSec++;
                        timeTextView.setText("Czas: "+String.format("%02d:%02d:%02d",timeInSec/3600,timeInSec%3600/60, timeInSec%60));
                        sleep(1000);
                    }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}