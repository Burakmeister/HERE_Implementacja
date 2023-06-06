package com.example.here;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.here.sdk.core.Anchor2D;
import com.here.sdk.core.Color;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.GeoPolyline;
import com.here.sdk.core.LanguageCode;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.mapview.LocationIndicator;
import com.here.sdk.mapview.MapImage;
import com.here.sdk.mapview.MapImageFactory;
import com.here.sdk.mapview.MapMarker;
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

import java.util.ArrayList;
import java.util.List;

public class SetRouteActivity extends AppCompatActivity {

    private MapView mapView;
    private Button resetRoute;
    private Button setRoute;
    private Button cancelButton;
    private Button skipSetRoute;

    private PermissionsRequestor permissionsRequestor;
    private android.location.Location pastLocation;
    private float distanceUntilWaypoint = 10.0f;
    private LocationIndicator locationIndicator;
    private List<Waypoint> currentWaypoints = new ArrayList<>();
    private List<MapPolyline> lines = new ArrayList<>();
    private List<MapMarker> markers = new ArrayList<>();

    private RoutingEngine routingEngine;

    private boolean isTraining;
    public SetRouteActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        MapView.setPrimaryLanguage(LanguageCode.PL_PL);

        setTheme(R.style.AppTheme);
        isTraining = getIntent().getBooleanExtra("isTraining", true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_route_activity);

        this.mapView = findViewById(R.id.map_view);
        this.mapView.onCreate(savedInstanceState);
        this.setLongPressGestureHandler(mapView);

        this.resetRoute = findViewById(R.id.reset_route);
        this.setRoute = findViewById(R.id.set_route);
        this.cancelButton = findViewById(R.id.cancel_button);
        this.locationIndicator = new LocationIndicator();

        this.skipSetRoute = findViewById(R.id.skip_route_button);

        handleAndroidPermissions();
        initialization();
    }

    private void initialization() {
        resetRoute.setOnClickListener(view -> {
            this.currentWaypoints.clear();
            mapView.getMapScene().removeMapPolylines(lines);
            lines.clear();
            currentWaypoints.clear();
            mapView.getMapScene().removeMapMarkers(this.markers);
            this.markers.clear();
            loadMapScene();
        });

        setRoute.setOnClickListener(view -> {
            // Zapisz zaznaczoną trasę
            if(currentWaypoints.size()<1){
                if(isTraining){
                    Toast.makeText(getApplicationContext(), R.string.please_complete_route, Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), R.string.please_complete_route_race, Toast.LENGTH_SHORT).show();
                }
            }else if(currentWaypoints.size()==1 && !isTraining){
                Toast.makeText(getApplicationContext(), R.string.please_complete_route_race, Toast.LENGTH_SHORT).show();
            } else{

                double []array = new double[currentWaypoints.size()*2];
                int j=0;
                for(int i=0; i<currentWaypoints.size(); i++){
                    array[j++] = currentWaypoints.get(i).coordinates.latitude;
                    array[j++] = currentWaypoints.get(i).coordinates.longitude;
                }
                if(isTraining){
                    Intent intent = new Intent(SetRouteActivity.this, OngoingActivity.class);
                    intent.putExtra("coords", array);
                    startActivity(intent);
                }else{
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("coords", array);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });

        cancelButton.setOnClickListener(view -> {
            // Anuluj i wyjdź z widoku
            if(isTraining) {
                Intent intent = new Intent(SetRouteActivity.this, MainActivity.class);
                startActivity(intent);
            }else{
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        if(isTraining){
            skipSetRoute.setOnClickListener(view -> {
                Intent intent = new Intent(SetRouteActivity.this, OngoingActivity.class);
                startActivity(intent);
            });
        }else{
            skipSetRoute.setVisibility(View.GONE);
        }

        try {
            routingEngine = new RoutingEngine();
        } catch (InstantiationErrorException e) {
            throw new RuntimeException("Initialization of RoutingEngine failed: " + e.error.name());
        }
    }

    private void loadMapScene() {
        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, mapError -> {
            if (mapError == null) {

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
                        new GeoCoordinates(latitude, longitude)); //start
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
                .setMinUpdateIntervalMillis(100)
                .setMaxUpdateDelayMillis(500)
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
                        MapImage mapImage = MapImageFactory.fromResource(getResources(), R.drawable.point);

                        Anchor2D anchor2D = new Anchor2D(0.5F, 1);
                        MapMarker mapMarkerEnd = new MapMarker(this.currentWaypoints.get(currentWaypoints.size()-1).coordinates, mapImage, anchor2D);
                        mapView.getMapScene().addMapMarker(mapMarkerEnd);
                        markers.add(mapMarkerEnd);
                    }else if(currentWaypoints.size()==1){
                        MapImage mapImage = MapImageFactory.fromResource(getResources(), R.drawable.point);

                        Anchor2D anchor2D = new Anchor2D(0.5F, 1);
                        MapMarker mapMarker = new MapMarker(this.currentWaypoints.get(0).coordinates, mapImage, anchor2D);
                        mapView.getMapScene().addMapMarker(mapMarker);
                        markers.add(mapMarker);
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