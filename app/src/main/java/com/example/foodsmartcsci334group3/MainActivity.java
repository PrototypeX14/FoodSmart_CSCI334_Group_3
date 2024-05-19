package com.example.foodsmartcsci334group3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static final String ARG_FOODITEMNAME = "foodName";
    public static final String ARG_USERID = "userId";
    public static final String ARG_GROUPID = "groupId";
    private List<User> mUserList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        findViewById(R.id.signInButton).setOnClickListener(this::signIn_onClick);
        findViewById(R.id.signUpButton).setOnClickListener(this::signIn_signUp_onClick);
        try {
            mUserList = User.getUsers(getApplicationContext());
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void signIn_onClick(View v) {
        EditText usernameInput = findViewById(R.id.usernameInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        boolean possiblePasswordIncorrect = false;
        for (User user : mUserList) {
            if (Objects.equals(user.getUsername(), usernameInput.getText().toString()) && Objects.equals(user.getPassword(), passwordInput.getText().toString())) {
                Intent intent = new Intent(this, MainMenuActivity.class);
                intent.putExtra(ARG_USERID, user.getId());
                startActivity(intent);
            }
            else if (Objects.equals(user.getUsername(), usernameInput.getText().toString()) && !Objects.equals(user.getPassword(), passwordInput.getText().toString())) {
                possiblePasswordIncorrect = true;
            }
        }
        TextView invalidSignIn = findViewById(R.id.invalidSignIn);
        if(possiblePasswordIncorrect) {
            invalidSignIn.setText(R.string.password_incorrect);
        }
        invalidSignIn.setVisibility(View.VISIBLE);
    }

    private void signIn_signUp_onClick(View v) {
        Intent intent = new Intent(this, SignUpActivity.class);
        this.startActivity(intent);
    }
}
