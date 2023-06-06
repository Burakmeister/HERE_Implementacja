package com.example.here.home;

import static android.content.Context.MODE_PRIVATE;

//import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.here.PersonFragment;
import com.example.here.ProfileFragment;
import com.example.here.R;
import com.example.here.UserSettingsActivity;
import com.example.here.constants.ActivityType;
import com.example.here.restapi.ApiInterface;
import com.example.here.restapi.RetrofitClient;
import com.example.here.restapi.Username;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Widok strony głównej

public class HomeFragment extends Fragment {

    private FriendsStatusAdapter statusAdapter;
    private View view;
    private RecyclerView statusView;
    private SharedPreferences sp;
    private TextView welcomeTextView;
    private ProgressBar progressBar;
    private ScrollView scrollView;
    private Button settingsButton;
    private Button showProfileButton;

    String[] items = {"Marsz","Bieganie","Jazda na rowerze","Kajakarstwo"};
    AutoCompleteTextView autoCompleteTxt;
    ArrayAdapter<String> adapterItems;

    private double lastTourStartV1 = 53.41178404163292, lastTourStartV2 = 23.516119474276664,           // pobierane z bazy danych / z pamieci urzadzenia
            lastTourEndV1 = 53.1276662351446, lastTourEndV2 = 23.160716949523863;
    private Button friendsButton;
    private AtomicInteger countCalls;

    public HomeFragment() {
        // require a empty public constructor
    }

//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.fragment_home, container, false);;
////        this.mapView = view.findViewById(R.id.mapView);
////        this.mapView.onCreate(savedInstanceState);
//
////        setToLastRoute(new Waypoint(new GeoCoordinates(lastTourStartV1, lastTourStartV2)), new Waypoint(new GeoCoordinates(lastTourEndV1, lastTourEndV2)));     //rysowanie poprzedniej trasy po wspolrzednych*/
//
//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
//
//        chooseDiscipline();
//        return view;
//    }

    public class FriendsStatusAdapter extends RecyclerView.Adapter<FriendsStatusAdapter.FriendsStatusViewHolder> {
        private List<FriendsStatus> friendsStatusList;

        public FriendsStatusAdapter(List<FriendsStatus> friendsStatusList) {
            this.friendsStatusList = friendsStatusList;
        }

        @Override
        public FriendsStatusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.friends_status_item, parent, false);
            return new FriendsStatusViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(FriendsStatusViewHolder holder, int position) {
            FriendsStatus friendsStatus = friendsStatusList.get(position);
            holder.bind(friendsStatus);
        }

        @Override
        public int getItemCount() {
            return friendsStatusList.size();
        }

        public class FriendsStatusViewHolder extends RecyclerView.ViewHolder {
            private TextView text;

            public FriendsStatusViewHolder(View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.activity_text);
            }

            public void bind(FriendsStatus friendsStatus) {
                // Set the profile image, username, and status
                if(friendsStatus.getDistance() != null)
                    text.setText(itemView.getContext().getString(R.string.friends_activity_training, friendsStatus.getNickname(), friendsStatus.getDistance()));
                else if(friendsStatus.getPosition() != null)
                    text.setText(itemView.getContext().getString(R.string.friends_activity_race, friendsStatus.getNickname(), friendsStatus.getPosition(), friendsStatus.getRace()));
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);;
        this.statusView = view.findViewById(R.id.recyclerView_OnlineFriends);
        this.progressBar = view.findViewById(R.id.progressBar);
        this.scrollView = view.findViewById(R.id.scrollView2);
        this.progressBar.setVisibility(View.VISIBLE);
        this.scrollView.setVisibility(View.GONE);
        this.welcomeTextView = (TextView) view.findViewById(R.id.textView_WelcomeUser);
        this.showProfileButton = view.findViewById(R.id.button_YourProfile);
        this.friendsButton = view.findViewById(R.id.button_FriendsList);

        statusView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFriendsList();
            }
        });

        //settings
        this.settingsButton = view.findViewById(R.id.button_settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {goToSettings();}
        });
        //

        //show profile
        this.showProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {goToProfile();}
        });

        this.sp = this.getActivity().getSharedPreferences("msb", MODE_PRIVATE);

        loadData();

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        chooseDiscipline();

        return view;
    }

    private void goToFriendsList() {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new PersonFragment()).commit();
    }

    private void goToProfile() {
//        Intent i = new Intent(getActivity(), UserSettingsActivity.class);
//        startActivity(i);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).commit();
    }

    private void goToSettings() {
        Intent i = new Intent(getActivity(), UserSettingsActivity.class);
        startActivity(i);
    }

    private void loadData() {
        this.countCalls = new AtomicInteger(0);
        getUserFirstname();
        getFriendsActivities();
    }

    private void getFriendsActivities() {
        ApiInterface apiInterface = RetrofitClient.getInstance().create(ApiInterface.class);
        Call<List<FriendsStatus>> call = apiInterface.getActivities("Token " + sp.getString("token", ""));
        call.enqueue(new Callback<List<FriendsStatus>>() {
            @Override
            public void onResponse(Call<List<FriendsStatus>> call, Response<List<FriendsStatus>> response) {
                List<FriendsStatus> activities = response.body();
                statusAdapter = new FriendsStatusAdapter(activities);
                statusView.setAdapter(statusAdapter);
                if(countCalls.incrementAndGet() == 2) {
                    progressBar.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<FriendsStatus>> call, Throwable t) {

            }
        });
    }

    private void getUserFirstname() {
        ApiInterface apiInterface = RetrofitClient.getInstance().create(ApiInterface.class);
        Call<Username> call = apiInterface.getUsername("Token " + sp.getString("token", ""));
        call.enqueue(new Callback<Username>() {
            @Override
            public void onResponse(Call<Username> call, Response<Username> response) {
                if (response.isSuccessful()) {
                    String username = response.body().getUsername();
                    welcomeTextView.setText(getString(R.string.welcomeText, username));
                    if(countCalls.incrementAndGet() == 2) {
                        progressBar.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);
                    }
                } else {
//                    unsuccessful
                }
            }

            @Override
            public void onFailure(Call<Username> call, Throwable t) {
                //handle network problems
            }
        });
    }


    public void chooseDiscipline(){
        autoCompleteTxt = view.findViewById(R.id.auto_complete_txt);
        adapterItems = new ArrayAdapter<String>(getActivity(),R.layout.list_item,items);
        autoCompleteTxt.setText(items[0]);
        autoCompleteTxt.setAdapter(adapterItems);
        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                String item = parent.getItemAtPosition(position).toString();
//                Toast.makeText(getActivity().getApplicationContext(),"Item: "+item,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
