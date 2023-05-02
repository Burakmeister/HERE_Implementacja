package com.example.here;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.here.models.UserData;
import com.example.here.restapi.ApiInterface;
import com.example.here.restapi.RegisterCredentials;
import com.example.here.restapi.RetrofitClient;
import com.example.here.restapi.Token;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDataCreation extends AppCompatActivity {

    SharedPreferences sp;

    EditText nicknameEdit;
    String[] genders;
    String[] countries;
    Spinner sexSpinner;
    Spinner countriesSpinner;

    DatePickerDialog datePickerDialog;
    Button dateButton;

    NumberPicker heightPicker, weightPicker;

    Button addDataButton;
    TextView skipButton;

    ProgressBar progressBar;
    ScrollView scrollView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_data_creation);

        progressBar = findViewById(R.id.progressBar);
        scrollView = findViewById(R.id.scroll);

        sp = getSharedPreferences("msb",MODE_PRIVATE);

        //nickname
        nicknameEdit = findViewById(R.id.editText_Nick);

        String nick = getIntent().getExtras().getString("nick");
        nicknameEdit.setText(nick);

        //sex
        genders = getResources().getStringArray(R.array.genders);
        this.sexSpinner = findViewById(R.id.spinner_Sex);
        ArrayAdapter sexAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, genders);
        sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexSpinner.setAdapter(sexAdapter);

        //country
        countries = getResources().getStringArray(R.array.countries);
        this.countriesSpinner = findViewById(R.id.spinner_Country);
        ArrayAdapter countriesAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, countries);
        sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countriesSpinner.setAdapter(countriesAdapter);

        //bday
        dateButton = findViewById(R.id.birth_date_btn);
        dateButton.setText(today());
        initDatePicker();
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        //height and weight
        heightPicker = findViewById(R.id.height_picker);
        heightPicker.setMinValue(0);
        heightPicker.setMaxValue(300);

        weightPicker = findViewById(R.id.weight_picker);
        weightPicker.setMinValue(0);
        weightPicker.setMaxValue(300);

        //confirm button

        addDataButton = findViewById(R.id.button_SignInButton);
        addDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {addData();}
        });

        //skip

        skipButton = findViewById(R.id.skip_data_creation_btn);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {goToMainActivity();}
        });

    }

    private void addData() {
        if(nicknameEdit.getText().toString().isEmpty()) {
            nicknameEdit.setBackgroundResource(R.drawable.edit_text_wrong);
            Toast.makeText(getApplicationContext(), R.string.please_complete_the_form, Toast.LENGTH_SHORT).show();
            return;
        }

        startLoading();

        nicknameEdit.setBackgroundResource(R.drawable.edittext_background);

        ApiInterface apiInterface = RetrofitClient.getInstance().create(ApiInterface.class);

        UserData userData = new UserData();
        userData.setNick(nicknameEdit.getText().toString());
        char sex = sexSpinner.getSelectedItemPosition() == 0 ? 'M' : 'F';
        userData.setSex(sex);
        userData.setCountry(countriesSpinner.getSelectedItem().toString());
        userData.setHeight(heightPicker.getValue());
        userData.setWeight((float) weightPicker.getValue());

        Call<Void> call = apiInterface.addData("Token " + sp.getString("token", ""), userData);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    goToMainActivity();
                } else {
                    stopLoading();
                    Toast.makeText(getApplicationContext(), R.string.something_went_wrong,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                //handle network problems
            }
        });
    }

    private String today() {

        Calendar calendar = Calendar.getInstance();
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH);
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        return dateFromValues(y,m+1,d);
    }

    private String dateFromValues(int y, int m, int d) {
        return d+"/"+m+"/"+y;
    }

    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                dateButton.setText(dateFromValues(year,month+1,day));
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
        }

    }

    private void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
    }

    private void stopLoading() {
        progressBar.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
    }

    public void goToMainActivity(){
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
        finish();
    }

}
