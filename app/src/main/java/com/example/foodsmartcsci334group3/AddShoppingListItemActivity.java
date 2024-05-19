package com.example.foodsmartcsci334group3;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

public class AddShoppingListItemActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_shopping_list_item);
        RecyclerView recyclerView = findViewById(R.id.foodItemRecycler);
        int userId = getIntent().getIntExtra(MainActivity.ARG_USERID, -1);
        ShoppingList shoppingList;
        try {
            shoppingList = ShoppingList.getShoppingListFromId(this, userId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ClickDetector listener = (position, info) -> {

        };

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        List<FoodItem> itemList;
        try {
            itemList = FoodItem.loadFoodItems(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FoodItemRecyclerAdapter foodItemRecyclerAdapter = new FoodItemRecyclerAdapter(this, itemList, shoppingList, listener);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(foodItemRecyclerAdapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}