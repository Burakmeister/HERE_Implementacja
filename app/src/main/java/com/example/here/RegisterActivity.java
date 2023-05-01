package com.example.here;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.here.restapi.ApiInterface;
import com.example.here.restapi.RegisterCredentials;
import com.example.here.restapi.RetrofitClient;
import com.example.here.restapi.Token;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    SharedPreferences sp;
    EditText nickEdit, emailEdit, passwordEdit, repeatPasswordEdit, firstNameEdit, lastNameEdit;
    List<EditText> requiredEditTextList = new ArrayList<>();
    boolean formIncomplete = false;
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
        firstNameEdit = findViewById(R.id.editText_FirstName);
        lastNameEdit = findViewById(R.id.editText_LastName);

        requiredEditTextList.add(nickEdit);
        requiredEditTextList.add(emailEdit);
        requiredEditTextList.add(passwordEdit);
        requiredEditTextList.add(repeatPasswordEdit);

        sp = getSharedPreferences("msb",MODE_PRIVATE);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(EditText et : requiredEditTextList) {
                    if(et.getText().toString().isEmpty()) {
                        et.setBackgroundResource(R.drawable.edit_text_wrong);
                        formIncomplete = true;
                    }
                    else {
                        et.setBackgroundResource(R.drawable.edittext_background);
                        formIncomplete = false;
                    }
                }
                if(formIncomplete) {
                    Toast.makeText(getApplicationContext(), R.string.please_complete_the_form, Toast.LENGTH_SHORT).show();
                }
                else if(!passwordEdit.getText().toString().equals(repeatPasswordEdit.getText().toString())) {
                    Toast.makeText(getApplicationContext(), R.string.wrong_repeated_password, Toast.LENGTH_SHORT).show();
                }
                else
                    registerUser(nickEdit.getText().toString(), emailEdit.getText().toString(), passwordEdit.getText().toString(), firstNameEdit.getText().toString(), lastNameEdit.getText().toString());
            }
        });

    }

    private void registerUser(String username, String email, String password, String firstName, String lastName) {
        ApiInterface apiInterface = RetrofitClient.getInstance().create(ApiInterface.class);
        Call<Token> call = apiInterface.register(new RegisterCredentials(firstName, lastName, username, email, password));
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful()) {
                    String token = response.body().getToken();
                    sp.edit().putString("token", token).commit();
                    sp.edit().putBoolean("logged",true).apply();
                    goToUserDataCreationActivity(username);
                } else {
                    Toast.makeText(getApplicationContext(), response.message(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                //handle network problems
            }
        });
    }

    public void goToUserDataCreationActivity(String username){
        Intent i = new Intent(this,UserDataCreation.class);
        i.putExtra("nick", username);
        startActivity(i);
        finish();
    }

}
