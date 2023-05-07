package com.example.here;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
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

import com.here.sdk.mapview.MapView;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_race, container, false);

        nameEditText = view.findViewById(R.id.name_edit_text);
        numParticipantsEditText = view.findViewById(R.id.num_participants_edit_text);
        cityEditText = view.findViewById(R.id.city_edit_text);
        mapView = view.findViewById(R.id.map_view);

        savedRoutesButton = view.findViewById(R.id.saved_routes_button);
        createRouteButton = view.findViewById(R.id.create_route_button);
        selectDateTimeButton = view.findViewById(R.id.select_date_time_button);
        createRaceButton = view.findViewById(R.id.create_race_button);

        this.mapView.onCreate(savedInstanceState);
        // Tutaj zimplementowac aby pokazywało w podglądzie wybraną trasę


        savedRoutesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Otwórz popup z utworzonymi trasami (Działa, ale się źle wyświetla)

                PopupMenu popup = new PopupMenu(getActivity(), view);
                popup.getMenuInflater().inflate(R.menu.saved_route_popup_menu, popup.getMenu());
                RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(getActivity())
                        .inflate(R.layout.popup_list, null);

                // Znajdź ListView w RelativeLayout
                ListView listView = relativeLayout.findViewById(R.id.list_view);

                // Stwórz adapter dla listy
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1, android.R.id.text1, getTrackTitles(popup));
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        // Handle item click event here
                    }
                });
                popup.show();

            }
        });

        createRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Przejdź do widoku ustawiania trasy (trzeba zrobić SetRaceRouteFragment na podstawie SetRouteActivity)
                Fragment newFragment = new SetRaceRouteFragment(); //utworzenie nowej instancji klasy MyRacesFragment
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
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


}