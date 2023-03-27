package com.example.here;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class CupFragment extends Fragment {

    public CupFragment(){
        // require a empty public constructor
    }

    private Button myRacesButton, joinRaceButton, searchRaceButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_cup, container, false);

        myRacesButton = view.findViewById(R.id.my_races_button);
        joinRaceButton = view.findViewById(R.id.join_race_button);
        searchRaceButton = view.findViewById(R.id.search_race_button);

        myRacesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Narazie po kliknięciu przechodzi do widoku ustawiania trasy, zmienić to potem!!!!
                Intent intent = new Intent(getActivity(), SetRouteActivity.class);
                startActivity(intent);
            }
        });

        joinRaceButton.setOnClickListener(new View.OnClickListener() {  // po kliknięciu pojawia się popup do wprowadzenia kodu do zapisów
            @Override
            public void onClick(View view) {
                showJoinRaceDialog();
            }
        });

        searchRaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Wyszukaj wyścig jakoś
            }
        });

        return view;
    }

    private void showJoinRaceDialog() { // popup wprowadzania kodu do zapisu do wyścigu
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Podaj kod wyścigu:");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String raceCode = input.getText().toString();
                // Zrób coś z kodem do wyścigu
            }
        });
        builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
