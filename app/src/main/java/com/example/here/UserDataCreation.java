package com.example.here;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class UserDataCreation extends AppCompatActivity {

    String[] genders = {"mężczyzna", "kobieta"};
    String[] countries;
    Spinner sexSpinner;
    Spinner countriesSpinner;

    DatePickerDialog datePickerDialog;
    TextView dateButton;

    NumberPicker heightPicker, weightPicker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_data_creation);

        //sex
        this.sexSpinner = findViewById(R.id.spinner_Sex);
        ArrayAdapter sexAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, genders);
        sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexSpinner.setAdapter(sexAdapter);
        sexSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //country
        countries = getResources().getStringArray(R.array.countries);
        this.countriesSpinner = findViewById(R.id.spinner_Country);
        ArrayAdapter countriesAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, countries);
        sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countriesSpinner.setAdapter(countriesAdapter);
        countriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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


        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());


    }

    public void goToMainActivity(){
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
        finish();
    }

}
