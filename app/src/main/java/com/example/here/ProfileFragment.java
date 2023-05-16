package com.example.here;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.here.home.HomeFragment;
import com.example.here.models.UserData;
import com.example.here.restapi.ApiInterface;
import com.example.here.restapi.Name;
import com.example.here.restapi.RetrofitClient;
import com.example.here.restapi.UserEmail;
import com.example.here.restapi.Username;

import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment{
    private ApiInterface apiInterface;
    private SharedPreferences sp;
    private String token;
    private View view;
    private Button editButton;
    private TextView birthDateText;
    private TextView countryText;
    private TextView sexText;
    private TextView heightText;
    private TextView weightText;

    private TextView nickText;
    private TextView emailText;

    private ProgressBar progressBar;
    private ScrollView scrollView;

    public ProfileFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.user_account_view, container, false);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        this.apiInterface = RetrofitClient.getInstance().create(ApiInterface.class);
        this.sp = getActivity().getSharedPreferences("msb",MODE_PRIVATE);
        this.token = sp.getString("token", "");

        this.scrollView = view.findViewById(R.id.profile_scroll);
        this.progressBar = view.findViewById(R.id.progressBar);

        //edit button
        this.editButton = view.findViewById(R.id.edit_button);
        this.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEditUserData();
            }
        });

        //bday
        this.birthDateText = view.findViewById(R.id.birt_date_content_value_txt);

        //country
        this.countryText = view.findViewById(R.id.country_content_value_txt);

        //sex
        this.sexText = view.findViewById(R.id.gender_content_content_value_text);

        //height
        this.heightText = view.findViewById(R.id.height_content_value_text);

        //weight
        this.weightText = view.findViewById(R.id.weight_content_value_text);

        //nick
        this.nickText = view.findViewById(R.id.user_nick_txt);

        //email
        this.emailText = view.findViewById(R.id.email_text);

        setDataToViews();

        return view;
    }

    private void setDataToViews() {

        startLoading();
        AtomicInteger finishedLoading = new AtomicInteger(0);

        Call<UserData> dataCall = apiInterface.getUserData("Token " + token);
        Call<UserEmail> emailCall = apiInterface.getEmail("Token " + token);


        emailCall.enqueue(new Callback<UserEmail>() {
            @Override
            public void onResponse(Call<UserEmail> call, Response<UserEmail> response) {
                String email = response.body().getEmail();

                emailText.setText(email);

                if(finishedLoading.incrementAndGet() == 2)
                    stopLoading();

            }

            @Override
            public void onFailure(Call<UserEmail> call, Throwable t) {

            }
        });

        dataCall.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                UserData userData = response.body();

                nickText.setText(userData.getNick());

                String s = userData.getSex() == 'M' ? "mężczyzna" : "kobieta";

                sexText.setText(s);
                countryText.setText(userData.getCountry());

                heightText.setText(String.valueOf(userData.getHeight()));
                weightText.setText(String.valueOf(userData.getWeight()));

                birthDateText.setText(userData.getBirthDate());

                if(finishedLoading.incrementAndGet() == 2)
                    stopLoading();

            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {

            }
        });

    }

    private void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
    }

    private void stopLoading() {
        progressBar.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
    }

    private void goToEditUserData() {
        Intent i = new Intent(getActivity(), EditUserData.class);
        startActivity(i);
    }

    @Override
    public void onResume() {
        setDataToViews();
        super.onResume();
    }
}
