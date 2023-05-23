package com.example.here;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.here.restapi.ApiInterface;
import com.example.here.restapi.RaceInfo;
import com.example.here.restapi.Races;
import com.example.here.restapi.RetrofitClient;
import com.example.here.restapi.TrainingStats;
import com.example.here.restapi.Username;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class RaceAdapter extends RecyclerView.Adapter<RaceAdapter.RaceViewHolder> {
    private List<Races.races> raceList;

    public RaceAdapter(List<Races.races> raceList) {
        this.raceList = raceList;
    }

    @NonNull
    @Override
    public RaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.finding_races_recycler_row, parent, false);
        return new RaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RaceViewHolder holder, int position) {
        Races.races race = raceList.get(position);
        holder.bind(race);
    }

    @Override
    public int getItemCount() {
        return raceList.size();
    }

    public static class RaceViewHolder extends RecyclerView.ViewHolder {
        private TextView raceIdText;
        private TextView raceTitleText;
        private TextView raceStartTime;
        private TextView raceOrganizer;

        public RaceViewHolder(@NonNull View itemView) {
            super(itemView);
            raceIdText = itemView.findViewById(R.id.race_id_text);
            raceTitleText = itemView.findViewById(R.id.race_title_text);
            raceStartTime = itemView.findViewById(R.id.race_start_time);
            raceOrganizer = itemView.findViewById(R.id.race_organizer);
        }

        public void bind(Races.races race) {
            raceIdText.setText(String.valueOf(race.getRace_id()));
            raceTitleText.setText(race.getName());
            raceStartTime.setText(String.valueOf(race.getDate_time()));
            raceOrganizer.setText(String.valueOf(race.getOrganizer_id()));
        }
    }
}



public class FindRaces extends AppCompatActivity implements SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener {
    private static final String TAG = "FindRaces";
    private ApiInterface apiInterface;
    private SharedPreferences sp;
    private TextView welcomeTextView;
    private RecyclerView recyclerView;
    private RaceAdapter raceAdapter;
    private List<Races.races> allRaces;
    private List<Races.races> filteredRaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_races);
        
        sp = getSharedPreferences("msb", MODE_PRIVATE);
        recyclerView = findViewById(R.id.kuba);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        androidx.appcompat.widget.SearchView searchView = findViewById(R.id.searchView_find_races);
        searchView.setOnQueryTextListener(this);

        apiInterface = RetrofitClient.getInstance().create(ApiInterface.class);
        getRaceInfo();
    }

    private void updateRecyclerView(List<Races.races> races) {
        raceAdapter = new RaceAdapter(races);
        recyclerView.setAdapter(raceAdapter);
    }

    private void filterRaces(String query) {
        filteredRaces = new ArrayList<>();

        if (TextUtils.isEmpty(query)) {
            filteredRaces.addAll(allRaces);
        } else {
            for (Races.races race : allRaces) {
                if (race.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredRaces.add(race);
                }
            }
        }

        updateRecyclerView(filteredRaces);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        filterRaces(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        filterRaces(newText);
        return true;
    }

    private void getRaceInfo() {
        Call<Races> raceCall = apiInterface.getRaces("Token " + sp.getString("token", ""));
        raceCall.enqueue(new Callback<Races>() {
            @Override
            public void onResponse(Call<Races> call, Response<Races> response) {
                Log.d(TAG, "onResponse: code :" + response.code());
                ArrayList<Races.races> races = response.body().getRacess();
                allRaces = races;
                filteredRaces = races;
                updateRecyclerView(races);
            }

            @Override
            public void onFailure(Call<Races> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private static class RaceAdapter extends RecyclerView.Adapter<RaceAdapter.RaceViewHolder> {
        private List<Races.races> raceList;

        public RaceAdapter(List<Races.races> raceList) {
            this.raceList = raceList;
        }

        @NonNull
        @Override
        public RaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.finding_races_recycler_row, parent, false);
            return new RaceViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RaceViewHolder holder, int position) {
            Races.races race = raceList.get(position);
            holder.bind(race);
        }

        @Override
        public int getItemCount() {
            return raceList.size();
        }

        public static class RaceViewHolder extends RecyclerView.ViewHolder {
            private TextView raceIdText;
            private TextView raceTitleText;
            private TextView raceStartTime;
            private TextView raceOrganizer;

            public RaceViewHolder(@NonNull View itemView) {
                super(itemView);
                raceIdText = itemView.findViewById(R.id.race_id_text);
                raceTitleText = itemView.findViewById(R.id.race_title_text);
                raceStartTime = itemView.findViewById(R.id.race_start_time);
                raceOrganizer = itemView.findViewById(R.id.race_organizer);
            }

            public void bind(Races.races race) {
                raceIdText.setText(String.valueOf(race.getRace_id()));
                raceTitleText.setText(race.getName());
                // Formatowanie daty
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

                try {
                    // Parsowanie i formatowanie daty
                    String raceDateTime = outputFormat.format(inputFormat.parse(race.getDate_time()));
                    raceStartTime.setText(raceDateTime);
                } catch (Exception e) {
                    e.printStackTrace();
                    raceStartTime.setText("");
                }
                raceOrganizer.setText(race.getOrganizer_id());
            }
        }
    }
}




