package com.example.foodsmartcsci334group3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {

    private int userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getIntent().getIntExtra(MainActivity.ARG_USERID, -1);
        setContentView(R.layout.main_menu_activity);
        findViewById(R.id.groupsButton).setOnClickListener(this::groupOnClick);
        findViewById(R.id.shoppingListButton).setOnClickListener(this::shoppingListOnClick);
    }

    private void groupOnClick(View v) {
        Intent intent = new Intent(this, GroupsActivity.class);
        intent.putExtra(MainActivity.ARG_USERID, userId);
        startActivity(intent);
    }

    private void shoppingListOnClick(View v) {
        Intent intent = new Intent(this, ShoppingListActivity.class);
        intent.putExtra(MainActivity.ARG_USERID, userId);
        startActivity(intent);
    }
}
