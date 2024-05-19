package com.example.foodsmartcsci334group3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.io.IOException;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);
        findViewById(R.id.signUpContinueButton).setOnClickListener(this::signUp_OnClick);
    }

    private void signUp_OnClick(View v){
        EditText emailInput = findViewById(R.id.emailAddressInput);
        EditText passwordInput = findViewById(R.id.signUpPasswordInput);
        EditText confirmPasswordInput = findViewById(R.id.signUpConfirmPassword);
        if(Objects.equals(passwordInput.getText().toString(), confirmPasswordInput.getText().toString())) {
            User user;
            try {
                user = User.createUser(emailInput.getText().toString(), passwordInput.getText().toString(), this);
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
            Intent intent = new Intent(this, MainMenuActivity.class);
            intent.putExtra(MainActivity.ARG_USERID, user.getId());
            startActivity(intent);
        }
        else {
            findViewById(R.id.passwordsDoNotMatchLabel).setVisibility(View.VISIBLE);
        }
    }
}
