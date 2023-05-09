package com.example.here;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.here.routeCreator.PlatformPositioningProvider;
import com.example.here.routeCreator.RouteCreatorTrainingActive;
import com.example.here.routeCreator.RouteCreatorTrainingSuspended;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.here.sdk.routing.CalculateRouteCallback;
import com.here.sdk.routing.PedestrianOptions;
import com.here.sdk.routing.Route;
import com.here.sdk.routing.RoutingEngine;
import com.here.sdk.routing.RoutingError;
import com.here.sdk.routing.Waypoint;

import java.io.Serializable;
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

    private float curSpeed = 0;
    private float avgSpeed = 0;
    private float maxSpeed = 0;
    private float distance = 0;
    private int kcal = 0;

    private MapMeasure mapMeasureZoom;
    //    private android.location.Location startLocation;
    private PlatformPositioningProvider platformPositioningProvider ;
    private LocationIndicator locationIndicator;
    private float distanceUntilWaypoint = 10.0f;
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
                trainingActive = !trainingActive;

                if (trainingActive) {
                    pauseReturnButton.setText(R.string.paused);
                    pauseReturnButton.setBackgroundColor(getResources().getColor(R.color.orange));
                } else {
                    pauseReturnButton.setText(R.string.started);
                    pauseReturnButton.setBackgroundColor(getResources().getColor(R.color.green));
                }
            }
        });
        this.endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrainingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        startTrainingActivity();
        displayTrainingData();
    }

    private void initTrace(double[] array) {
        traceWaypoints.add(new Waypoint(new GeoCoordinates(platformPositioningProvider.getLastKnownLocation().getLatitude(), platformPositioningProvider.getLastKnownLocation().getLongitude())));
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
                new PedestrianOptions(),
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
                        } else {
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
//        Sensor gSensor = null, mSensor;
//        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        sensorManager.registerListener(this, gSensor,
//                SensorManager.SENSOR_DELAY_GAME);
//        sensorManager.registerListener(this, mSensor,
//                SensorManager.SENSOR_DELAY_GAME);
        platformPositioningProvider.startLocating(new PlatformPositioningProvider.PlatformLocationListener() {
            @Override
            public void onLocationUpdated(android.location.Location location) {
                Log.d("LOCALIZATION", "coords: " + location.getLatitude() + "  " + location.getLongitude());
                currentWaypoints.add(new Waypoint(convertLocation(location).coordinates));
                Location loc = convertLocation(location);
                loc.time = new Date();
//                loc.bearingInDegrees = 0d; //   COMPASS HERE
                locationIndicator.updateLocation(loc);

                mapView.getCamera().lookAt(
                        new GeoCoordinates(location.getLatitude(), location.getLongitude()), mapMeasureZoom);

                if(route!=null){
                    distanceLeft.setText("Pozostało: " + Math.round(route.getLengthInMeters()/10.0)/10.0 + " km");
                    if(avgSpeed>0)
                        timeLeft.setText(Math.round(route.getLengthInMeters()/avgSpeed/60) + " minut");
                }

                if(locationIndicator != null)
                    locationIndicator.updateLocation(LocationConverter.convertToHERE(location));

                float currentDistance = 0;

                if (trainingActive) {

                    distance += currentDistance;
                    avgSpeed = distance / timeInSec;
                    curSpeed = location.getSpeed();
                    if (curSpeed > maxSpeed)
                        maxSpeed = curSpeed;
                    displayTrainingData();
                }
            }
        });

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
                double distanceInMeters = 1000 * 10;
                mapMeasureZoom = new MapMeasure(MapMeasure.Kind.DISTANCE, distanceInMeters);

//                locationIndicator = new LocationIndicator();
//                locationIndicator.setLocationIndicatorStyle(LocationIndicator.IndicatorStyle.PEDESTRIAN);
//                locationIndicator.updateLocation(LocationConverter.convertToHERE(this.pastLocation)); // start
//
//
//                mapView.addLifecycleListener(locationIndicator);
                mapView.getCamera().lookAt(
                        new GeoCoordinates(platformPositioningProvider.getLastKnownLocation().getLatitude(), platformPositioningProvider.getLastKnownLocation().getLongitude()), mapMeasureZoom); //start
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
//        this.kcalTextView.setText(getString(R.string.kcalText, kcal));
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