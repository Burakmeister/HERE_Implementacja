package com.example.here;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.Location;
import com.here.sdk.core.engine.SDKNativeEngine;
import com.here.sdk.core.engine.SDKOptions;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.mapview.LocationIndicator;
import com.here.sdk.mapview.MapMeasure;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private PermissionsRequestor permissionsRequestor;
    private PlatformPositioningProvider platformPositioningProvider;
    private LocationIndicator locationIndicator;
    private MapMeasure mapMeasureZoom;
    private android.location.Location startLocation;
    private android.location.Location pastLocation;
    private float distance = 0.0f;
    private TextView speedText, avgSpeedText, distanceText, timeText;
    private float speed = 0, avgSpeed = 0;
    private long time, startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeHERESDK();

        setContentView(R.layout.activity_main);

        handleAndroidPermissions();
        platformPositioningProvider = new PlatformPositioningProvider(MainActivity.this);
        this.startLocation = platformPositioningProvider.getLastKnownLocation();
        this.pastLocation = startLocation;

        this.speedText = findViewById(R.id.speedTextView);
        this.avgSpeedText = findViewById(R.id.avgSpeedTextView);
        this.distanceText = findViewById(R.id.distanceTextView);
        this.timeText = findViewById(R.id.timeTextView);

        this.mapView = findViewById(R.id.map_view);
        this.mapView.onCreate(savedInstanceState);
        this.loadMapScene();

        displayData();

        startTime = System.nanoTime();
        platformPositioningProvider.startLocating(new PlatformPositioningProvider.PlatformLocationListener() {
            @Override
            public void onLocationUpdated(android.location.Location location) {
                mapView.getCamera().lookAt(
                        new GeoCoordinates(location.getLatitude(), location.getLongitude()), mapMeasureZoom);
                locationIndicator.updateLocation(convertLocation(location));

                distance += pastLocation.distanceTo(location);
                pastLocation = new android.location.Location(location);
                
                time = (System.nanoTime() - startTime) / 1_000_000_000;

                avgSpeed = distance/time;

                speed = location.getSpeed();

                displayData();
                
            }
        });
    }
    
    private void displayData() {
        speedText.setText(getString(R.string.speed, speed));
        distanceText.setText(getString(R.string.distance, distance));
        avgSpeedText.setText(getString(R.string.avg, avgSpeed));
        timeText.setText(getString(R.string.time, time));
    }

    private void initializeHERESDK() {
        String accessKeyID = "-BT3t43rvbrnst8P5yJBig";
        String accessKeySecret = "a3jWnLaAjsC10STdlzEgqsGrncFC18grLmXjS5MTBfAFqjV4e615e_67iNqnYfbedjcle3CQavPptu7L9c95KA";
        SDKOptions options = new SDKOptions(accessKeyID, accessKeySecret);
        try {
            Context context = this;
            SDKNativeEngine.makeSharedInstance(context, options);
            Log.d("HERE_initialize: ", "easy peazy");
        } catch (InstantiationErrorException e) {
            Log.d("HERE_initialize: ", "failure");
            throw new RuntimeException("Initialization of HERE SDK failed: " + e.error.name());
        }
    }

    private void disposeHERESDK() {
        SDKNativeEngine sdkNativeEngine = SDKNativeEngine.getSharedInstance();
        if (sdkNativeEngine != null) {
            sdkNativeEngine.dispose();
            SDKNativeEngine.setSharedInstance(null);
        }
    }

    private void loadMapScene() {
        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, mapError -> {
            if (mapError == null) {
                double distanceInMeters = 1000 * 10;
                mapMeasureZoom = new MapMeasure(MapMeasure.Kind.DISTANCE, distanceInMeters);

                locationIndicator = new LocationIndicator();
                locationIndicator.setLocationIndicatorStyle(LocationIndicator.IndicatorStyle.PEDESTRIAN);
                locationIndicator.updateLocation(convertLocation(startLocation));

                mapView.addLifecycleListener(locationIndicator);
                mapView.getCamera().lookAt(
                        new GeoCoordinates(startLocation.getLatitude(), startLocation.getLongitude()), mapMeasureZoom);
            } else {
                Log.d("loadMapScene()", "Loading map failed: mapError: " + mapError.name());
            }
        });
    }

    private void handleAndroidPermissions() {
        permissionsRequestor = new PermissionsRequestor(this);
        permissionsRequestor.request(new PermissionsRequestor.ResultListener(){

            @Override
            public void permissionsGranted() {
            }

            @Override
            public void permissionsDenied() {
                Toast.makeText(getApplicationContext(),"Cannot start app: Location service and permissions are needed for this app.",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
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

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        disposeHERESDK();
        platformPositioningProvider.stopLocating();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        mapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
}