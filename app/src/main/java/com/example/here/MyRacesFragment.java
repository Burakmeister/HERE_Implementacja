package com.example.here;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.here.restapi.ApiInterface;
import com.example.here.restapi.Races;
import com.example.here.restapi.RetrofitClient;
import com.google.android.material.tabs.TabLayout;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRacesFragment extends Fragment {

    public MyRacesFragment() {
        // wymagany pusty konstruktor publiczny
    }

    private Button createButton;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Spinner spinner;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myraces, container, false);

        createButton = view.findViewById(R.id.create_race_button);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.races_viewpager);

        spinner = view.findViewById(R.id.my_races_spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // Aktualne wyścigi
                        viewPager.setCurrentItem(0);
                        break;
                    case 1: // Nadchodzące wyścigi
                        viewPager.setCurrentItem(1);
                        break;
                    case 2: // Zakończone wyścigi
                        viewPager.setCurrentItem(2);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nic nie rób
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obsługa kliknięcia przycisku Utwórz wyścig
                Fragment newFragment = new CreateRaceFragment();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        // Konfiguracja widoku ViewPager z zakładkami
        viewPager.setAdapter(new MyRacesAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    private static class MyRacesAdapter extends FragmentPagerAdapter {

        private static final int NUM_PAGES = 3;

        public MyRacesAdapter(@NonNull FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ActiveRacesFragment();
                case 1:
                    return new UpcomingRacesFragment();
                case 2:
                    return new FinishedRacesFragment();
                default:
                    throw new IllegalArgumentException("Invalid fragment position: " + position);
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Aktywne";
                case 1:
                    return "Nadchodzące";
                case 2:
                    return "Zakończone wyścigi";
                default:
                    return null;
            }
        }
    }

    public static class ActiveRacesFragment extends Fragment {

        private List<Race> activeRaces;
        private ApiInterface apiInterface;
        private static final String TAG = "FindRaces";
        private List<Races.races> allRaces;

        public ActiveRacesFragment() {
            activeRaces = new ArrayList<>();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_friends_list, container, false);

            apiInterface = RetrofitClient.getInstance().create(ApiInterface.class);
            getRaceInfo();

            ListView activeRacesListView = view.findViewById(R.id.friends_list);
            RaceListAdapter raceListAdapter = new RaceListAdapter(getContext(), activeRaces);
            activeRacesListView.setAdapter(raceListAdapter);

            return view;
        }

        private void getRaceInfo() {
            Call<Races> raceCall = apiInterface.getRaces("Token 7657d91254e0beff089cb04adedae33c7278d885");
            raceCall.enqueue(new Callback<Races>() {
                @Override
                public void onResponse(Call<Races> call, Response<Races> response) {
                    Log.d(TAG, "onResponse: code: " + response.code());
                    if (response.isSuccessful()) {
                        Races races = response.body();
                        if (races != null) {
                            ArrayList<Races.races> raceList = races.getRacess();
                            if (raceList != null) {
                                activeRaces.clear();
                                allRaces = raceList;
                                addRacesToActiveRacesList();
                            }
                        }
                    } else {
                        Log.d(TAG, "onResponse: Request failed with code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Races> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });
        }

        private void addRacesToActiveRacesList() {
            LocalDateTime currentDateTime = LocalDateTime.now();
            LocalDateTime oneHourAgo = currentDateTime.minusHours(1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            for (Races.races race : allRaces) {
                String name = race.getName();
                String dateStr = race.getDate_time(); // Przykładowa data w formacie "2023-05-31T20:07:38Z"
                LocalDateTime raceDateTime = LocalDateTime.parse(dateStr, formatter);
                if (raceDateTime.isAfter(oneHourAgo) && raceDateTime.isBefore(currentDateTime)) {
                    activeRaces.add(new Race(name));
                }
            }

            // Aktualizacja adaptera po dodaniu wyścigów
            if (getView() != null) {
                ListView activeRacesListView = getView().findViewById(R.id.friends_list);
                RaceListAdapter raceListAdapter = new RaceListAdapter(getContext(), activeRaces);
                activeRacesListView.setAdapter(raceListAdapter);
            }
        }
    }




    public static class UpcomingRacesFragment extends Fragment {

        private List<Race> upcomingRaces;
        private ApiInterface apiInterface;
        private static final String TAG = "FindRaces";
        private List<Races.races> allRaces;

        public UpcomingRacesFragment() {
            upcomingRaces = new ArrayList<>();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_friends_list, container, false);

            apiInterface = RetrofitClient.getInstance().create(ApiInterface.class);
            getRaceInfo();

            ListView upcomingRacesListView = view.findViewById(R.id.friends_list);
            RaceListAdapter raceListAdapter = new RaceListAdapter(getContext(), upcomingRaces);
            upcomingRacesListView.setAdapter(raceListAdapter);

            return view;
        }

        private void getRaceInfo() {
            Call<Races> raceCall = apiInterface.getRaces("Token 7657d91254e0beff089cb04adedae33c7278d885");
            raceCall.enqueue(new Callback<Races>() {
                @Override
                public void onResponse(Call<Races> call, Response<Races> response) {
                    Log.d(TAG, "onResponse: code: " + response.code());
                    if (response.isSuccessful()) {
                        Races races = response.body();
                        if (races != null) {
                            ArrayList<Races.races> raceList = races.getRacess();
                            if (raceList != null) {
                                upcomingRaces.clear();
                                allRaces = raceList;
                                addRacesToUpcomingRacesList();
                            }
                        }
                    } else {
                        Log.d(TAG, "onResponse: Request failed with code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Races> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });
        }

        private void addRacesToUpcomingRacesList() {
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            for (Races.races race : allRaces) {
                String name = race.getName();
                String dateStr = race.getDate_time(); // Przykładowa data w formacie "2023-05-31T20:07:38Z"
                LocalDateTime raceDateTime = LocalDateTime.parse(dateStr, formatter);
                if (raceDateTime.isAfter(currentDateTime)) {
                    upcomingRaces.add(new Race(name));
                }
            }

            // Aktualizacja adaptera po dodaniu wyścigów
            if (getView() != null) {
                ListView upcomingRacesListView = getView().findViewById(R.id.friends_list);
                RaceListAdapter raceListAdapter = new RaceListAdapter(getContext(), upcomingRaces);
                upcomingRacesListView.setAdapter(raceListAdapter);
            }
        }
    }


    public static class FinishedRacesFragment extends Fragment {

        private List<Race> finishedRaces;
        private ApiInterface apiInterface;
        private static final String TAG = "FindRaces";
        private List<Races.races> allRaces;

        public FinishedRacesFragment() {
            finishedRaces = new ArrayList<>();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_friends_list, container, false);

            apiInterface = RetrofitClient.getInstance().create(ApiInterface.class);
            getRaceInfo();

            ListView finishedRacesListView = view.findViewById(R.id.friends_list);
            RaceListAdapter raceListAdapter = new RaceListAdapter(getContext(), finishedRaces);
            finishedRacesListView.setAdapter(raceListAdapter);

            return view;
        }

        private void getRaceInfo() {
            Call<Races> raceCall = apiInterface.getRaces("Token 7657d91254e0beff089cb04adedae33c7278d885");
            raceCall.enqueue(new Callback<Races>() {
                @Override
                public void onResponse(Call<Races> call, Response<Races> response) {
                    Log.d(TAG, "onResponse: code: " + response.code());
                    if (response.isSuccessful()) {
                        Races races = response.body();
                        if (races != null) {
                            ArrayList<Races.races> raceList = races.getRacess();
                            if (raceList != null) {
                                finishedRaces.clear();
                                allRaces = raceList;
                                addRacesToFinishedRacesList();
                            }
                        }
                    } else {
                        Log.d(TAG, "onResponse: Request failed with code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Races> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });
        }

        private void addRacesToFinishedRacesList() {
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            for (Races.races race : allRaces) {
                String name = race.getName();
                String dateStr = race.getDate_time(); // Przykładowa data w formacie "2023-05-31T20:07:38Z"
                LocalDateTime raceDateTime = LocalDateTime.parse(dateStr, formatter);
                if (raceDateTime.isBefore(currentDateTime)) {
                    finishedRaces.add(new Race(name));
                }
            }

            // Aktualizacja adaptera po dodaniu wyścigów
            if (getView() != null) {
                ListView finishedRacesListView = getView().findViewById(R.id.friends_list);
                RaceListAdapter raceListAdapter = new RaceListAdapter(getContext(), finishedRaces);
                finishedRacesListView.setAdapter(raceListAdapter);
            }
        }
    }





    private static class Race {
        private String name;

        public Race(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private static class RaceListAdapter extends ArrayAdapter<Race> {

        public RaceListAdapter(Context context, List<Race> races) {
            super(context, 0, races);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_race, parent, false);
            }

            TextView raceNameTextView = convertView.findViewById(R.id.race_name);
            Race race = getItem(position);
            if (race != null) {
                raceNameTextView.setText(race.getName());
            }

            return convertView;
        }
    }
}
