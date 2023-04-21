package com.example.here;

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
                else
                    registerUser();
            }
        });

    }

    private void registerUser() {
        
    }
}
