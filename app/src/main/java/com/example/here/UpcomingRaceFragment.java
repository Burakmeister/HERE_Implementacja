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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class UpcomingRaceFragment extends Fragment {

    public UpcomingRaceFragment() {
        // Required empty public constructor
    }

    private TextView raceName;  // trzeba będzie zimplementować pobieranie z bazy nazwy wyścigu!!
    private TextView raceCode;
    private Button inviteFriends;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upcoming_race, container, false);

        this.raceCode = view.findViewById(R.id.generated_race_code);
        this.inviteFriends = view.findViewById(R.id.invite_friends_button);

        inviteFriends.setOnClickListener(new View.OnClickListener() {   // przełączenie na widok zapraszania znajomych, widok do zrobienia!!
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Kliknięto przycisk Zaproś znajomych", Toast.LENGTH_SHORT).show();
            }
        });

        // Tutaj trzeba wymyśleć system unikalnych kodów zaproszeń do wyścigów i zapisywania ich w bazie!!
        raceCode.setText("x4Efdfw");

        // Ustawienie adaptera na przykładowe dane
        ArrayList<UpcomingRaceFragment.User> users = new ArrayList<UpcomingRaceFragment.User>();
        users.add(new User("Jan", "Kowalski", R.drawable.ic_round_person_24));
        users.add(new User("Jan", "Kowalski", R.drawable.ic_round_person_24));
        users.add(new User("Jan", "Kowalski", R.drawable.ic_round_person_24));
        users.add(new User("Jan", "Kowalski", R.drawable.ic_round_person_24));
        users.add(new User("Jan", "Kowalski", R.drawable.ic_round_person_24));
        users.add(new User("Jan", "Kowalski", R.drawable.ic_round_person_24));
        users.add(new User("Jan", "Kowalski", R.drawable.ic_round_person_24));
        users.add(new User("Jan", "Kowalski", R.drawable.ic_round_person_24));
        users.add(new User("Jan", "Kowalski", R.drawable.ic_round_person_24));
        users.add(new User("Jan", "Kowalski", R.drawable.ic_round_person_24));
        ParticipantsListAdapter racesListAdapter = new ParticipantsListAdapter(getContext(), users);
        ListView participantListView = view.findViewById(R.id.participants_list);
        participantListView.setAdapter(racesListAdapter);

        return view;
    }

    private static class ParticipantsListAdapter extends ArrayAdapter<UpcomingRaceFragment.User> {   // adapter do uzupełnienia listy

        public ParticipantsListAdapter(Context context, ArrayList<UpcomingRaceFragment.User> users) {
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
            UpcomingRaceFragment.User user = getItem(position);
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