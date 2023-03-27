package com.example.here;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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


public class PersonFragment extends Fragment {

    public PersonFragment(){
        // require a empty public constructor
    }

    private Button addButton;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_person, container, false);

        addButton = view.findViewById(R.id.add_friend_button);
        tabLayout = view.findViewById(R.id.friends_tabs);
        viewPager = view.findViewById(R.id.friends_viewpager);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obsługa kliknięcia przycisku Dodaj znajomego
                Toast.makeText(getActivity(), "Kliknięto przycisk Dodaj znajomego", Toast.LENGTH_SHORT).show();
            }
        });

        // Konfiguracja widoku ViewPager z zakładkami
        viewPager.setAdapter(new FriendsPagerAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

//        // Ustawienie ikonek użytkowników dla elementów listy
//        ArrayList<User> friends = new ArrayList<>();
//        friends.add(new User("Jan", "Kowalski", R.drawable.ic_round_person_24));
//        friends.add(new User("Anna", "Nowak", R.drawable.ic_round_person_24));
//        friends.add(new User("Piotr", "Wiśniewski", R.drawable.ic_round_person_24));
//        FriendsListAdapter friendsListAdapter = new FriendsListAdapter(getContext(), friends);
//        ListView friendsListView = (ListView) inflater.inflate(R.layout.friends_list, container, false);
//        friendsListView.setAdapter(friendsListAdapter);
//
//        ArrayList<User> invitations = new ArrayList<>();
//        invitations.add(new User("Tomasz", "Jankowski", R.drawable.ic_round_person_24));
//        invitations.add(new User("Magdalena", "Kaczmarek", R.drawable.ic_round_person_24));
//        invitations.add(new User("Krzysztof", "Lewandowski", R.drawable.ic_round_person_24));
//        FriendsListAdapter invitationsListAdapter = new FriendsListAdapter(getContext(), invitations);
//        ListView invitationsListView = (ListView) inflater.inflate(R.layout.invitations_list, container, false);
//        invitationsListView.setAdapter(invitationsListAdapter);

        return view;
    }

    // Adapter dla ViewPager z zakładkami
    private class FriendsPagerAdapter extends FragmentPagerAdapter {

        public FriendsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FriendsListFragment();
                case 1:
                    return new InvitationsListFragment();
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
                    return "Lista znajomych";
                case 1:
                    return "Lista zaproszeń";
                default:
                    return null;
            }
        }
    }

    // Adapter dla ListView z listą znajomych i listą zaproszeń
    private static class FriendsListAdapter extends ArrayAdapter<User> {

        public FriendsListAdapter(Context context, ArrayList<User> users) {
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
            User user = getItem(position);
            if (user != null) {
                userIconImageView.setImageResource(user.getIconResource());
                userNameTextView.setText(user.getName());
                userSurnameTextView.setText(user.getSurname());
            }
            return convertView;
        }
    }

    // Fragment dla zakładki Lista znajomych
    public static class FriendsListFragment extends Fragment {

        public FriendsListFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_friends_list, container, false);
            // Dodanie listy znajomych
            ArrayList<User> friends = new ArrayList<>();
            friends.add(new User("Jan", "Kowalski", R.drawable.ic_round_person_24));
            friends.add(new User("Anna", "Nowak", R.drawable.ic_round_person_24));
            friends.add(new User("Piotr", "Wiśniewski", R.drawable.ic_round_person_24));
            FriendsListAdapter friendsListAdapter = new FriendsListAdapter(getContext(), friends);
            ListView friendsListView = (ListView) view.findViewById(R.id.friends_list);
            friendsListView.setAdapter(friendsListAdapter);
            return view;
        }
    }

    // Fragment dla zakładki Lista zaproszeń
    public static class InvitationsListFragment extends Fragment {

        public InvitationsListFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_invitations_list, container, false);
            // Dodanie listy zaproszeń
            ArrayList<User> invitations = new ArrayList<>();
            invitations.add(new User("Tomasz", "Jankowski", R.drawable.ic_round_person_24));
            invitations.add(new User("Magdalena", "Kaczmarek", R.drawable.ic_round_person_24));
            invitations.add(new User("Krzysztof", "Lewandowski", R.drawable.ic_round_person_24));
            FriendsListAdapter invitationsListAdapter = new FriendsListAdapter(getContext(), invitations);
            ListView invitationsListView = (ListView) view.findViewById(R.id.invitations_list);
            invitationsListView.setAdapter(invitationsListAdapter);
            return view;
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
