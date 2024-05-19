package com.example.foodsmartcsci334group3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.Objects;

public class ShoppingListActivity extends AppCompatActivity {
    private int userId;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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