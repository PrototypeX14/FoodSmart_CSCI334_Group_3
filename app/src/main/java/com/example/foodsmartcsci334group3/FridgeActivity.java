package com.example.foodsmartcsci334group3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodsmartcsci334group3.databinding.ActivityFridgeBinding;

import java.io.IOException;
import java.util.Objects;

public class FridgeActivity extends AppCompatActivity {

    private int userId;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        userId = getIntent().getIntExtra(MainActivity.ARG_USERID, -1);

        mRecyclerView = findViewById(R.id.shoppingListRecycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        ShoppingListRecyclerAdapter adapter;
        try {
            adapter = new ShoppingListRecyclerAdapter(ShoppingList.getShoppingListFromId(this, userId), this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
        Button addItemButton = findViewById(R.id.addShoppingListItemButton);
        addItemButton.setOnClickListener(this::addItem_OnClick);
    }

    public void addItem_OnClick (View v) {
        Intent intent = new Intent(this, AddShoppingListItemActivity.class);
        intent.putExtra(MainActivity.ARG_USERID, userId);
        startActivityForResult(intent, 201);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Objects.requireNonNull(mRecyclerView.getAdapter()).notifyDataSetChanged();

    }
}