package com.example.here.home;

import static android.content.Context.MODE_PRIVATE;

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.here.R;
import com.example.here.constants.ActivityType;
import com.example.here.restapi.ApiInterface;
import com.example.here.restapi.Firstname;
import com.example.here.restapi.RetrofitClient;
import com.example.here.restapi.Username;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Widok strony głównej

public class HomeFragment extends Fragment {

    private FriendStatusAdapter statusAdapter;
    private View view;
    private RecyclerView statusView;
    private SharedPreferences sp;
    private TextView welcomeTextView;
    private ProgressBar progressBar;
    private ScrollView scrollView;

    String[] items = {"Marsz","Bieganie","Jazda na rowerze","Kajakarstwo"};
    AutoCompleteTextView autoCompleteTxt;
    ArrayAdapter<String> adapterItems;

    private double lastTourStartV1 = 53.41178404163292, lastTourStartV2 = 23.516119474276664,           // pobierane z bazy danych / z pamieci urzadzenia
            lastTourEndV1 = 53.1276662351446, lastTourEndV2 = 23.160716949523863;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);;
        this.statusView = view.findViewById(R.id.recyclerView_OnlineFriends);
        this.progressBar = view.findViewById(R.id.progressBar);
        this.scrollView = view.findViewById(R.id.scrollView2);
        this.progressBar.setVisibility(View.VISIBLE);
        this.scrollView.setVisibility(View.GONE);
        this.welcomeTextView = (TextView) view.findViewById(R.id.textView_WelcomeUser);

        this.sp = this.getActivity().getSharedPreferences("msb", MODE_PRIVATE);

        loadData();

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        this.updateView();
        chooseDiscipline();

        return view;
    }

    private void loadData() {
        getUserFirstname();
    }

    private void getUserFirstname() {
        ApiInterface apiInterface = RetrofitClient.getInstance().create(ApiInterface.class);
        Call<Username> call = apiInterface.getUsername("Token " + sp.getString("token", ""));
        call.enqueue(new Callback<Username>() {
            @Override
            public void onResponse(Call<Username> call, Response<Username> response) {
                if (response.isSuccessful()) {
                    String username = response.body().getUsername();
                    //Log.d("retro", firstname);
                    welcomeTextView.setText(getString(R.string.welcomeText, username));
                    progressBar.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
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

    private void updateView(){
        FriendStatus example = new FriendStatus("brak", "Aga", ActivityType.CYCLING, 69, "Aga123");
        List<FriendStatus> friends =  new ArrayList<>();
        friends.add(example);

        if(statusAdapter == null){
            statusAdapter = new FriendStatusAdapter(friends);
            statusView.setAdapter(statusAdapter);
        }else{
            statusAdapter.notifyDataSetChanged();
        }
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
        this.updateView();
    }

    private class FriendStatusHolder extends RecyclerView.ViewHolder{
        private FriendStatus status;
        private TextView nickname;
        private final TextView text;
        private final ImageView icon;
        public FriendStatusHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.status_list_item, parent, false));

            this.text = itemView.findViewById(R.id.status_item_text);
            this.icon = itemView.findViewById(R.id.status_item_image);
            this.nickname = itemView.findViewById(R.id.status_item_nickname);
        }

        public void bind(FriendStatus status){
            this.status = status;
            text.setText(status.getInfo());
            nickname.setText(status.getNickname());
            icon.setImageBitmap(BitmapFactory.decodeFile(this.status.getImageSource()));
        }
    }

    private class FriendStatusAdapter extends RecyclerView.Adapter<FriendStatusHolder>{
        private final List<FriendStatus> status;
        public FriendStatusAdapter(List<FriendStatus> status){
            this.status = status;
        }
        @NonNull
        @Override
        public FriendStatusHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new FriendStatusHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull FriendStatusHolder holder, int position) {
            FriendStatus friendStatus = this.status.get(position);
            holder.bind(friendStatus);
        }

        @Override
        public int getItemCount() {
            return status.size();
        }
    }
}
