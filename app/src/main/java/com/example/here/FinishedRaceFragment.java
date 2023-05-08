package com.example.here;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.here.sdk.mapview.MapView;

import java.util.ArrayList;

public class FinishedRaceFragment extends Fragment {

    public FinishedRaceFragment() {
        // Required empty public constructor
    }

    private TextView raceTime;
    private MapView mapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_finished_race, container, false);

        raceTime = view.findViewById(R.id.race_time);
        mapView = view.findViewById(R.id.map_view);

        this.mapView.onCreate(savedInstanceState);
        // Tutaj zimplementowac aby pokazywało w podglądzie trasę wyścigu


        // Ustawienie adaptera na przykładowe dane
        ArrayList<FinishedRaceFragment.User> users = new ArrayList<FinishedRaceFragment.User>();
        users.add(new FinishedRaceFragment.User("Jan", "Kowalski", R.drawable.ic_round_person_24));
        users.add(new FinishedRaceFragment.User("Jan", "Kowalski", R.drawable.ic_round_person_24));
        users.add(new FinishedRaceFragment.User("Jan", "Kowalski", R.drawable.ic_round_person_24));
        users.add(new FinishedRaceFragment.User("Jan", "Kowalski", R.drawable.ic_round_person_24));
        users.add(new FinishedRaceFragment.User("Jan", "Kowalski", R.drawable.ic_round_person_24));
        users.add(new FinishedRaceFragment.User("Jan", "Kowalski", R.drawable.ic_round_person_24));
        users.add(new FinishedRaceFragment.User("Jan", "Kowalski", R.drawable.ic_round_person_24));
        users.add(new FinishedRaceFragment.User("Jan", "Kowalski", R.drawable.ic_round_person_24));
        users.add(new FinishedRaceFragment.User("Jan", "Kowalski", R.drawable.ic_round_person_24));
        users.add(new FinishedRaceFragment.User("Jan", "Kowalski", R.drawable.ic_round_person_24));
        FinishedRaceFragment.RankingListAdapter racesListAdapter = new FinishedRaceFragment.RankingListAdapter(getContext(), users);
        ListView participantListView = view.findViewById(R.id.ranking_list);
        participantListView.setAdapter(racesListAdapter);

        return view;
    }

    private static class RankingListAdapter extends ArrayAdapter<FinishedRaceFragment.User> {   // adapter do uzupełnienia listy

        public RankingListAdapter(Context context, ArrayList<FinishedRaceFragment.User> users) {
            super(context, 0, users);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_user, parent, false);
            }
            ImageView userIconImageView = convertView.findViewById(R.id.user_image);
            TextView userNameTextView = convertView.findViewById(R.id.user_name);
            TextView userSurnameTextView = convertView.findViewById(R.id.user_surname);
            FinishedRaceFragment.User user = getItem(position);
            if (user != null) {
                //userIconImageView.setImageResource(user.getAvatar());
                userIconImageView.setImageResource(R.drawable.ic_round_person_24);
                userNameTextView.setText(user.getName());
                userSurnameTextView.setText(user.getSurname());
            }
            return convertView;
        }
    }

    // Klasa reprezentująca testowego użytkownika
    private static class User {
        private String name;
        private String surname;
        private int iconResource;

        public User(String name, String surname, int iconResource) {
            this.name = name;
            this.surname = surname;
            this.iconResource = iconResource;
        }

        public String getName() {
            return name;
        }

        public String getSurname() {
            return surname;
        }

        public int getIconResource() {
            return iconResource;
        }
    }

}