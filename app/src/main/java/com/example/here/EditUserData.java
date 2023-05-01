package com.example.here;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.here.models.UserData;
import com.example.here.restapi.ApiInterface;
import com.example.here.restapi.Name;
import com.example.here.restapi.RetrofitClient;
import com.example.here.restapi.Username;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserData extends AppCompatActivity {

    ApiInterface apiInterface;
    SharedPreferences sp;

    EditText firstNameEdit, lastNameEdit;
    EditText nicknameEdit;
    String[] genders;
    String[] countries;
    Spinner sexSpinner;
    Spinner countriesSpinner;

    DatePickerDialog datePickerDialog;
    Button dateButton;

    NumberPicker heightPicker, weightPicker;

    Button addDataButton;

    ArrayAdapter countriesAdapter;

    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_data);

        apiInterface  = RetrofitClient.getInstance().create(ApiInterface.class);
        sp = getSharedPreferences("msb",MODE_PRIVATE);
        token = sp.getString("token", "");

        //first and last name
        firstNameEdit = findViewById(R.id.editText_FirstName);
        lastNameEdit = findViewById(R.id.editText_LastName);

        //nickname
        nicknameEdit = findViewById(R.id.editText_Nick);

        //sex
        genders = getResources().getStringArray(R.array.genders);
        this.sexSpinner = findViewById(R.id.spinner_Sex);
        ArrayAdapter sexAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, genders);
        sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexSpinner.setAdapter(sexAdapter);

        //country
        countries = getResources().getStringArray(R.array.countries);
        this.countriesSpinner = findViewById(R.id.spinner_Country);
        countriesAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, countries);
        countriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

        setDataToViews();

    }

    private void setDataToViews() {

        Call<Name> nameCall = apiInterface.getName("Token " + token);
        Call<UserData> dataCall = apiInterface.getUserData("Token " + token);

        nameCall.enqueue(new Callback<Name>() {
            @Override
            public void onResponse(Call<Name> call, Response<Name> response) {
                if (response.isSuccessful()) {
                    String firstName = response.body().getFirst_name();
                    String lastName = response.body().getLast_name();

                    firstNameEdit.setText(firstName);
                    lastNameEdit.setText(lastName);

                } else {
//                    unsuccessful
                }
            }

            @Override
            public void onFailure(Call<Name> call, Throwable t) {
                //handle network problems
            }
        });

        dataCall.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                UserData userData = response.body();

                nicknameEdit.setText(userData.getNick());

                int s = userData.getSex() == 'M' ? 0 : 1;
                sexSpinner.setSelection(s);

                int c = countriesAdapter.getPosition(userData.getCountry());
                if(c != -1) {
                    countriesSpinner.setSelection(c);
                }

                heightPicker.setValue(userData.getHeight());
                weightPicker.setValue(userData.getWeight().intValue());

            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {

            }
        });

    }

    private void addData() {

        if(nicknameEdit.getText().toString().isEmpty()) {
            nicknameEdit.setBackgroundResource(R.drawable.edit_text_wrong);
            Toast.makeText(getApplicationContext(), R.string.please_complete_the_form, Toast.LENGTH_SHORT).show();
            return;
        }

        nicknameEdit.setBackgroundResource(R.drawable.edittext_background);

        String firstName = firstNameEdit.getText().toString();
        String lastname = lastNameEdit.getText().toString();

        String nick = nicknameEdit.getText().toString();
        char sex = sexSpinner.getSelectedItemPosition() == 0 ? 'M' : 'F';
        String country = (String) countriesSpinner.getSelectedItem();
        int height = heightPicker.getValue();
        int weight = weightPicker.getValue();

        UserData userData = new UserData();
        userData.setNick(nick);
        userData.setSex(sex);
        userData.setCountry(country);
        userData.setHeight(height);
        userData.setWeight((float) weight);

        Call<Void> nameCall = apiInterface.editName("Token " + token, new Name(firstName, lastname));
        Call<Void> dataCall = apiInterface.editData("Token " + token, userData);

        nameCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("ass", firstName+" "+lastname);
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong,Toast.LENGTH_SHORT).show();
            }
        });
        dataCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong,Toast.LENGTH_SHORT).show();
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

}