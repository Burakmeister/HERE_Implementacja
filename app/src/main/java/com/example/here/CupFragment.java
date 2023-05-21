package com.example.here;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.here.models.UserData;
import com.example.here.restapi.ApiInterface;
import com.example.here.restapi.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                Fragment newFragment = new MyRacesFragment(); //utworzenie nowej instancji klasy MyRacesFragment
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
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
                // Tutaj zimplementować wyszukiwanie wyścigu!!
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
                raceCode = raceCode.replaceAll("[^1-9]", "");   // usuń dopisane zera z przodu kodu, by pozostało id z bazy

                // Sprawdź, czy istnieje wyścig o podanym kodzie
                ApiInterface apiInterface = RetrofitClient.getInstance().create(ApiInterface.class);
                SharedPreferences sp = getActivity().getSharedPreferences("msb", Context.MODE_PRIVATE);
                Call<List<Integer>> call = apiInterface.getRacesId("Token " + sp.getString("token", ""));
                String finalRaceCode = raceCode;    // coś krzyczało dlatego tak
                call.enqueue(new Callback<List<Integer>>() {
                    @Override
                    public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                        Log.d("retro", "onresponse");
                        if (response.isSuccessful()) {
                            Log.d("retro", "success");
                            List<Integer> races_id_list = (ArrayList<Integer>) response.body();
                            for(Integer id : races_id_list) {
                                if(id.toString().equals(finalRaceCode)) {
                                    // Przypisz użytkownika jako uczestnika do wyścigu, jeśli wciąż są miejsca (pobierz z danych wyścigu participants limit, zapisz jako participant do wyścigu)
                                    // Zimplementować get_race aby osobno nie pobierać już limit i zapisywać
                                    

                                    Toast.makeText(getActivity(), "Pomyślnie dołączono do wyścigu!", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                                if(id == races_id_list.size()) {
                                    Toast.makeText(getActivity(), "Nie znaleziono wyścigu, błędny kod!", Toast.LENGTH_SHORT).show();
                                }
                            }

                        } else {
//                    unsuccessful
                            Toast.makeText(getActivity(), "Błąd połączenia z serwerem!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Integer>> call, Throwable t) {
                        //handle network problems
                        Toast.makeText(getActivity(), "Błąd połączenia z serwerem!", Toast.LENGTH_SHORT).show();
                    }
                });


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