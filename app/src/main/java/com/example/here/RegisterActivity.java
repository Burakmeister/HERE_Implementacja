package com.example.here;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.here.restapi.ApiInterface;
import com.example.here.restapi.Credentials;
import com.example.here.restapi.RegisterCredentials;
import com.example.here.restapi.RetrofitClient;
import com.example.here.restapi.Token;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    SharedPreferences sp;
    TextView nickEdit, emailEdit, passwordEdit, repeatPasswordEdit;
    Button registerButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        nickEdit = findViewById(R.id.editText_Nick);
        emailEdit = findViewById(R.id.editText_SignIn_Email);
        passwordEdit = findViewById(R.id.editText_Password);
        repeatPasswordEdit = findViewById(R.id.editText_Password_Again);
        registerButton = findViewById(R.id.button_SignInButton);

        sp = getSharedPreferences("msb",MODE_PRIVATE);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(
                        nickEdit.getText().toString().equals("") ||
                                emailEdit.getText().toString().equals("") ||
                                passwordEdit.getText().toString().equals("") ||
                                repeatPasswordEdit.getText().toString().equals("")
                ) {
                    Toast.makeText(getApplicationContext(), R.string.please_complete_the_form, Toast.LENGTH_SHORT).show();
                }
                else if(!passwordEdit.getText().toString().equals(repeatPasswordEdit.getText().toString())) {
                    Toast.makeText(getApplicationContext(), R.string.wrong_repeated_password, Toast.LENGTH_SHORT).show();
                }
                else
                    registerUser(nickEdit.getText().toString(), emailEdit.getText().toString(), passwordEdit.getText().toString());
            }
        });

    }

    private void registerUser(String username, String email, String password) {
        ApiInterface apiInterface = RetrofitClient.getInstance().create(ApiInterface.class);
        Call<Token> call = apiInterface.register(new RegisterCredentials(username, email, password));
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful()) {
                    String token = response.body().getToken();
                    sp.edit().putString("token", token).commit();
                    sp.edit().putBoolean("logged",true).apply();
                    goToMainActivity();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.something_went_wrong,Toast.LENGTH_SHORT).show();
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

}
