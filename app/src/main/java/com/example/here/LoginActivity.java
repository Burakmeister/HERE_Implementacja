package com.example.here;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.here.restapi.ApiInterface;
import com.example.here.restapi.Credentials;
import com.example.here.restapi.RetrofitClient;
import com.example.here.restapi.Token;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    Button login;
    EditText email, password;
    List<EditText> editTexts = new ArrayList<>();
    TextView registerButton;
    SharedPreferences sp;

    ProgressBar progressBar;
    LinearLayout linearLayout;
    private boolean formIncomplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        progressBar = findViewById(R.id.progressBar);
        linearLayout = findViewById(R.id.linearLayout);

        login = (Button) findViewById(R.id.button_Login);
        email = (EditText) findViewById(R.id.editText_Email);
        password = (EditText) findViewById(R.id.editText_Password_Again);
        registerButton = (TextView) findViewById(R.id.registerTextView);

        editTexts.add(email);
        editTexts.add(password);

        sp = getSharedPreferences("msb",MODE_PRIVATE);
//        sp.edit().putBoolean("logged", false).apply(); // FOR TESTING!!!!11

        if(sp.getBoolean("logged",false)){
            goToMainActivity();
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(EditText et : editTexts) {
                    if(et.getText().toString().isEmpty()) {
                        et.setBackgroundResource(R.drawable.edit_text_wrong);
                        formIncomplete = true;
                    }
                    else {
                        et.setBackgroundResource(R.drawable.edittext_background);
                        formIncomplete = false;
                    }
                }
                if(formIncomplete)
                    Toast.makeText(getApplicationContext(), R.string.please_complete_the_form, Toast.LENGTH_SHORT).show();
                else
                    authenticateUser(email.getText().toString(), password.getText().toString());
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegisterActivity();
            }
        });
    }

    private void authenticateUser(String email, String password) {

        startLoading();

        ApiInterface apiInterface = RetrofitClient.getInstance().create(ApiInterface.class);
        Call<Token> call = apiInterface.getAuthToken(new Credentials(email, password));
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful()) {
                    String token = response.body().getToken();
                    sp.edit().putString("token", token).apply();
                    sp.edit().putBoolean("logged",true).apply();
                    goToMainActivity();
                } else {
                    stopLoading();
                    Toast.makeText(getApplicationContext(), R.string.wrong_email_or_password,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                //handle network problems
            }
        });
    }

    public void goToMainActivity(){
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
        finish();
    }

    public void goToRegisterActivity(){
        Intent i = new Intent(this,RegisterActivity.class);
        startActivity(i);
    }

    private void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);
    }

    private void stopLoading() {
        progressBar.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);
    }

}