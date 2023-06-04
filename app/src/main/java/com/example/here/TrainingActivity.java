package com.example.here;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.here.routeCreator.PlatformPositioningProvider;
import com.here.sdk.core.Color;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.GeoPolyline;
import com.here.sdk.core.Location;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.mapview.LocationIndicator;
import com.here.sdk.mapview.MapMeasure;
import com.here.sdk.mapview.MapPolyline;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;
import com.here.sdk.routing.BicycleOptions;
import com.here.sdk.routing.CalculateRouteCallback;
import com.here.sdk.routing.PedestrianOptions;
import com.here.sdk.routing.Route;
import com.here.sdk.routing.RoutingEngine;
import com.here.sdk.routing.RoutingError;
import com.here.sdk.routing.Waypoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrainingActivity extends AppCompatActivity {

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
    private List<Waypoint> currentWaypoints = new ArrayList<>();
    List<Waypoint> traceWaypoints = new ArrayList<>();
    private Bundle savedInstanceState;

    double[] traceInDoubles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        traceInDoubles = getIntent().getDoubleArrayExtra("coords");
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.training_activity);
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

                // ekran zakończonego wyścigu

                Intent intent = new Intent(TrainingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        startTrainingActivity();
        displayTrainingData();
    }

    private void initTrace(double[] array) {
        Waypoint start = new Waypoint(new GeoCoordinates(platformPositioningProvider.getLastKnownLocation().getLatitude(), platformPositioningProvider.getLastKnownLocation().getLongitude()));
        currentWaypoints.add(start);
        traceWaypoints.add(start);
        for(int i=0; i<array.length; i++){
            traceWaypoints.add(new Waypoint(new GeoCoordinates(array[i], array[++i])));
        }
        RoutingEngine routingEngine = null;
        try {
            routingEngine = new RoutingEngine();
        } catch (InstantiationErrorException e) {
            throw new RuntimeException(e);
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
        this.platformPositioningProvider = new PlatformPositioningProvider(TrainingActivity.this);
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
                        distanceLeft.setText("Pozostało: " + (Math.round(route.getLengthInMeters()/100.0)/10.0 - Math.round(distance/100.0)/10.0)+ " km");
                        if(avgSpeed>0)
                            timeLeft.setText(Math.round(route.getLengthInMeters()/avgSpeed/60) + " minut");
                        if(distance>route.getLengthInMeters()/2 && loc.coordinates.distanceTo(traceWaypoints.get(traceWaypoints.size()-1).coordinates)<100){
                            // widok zakończonego wyścigu
                        }
                    }
                    currentWaypoints.add(new Waypoint(loc.coordinates));
                    distance += currentWaypoints.get(currentWaypoints.size()-1).coordinates.distanceTo(currentWaypoints.get(currentWaypoints.size()-2).coordinates);
                    avgSpeed = Math.round(distance/timeInSec*3.6);
                    curSpeed = Math.round(location.getSpeed()*3.6);
                    int masa = 80;
                    kcal = (int) (masa*timeInSec/60*(0.6345*avgSpeed*avgSpeed+0.7563*avgSpeed+36.725)/(3600));     // masa ciała potrzebna
                    if (curSpeed > maxSpeed)
                        maxSpeed = curSpeed;
                    displayTrainingData();
                }
            }
        });
        if(traceInDoubles!=null)
            initTrace(traceInDoubles);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            this.addLocationIndicator(new GeoCoordinates(platformPositioningProvider.getLastKnownLocation().getLatitude(),platformPositioningProvider.getLastKnownLocation().getLongitude()),
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
        this.distanceTextView.setText(getString(R.string.distanceText, distance));
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