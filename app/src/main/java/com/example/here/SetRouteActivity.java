package com.example.here;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.here.routeCreator.RouteCreator;
import com.example.here.routeCreator.RouteCreatorTrainingSuspended;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;
import com.here.sdk.core.Color;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.GeoPolyline;
import com.here.sdk.core.Point2D;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.gestures.GestureState;
import com.here.sdk.gestures.LongPressListener;
import com.here.sdk.mapview.LocationIndicator;
import com.here.sdk.mapview.MapMeasure;
import com.here.sdk.mapview.MapPolyline;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;
import com.here.sdk.routing.BicycleOptions;
import com.here.sdk.routing.CalculateRouteCallback;
import com.here.sdk.routing.CarOptions;
import com.here.sdk.routing.PedestrianOptions;
import com.here.sdk.routing.Route;
import com.here.sdk.routing.RoutingEngine;
import com.here.sdk.routing.RoutingError;
import com.here.sdk.routing.Waypoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SetRouteActivity extends AppCompatActivity {

    private MapView mapView;
    private Button resetRoute;
    private Button setRoute;
    private Button cancelButton;
    private Button skipSetRoute;

    private PermissionsRequestor permissionsRequestor;
    private MapMeasure mapMeasureZoom;
    private android.location.Location pastLocation;
    private float distanceUntilWaypoint = 10.0f;
    private LocationIndicator locationIndicator;
    private List<Waypoint> currentWaypoints = new ArrayList<>();
    private List<MapPolyline> lines = new ArrayList<>();

    private RoutingEngine routingEngine;
    public SetRouteActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
//        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.set_route_activity);

        this.mapView = findViewById(R.id.map_view);
        this.mapView.onCreate(savedInstanceState);
        this.setLongPressGestureHandler(mapView);

        this.resetRoute = findViewById(R.id.reset_route);
        this.setRoute = findViewById(R.id.set_route);
        this.cancelButton = findViewById(R.id.cancel_button);
        this.skipSetRoute = findViewById(R.id.skip_route_button);
        this.locationIndicator = new LocationIndicator();

        handleAndroidPermissions();
        initialization();
    }

    private void initialization() {
        resetRoute.setOnClickListener(view -> {
            this.currentWaypoints.clear();
            mapView.getMapScene().removeMapPolylines(lines);
            lines.clear();
            currentWaypoints.clear();
            loadMapScene();
        });

        setRoute.setOnClickListener(view -> {
            // Zapisz zaznaczoną trasę
            Intent intent = new Intent(SetRouteActivity.this, TrainingActivity.class);
            double []array = new double[currentWaypoints.size()*2];
            int j=0;
            for(int i=0; i<currentWaypoints.size(); i++){
                array[j++] = currentWaypoints.get(i).coordinates.latitude;
                array[j++] = currentWaypoints.get(i).coordinates.longitude;
            }
            intent.putExtra("coords", array);
            startActivity(intent);
        });

        cancelButton.setOnClickListener(view -> {
            // Anuluj i wyjdź z widoku
            Intent intent = new Intent(SetRouteActivity.this, MainActivity.class);
            startActivity(intent);
        });

        skipSetRoute.setOnClickListener(view -> {
            Intent intent = new Intent(SetRouteActivity.this, TrainingActivity.class);
            startActivity(intent);
        });

        try {
            routingEngine = new RoutingEngine();
        } catch (InstantiationErrorException e) {
            throw new RuntimeException("Initialization of RoutingEngine failed: " + e.error.name());
        }
    }

    private void loadMapScene() {
        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, mapError -> {
            if (mapError == null) {
                double distanceInMeters = 1000 * 10;
                mapMeasureZoom = new MapMeasure(MapMeasure.Kind.DISTANCE, distanceInMeters);

                locationIndicator = new LocationIndicator();
                locationIndicator.setLocationIndicatorStyle(LocationIndicator.IndicatorStyle.PEDESTRIAN);
                locationIndicator.updateLocation(LocationConverter.convertToHERE(this.pastLocation)); // start

                double latitude, longitude;
                if(this.currentWaypoints.size()>0){
                    latitude = currentWaypoints.get(currentWaypoints.size()-1).coordinates.latitude;
                    longitude = currentWaypoints.get(currentWaypoints.size()-1).coordinates.longitude;
                }else{
                    latitude = pastLocation.getLatitude();
                    longitude = pastLocation.getLongitude();
                }
                mapView.addLifecycleListener(locationIndicator);
                mapView.getCamera().lookAt(
                        new GeoCoordinates(latitude , longitude), mapMeasureZoom); //start
            } else {
                Log.d("loadMapScene()", "Loading map failed: mapError: " + mapError.name());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsRequestor.onRequestPermissionsResult(requestCode, grantResults);
    }

    private void handleAndroidPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            setUpMap();
            return;
        }
        permissionsRequestor = new PermissionsRequestor(this);
        permissionsRequestor.request(new PermissionsRequestor.ResultListener(){

            @Override
            public void permissionsGranted() {
                 setUpMap();
            }

            @Override
            public void permissionsDenied() {
                Toast.makeText(getApplicationContext(),"Cannot start app: Location service and permissions are needed for this app.",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setUpMap() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(500)
                .setMaxUpdateDelayMillis(1000)
                .build();
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> pastLocation = location);

        }
        else {
            Log.d("PERM", "NO PERMISSIONS");
            finish();
        }
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
//                mapView.onCreate(savedInstanceState);
//                mapView.getCamera().lookAt(
//                        new GeoCoordinates(location.getLatitude(), location.getLongitude()), mapMeasureZoom);
                if (locationIndicator != null)
                    locationIndicator.updateLocation(LocationConverter.convertToHERE(location));

                float currentDistance = pastLocation.distanceTo(location);
                distanceUntilWaypoint -= currentDistance;

                if (distanceUntilWaypoint <= 0) {
                    distanceUntilWaypoint = 10.0f;
                    currentWaypoints.add(new Waypoint(new GeoCoordinates(location.getLatitude(), location.getLongitude())));
                }
            }
        };
        loadMapScene();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        mapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    private void setLongPressGestureHandler(MapView mapView) {
        mapView.getGestures().setLongPressListener((gestureState, touchPoint) -> {
            switch(gestureState){
                case BEGIN:
                    this.currentWaypoints.add(new Waypoint(mapView.viewToGeoCoordinates(touchPoint)));
                    if(currentWaypoints.size()>1){
                        routingEngine.calculateRoute(
                                currentWaypoints,
                                new PedestrianOptions(),
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
                                            lines.add(routeMapPolyline);
                                            loadMapScene();
                                        } else {
                                        }
                                    }
                                });
                    }
                    break;
                case UPDATE:
                    break;
                case END:
                    break;
                case CANCEL:
                    break;
            }
        });
    }

}