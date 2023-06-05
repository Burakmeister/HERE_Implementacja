package com.example.here;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

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
import com.here.sdk.routing.BicycleOptions;
import com.here.sdk.routing.CalculateRouteCallback;
import com.here.sdk.routing.Route;
import com.here.sdk.routing.RoutingEngine;
import com.here.sdk.routing.RoutingError;
import com.here.sdk.routing.Waypoint;

import java.util.ArrayList;
import java.util.List;

public class CreateRaceFragment extends Fragment {

    public CreateRaceFragment() {
        // Required empty public constructor
    }

    private EditText nameEditText;
    private EditText numParticipantsEditText;
    private EditText cityEditText;
    private MapView mapView;      //narazie zakomentowane bo bez implementacji wywalało
    private Button savedRoutesButton, createRouteButton, selectDateTimeButton, createRaceButton;

    private List<Waypoint> trace = new ArrayList<>();       // trasa w wyścigu do zapisania w bazie ( trace.get(nr).coordinates longitude latitude)
    private List<MapPolyline> lines = new ArrayList<>();
    private List<MapMarker> markers = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MapView.setPrimaryLanguage(LanguageCode.PL_PL);
        View view = inflater.inflate(R.layout.fragment_create_race, container, false);

        nameEditText = view.findViewById(R.id.name_edit_text);
        numParticipantsEditText = view.findViewById(R.id.num_participants_edit_text);
        cityEditText = view.findViewById(R.id.city_edit_text);
        mapView = view.findViewById(R.id.map_view);

        createRouteButton = view.findViewById(R.id.create_route_button);
        selectDateTimeButton = view.findViewById(R.id.select_date_time_button);
        createRaceButton = view.findViewById(R.id.create_race_button);

        this.mapView.onCreate(savedInstanceState);
        initTrace(null);

        createRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SetRouteActivity.class);
                intent.putExtra("isTraining", false);
                startActivityForResult(intent, 1);
            }
        });

        selectDateTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Wybierz datę i godzinę
                // Tworzenie widoku okna dialogowego
                RelativeLayout dateTimePickerLayout = new RelativeLayout(getActivity());
                dateTimePickerLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

                // Tworzenie widoku dla DatePicker
                final DatePicker datePicker = new DatePicker(getActivity());
                datePicker.setId(View.generateViewId());
                RelativeLayout.LayoutParams datePickerParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                dateTimePickerLayout.addView(datePicker, datePickerParams);

                // Tworzenie okna dialogowego z DatePicker
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Wybierz datę:");
                builder.setView(dateTimePickerLayout);
                builder.setPositiveButton("Dalej", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Zapisanie wybranej daty i przygotowanie okna dialogowego z TimePicker
                        final int year = datePicker.getYear();
                        final int month = datePicker.getMonth();
                        final int day = datePicker.getDayOfMonth();

                        RelativeLayout timePickerLayout = new RelativeLayout(getActivity());
                        timePickerLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

                        // Tworzenie widoku dla TimePicker
                        final TimePicker timePicker = new TimePicker(getActivity());
                        timePicker.setId(View.generateViewId());
                        RelativeLayout.LayoutParams timePickerParams = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT
                        );
                        timePickerLayout.addView(timePicker, timePickerParams);

                        // Tworzenie okna dialogowego z TimePicker
                        AlertDialog.Builder timePickerBuilder = new AlertDialog.Builder(getActivity());
                        timePickerBuilder.setTitle("Wybierz godzinę:");
                        timePickerBuilder.setView(timePickerLayout);
                        timePickerBuilder.setPositiveButton("Potwierdź", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Pobranie wybranej godziny i minuty
                                int hour = timePicker.getCurrentHour();
                                int minute = timePicker.getCurrentMinute();
                                // Tutaj zrobić coś z pobranymi danymi


                            }
                        });
                        timePickerBuilder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        timePickerBuilder.show();
                    }
                });
                builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        createRaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Utwórz wyścig i powróć do poprzedniego widoku

                Fragment newFragment = new MyRacesFragment(); //utworzenie nowej instancji klasy MyRacesFragment
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                Toast.makeText(getActivity(), "Wyścig utworzono pomyślnie!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
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

    // Metoda do pobrania nazw zapisanych tras
    private List<String> getTrackTitles(PopupMenu popup) {
        List<String> data = new ArrayList<String>();
        // Tutaj dodanie danych do listy, narazie na sztywno


        data.add("Trasa 1");
        data.add("Trasa 2");
        data.add("Trasa 3");

        // Pobierz Menu
        Menu menu = popup.getMenu();

        // Dodaj nowe elementy do menu
        for (String title : data) {
            menu.add(title);
        }

        return data;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("onActivityResult", ""+requestCode);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                this.trace.clear();
                mapView.getMapScene().removeMapPolylines(lines);
                mapView.getMapScene().removeMapMarkers(markers);
                initTrace(data.getDoubleArrayExtra("coords"));
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                this.trace.clear();
                initTrace(null);
            }
        }
    }

    private void initTrace(double[] array) {
        Log.d("initTrace", "array==null: " + (array==null));
        if(array!=null){
            for(int i=0; i<array.length; i++){
                trace.add(new Waypoint(new GeoCoordinates(array[i], array[++i])));
            }
            RoutingEngine routingEngine = null;
            try {
                routingEngine = new RoutingEngine();
            } catch (InstantiationErrorException e) {
                throw new RuntimeException(e);
            }
            routingEngine.calculateRoute(
                    trace,
                    new BicycleOptions(),
                    new CalculateRouteCallback() {
                        @Override
                        public void onRouteCalculated(@Nullable RoutingError routingError, @Nullable List<Route> routes) {
                            Route route;
                            if (routingError == null) {
                                route = routes.get(0);
                                GeoPolyline routeGeoPolyline = route.getGeometry();

                                float widthInPixels = 10;
                                MapPolyline routeMapPolyline = new MapPolyline(routeGeoPolyline,
                                        widthInPixels,
                                        Color.valueOf(0,1,0)); // RGBA

                                mapView.getMapScene().addMapPolyline(routeMapPolyline);
                                lines.add(routeMapPolyline);
                                loadMapScene();
                            }
                        }
                    });
            MapImage mapImage = MapImageFactory.fromResource(getResources(), R.drawable.point);

            Anchor2D anchor2D = new Anchor2D(0.5F, 1);
            MapMarker mapMarkerStart = new MapMarker(trace.get(0).coordinates, mapImage, anchor2D);
            MapMarker mapMarkerEnd = new MapMarker(trace.get(trace.size()-1).coordinates, mapImage, anchor2D);
            mapView.getMapScene().addMapMarker(mapMarkerEnd);
            mapView.getMapScene().addMapMarker(mapMarkerStart);
            this.markers.add(mapMarkerEnd);
            this.markers.add(mapMarkerStart);
        }else{
            loadMapScene();
        }
    }

    private void loadMapScene() {
        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, mapError -> {
            if (mapError == null) {
                double latitude, longitude;
                if(this.trace.size()>0){
                    latitude = (trace.get(0).coordinates.latitude + trace.get(trace.size()-1).coordinates.latitude) / 2;
                    longitude = (trace.get(0).coordinates.longitude + trace.get(trace.size()-1).coordinates.longitude) / 2;
                    mapView.getCamera().lookAt(
                            new GeoCoordinates(latitude, longitude),
                            new MapMeasure(MapMeasure.Kind.DISTANCE, 2*trace.get(0).coordinates.distanceTo(
                                    trace.get(trace.size()-1).coordinates))); //start
                }
                Log.d("loadMapScene()", "I am here now ");
            } else {
                Log.d("loadMapScene()", "Loading map failed: mapError: " + mapError.name());
            }
        });
    }
}