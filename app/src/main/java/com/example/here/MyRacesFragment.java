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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MyRacesFragment extends Fragment {

    public MyRacesFragment() {
        // require a empty public constructor
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
                    // Tutaj do spinnera trzeba dodać funkcjonalność nie tylko wyświetlania odpowiednich wyścigów ale również żeby np. w nadchodzących po naciśnięciu pokazywało odpowiedni widok (patrz skrypt),
                    // widok nadchodzącego i zakończonego wyścigu już są podpięte, brakuje wyścigu trwającego!!
                    case 0: // Aktualne wyścigi
                        // Kod do wyświetlenia aktywnych wyścigów

                        break;
                    case 1: // Nadchodzące wyścigi
                        // Kod do wyświetlenia nadchodzących wyścigów

                        break;
                    case 2: // Zakończone wyścigi
                        // Kod do wyświetlenia zakończonych wyścigów

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
                Fragment newFragment = new CreateRaceFragment(); //utworzenie nowej instancji klasy MyRacesFragment
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
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

    private class MyRacesAdapter extends FragmentPagerAdapter {

        public MyRacesAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new CreatedRacesListFragment(spinner);
                case 1:
                    return new ParticipatedRacesListFragment(spinner);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Utworzone";
                case 1:
                    return "Uczestniczę";
                default:
                    return null;
            }
        }
    }

    private static class RacesListAdapter extends ArrayAdapter<MyRacesFragment.Race> {

        public RacesListAdapter(Context context, ArrayList<MyRacesFragment.Race> races) {
            super(context, 0, races);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_race, parent, false);
            }
            TextView raceNameTextView = convertView.findViewById(R.id.race_name);
            MyRacesFragment.Race race = getItem(position);
            if (race != null) {
                raceNameTextView.setText(race.getName());
            }
            return convertView;
        }
    }

    public static class CreatedRacesListFragment extends Fragment {

        public CreatedRacesListFragment() {
        }

        public CreatedRacesListFragment(Spinner spinner) {  // aby wyłapać wybraną opcję spinnera
            this.spinner = spinner;
        }

        private Spinner spinner;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_friends_list, container, false);
            ArrayList<MyRacesFragment.Race> races = new ArrayList<MyRacesFragment.Race>();
            races.add(new MyRacesFragment.Race("Wyścig 1"));
            races.add(new MyRacesFragment.Race("Wyścig 2"));
            races.add(new MyRacesFragment.Race("Wyścig 3"));
            MyRacesFragment.RacesListAdapter racesListAdapter = new MyRacesFragment.RacesListAdapter(getContext(), races);
            ListView racesListView = (ListView) view.findViewById(R.id.friends_list);
            racesListView.setAdapter(racesListAdapter);

            racesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {    // wyświetl odpowiedni widok na podstawie spinnera
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String selectedSpinnerOption = spinner.getSelectedItem().toString();

                    if (selectedSpinnerOption.equals("Aktywne wyścigi")) {  // tutaj dodać widok aktywnego wyścigu gdy będzie już zrobiony!!


                    } if (selectedSpinnerOption.equals("Nadchodzące wyścigi")) {
                        Fragment newFragment = new UpcomingRaceFragment();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, newFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    } if (selectedSpinnerOption.equals("Zakończone wyścigi")) {
                        Fragment newFragment = new FinishedRaceFragment();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, newFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }
            });

            return view;
        }
    }

    public static class ParticipatedRacesListFragment extends Fragment {

        public ParticipatedRacesListFragment() {
        }

        public ParticipatedRacesListFragment(Spinner spinner) {  // aby wyłapać wybraną opcję spinnera
            this.spinner = spinner;
        }

        private Spinner spinner;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_invitations_list, container, false);
            ArrayList<MyRacesFragment.Race> races = new ArrayList<MyRacesFragment.Race>();
            races.add(new MyRacesFragment.Race("Wyścig 4"));
            races.add(new MyRacesFragment.Race("Wyścig 5"));
            races.add(new MyRacesFragment.Race("Wyścig 6"));
            MyRacesFragment.RacesListAdapter racesListAdapter = new MyRacesFragment.RacesListAdapter(getContext(), races);
            ListView racesListView = (ListView) view.findViewById(R.id.invitations_list);
            racesListView.setAdapter(racesListAdapter);

            racesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {    // wyświetl odpowiedni widok na podstawie spinnera
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String selectedSpinnerOption = spinner.getSelectedItem().toString();

                    if (selectedSpinnerOption.equals("Aktywne wyścigi")) {  // tutaj dodać widok aktywnego wyścigu gdy będzie już zrobiony!!


                    } if (selectedSpinnerOption.equals("Nadchodzące wyścigi")) {
                        Fragment newFragment = new UpcomingRaceFragment();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, newFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    } if (selectedSpinnerOption.equals("Zakończone wyścigi")) {
                        Fragment newFragment = new FinishedRaceFragment();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, newFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }
            });

            return view;
        }
    }

    // Klasa reprezentująca testowy wyścig
    private static class Race {
        private String name;

        public Race(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

    }


