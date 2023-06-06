package com.example.here;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.here.restapi.ApiInterface;
import com.example.here.restapi.RetrofitClient;
import com.example.here.restapi.StatisticsByPeriod;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovingFragment extends Fragment {
    private View view;
    String[] items = {"Ostatnia aktywność","Tydzień","Miesiąc","Rok","Cały okres"};
    AutoCompleteTextView autoCompleteTxt;
    ArrayAdapter<String> adapterItems;

    TextView distance;
    TextView speed;
    TextView duration;
    TextView count;

    ProgressBar progressBar;

    GridLayout gridLayout;

    public MovingFragment(){
        // require a empty public constructor
    }

    // period:
    // 0 - last one
    // 1 - week
    // 2 - month
    // 3 - year
    // 4 - all
    public void loadStats(int period) {

        gridLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        ApiInterface apiInterface = RetrofitClient.getInstance().create(ApiInterface.class);
        SharedPreferences sp = getActivity().getSharedPreferences("msb",MODE_PRIVATE);
        String token = sp.getString("token", "");

        Call<StatisticsByPeriod> call = apiInterface.statisticsByPeriod("Token " + token, period);

        call.enqueue(new Callback<StatisticsByPeriod>() {
            @Override
            public void onResponse(Call<StatisticsByPeriod> call, Response<StatisticsByPeriod> response) {
                StatisticsByPeriod statistics = response.body();
                if(statistics!=null){
                    duration.setText(getString(R.string.duration_period, statistics.getDuration()));
                    distance.setText(getString(R.string.kilometers, statistics.getDistance()));
                    speed.setText(getString(R.string.kmh, statistics.getSpeed()*3600));
                    count.setText(String.valueOf(statistics.getCount()));
                }

                gridLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<StatisticsByPeriod> call, Throwable t) {

            }
        });

    }

    public void chooseActivePeriod(){
        autoCompleteTxt = view.findViewById(R.id.auto_complete_txt);
        adapterItems = new ArrayAdapter<String>(getActivity(),R.layout.list_item,items);
        autoCompleteTxt.setText(items[0]);
        autoCompleteTxt.setAdapter(adapterItems);
        loadStats(0);
        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                loadStats(position);
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_moving, container, false);

        this.progressBar = view.findViewById(R.id.progressBar);
        this.gridLayout = view.findViewById(R.id.grid_layout);

        this.distance = view.findViewById(R.id.distance_text);
        this.duration = view.findViewById(R.id.duration_text);
        this.speed = view.findViewById(R.id.speed_text);
        this.count = view.findViewById(R.id.stats_count);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        chooseActivePeriod();
        return view;
    }
}
