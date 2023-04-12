package com.example.here;

import android.content.Context;
import android.os.Bundle;
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
                    case 0: // Ostatni trening
                        // Kod do wyświetlenia aktywnych wyścigów

                        break;
                    case 1: // Ostatnie 5 treningów
                        // Kod do wyświetlenia nadchodzących wyścigów

                        break;
                    case 2: // Ostatnie 10 treningów
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
                    return new CreatedRacesListFragment();
                case 1:
                    return new ParticipatedRacesListFragment();
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

        public RacesListAdapter(Context context, ArrayList<MyRacesFragment.Race> users) {
            super(context, 0, users);
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
            return view;
        }
    }

    public static class ParticipatedRacesListFragment extends Fragment {

        public ParticipatedRacesListFragment() {
        }

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

//        // Dodaj fragmenty do ViewPager
//        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
//        adapter.addFragment(new MyRacesCreatedFragment(), "Utworzone");
//        adapter.addFragment(new MyRacesParticipateFragment(), "Uczestniczę");
//        viewPager.setAdapter(adapter);
//
//        // Ustaw ViewPager jako źródło zakładek w TabLayout
//        tabLayout.setupWithViewPager(viewPager);
//
//        // Wyświetl odpowiedni fragment w zależności od wybranej zakładki
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                int position = tab.getPosition();
//                if (position == 0) {
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.fragment_container, new MyRacesCreatedFragment())
//                            .commit();
//                } else if (position == 1) {
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.fragment_container, new MyRacesParticipateFragment())
//                            .commit();
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//            }
//
//        return view;
//    }
//
//    @Override
//    protected void onCreateView(@Nullable Bundle savedInstanceState) {
//        setTheme(R.style.AppTheme);
////        getSupportActionBar().hide();
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_myraces);
//
//        tabLayout = findViewById(R.id.tab_layout);
//        viewPager = findViewById(R.id.races_viewpager);
//
//        // Dodaj fragmenty do ViewPager
//        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
//        adapter.addFragment(new MyRacesCreatedFragment(), "Utworzone");
//        adapter.addFragment(new MyRacesParticipateFragment(), "Uczestniczę");
//        viewPager.setAdapter(adapter);
//
//        // Ustaw ViewPager jako źródło zakładek w TabLayout
//        tabLayout.setupWithViewPager(viewPager);
//
//        // Wyświetl odpowiedni fragment w zależności od wybranej zakładki
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                int position = tab.getPosition();
//                if (position == 0) {
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.fragment_container, new MyRacesCreatedFragment())
//                            .commit();
//                } else if (position == 1) {
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.fragment_container, new MyRacesParticipateFragment())
//                            .commit();
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//            }
//        });

    }


