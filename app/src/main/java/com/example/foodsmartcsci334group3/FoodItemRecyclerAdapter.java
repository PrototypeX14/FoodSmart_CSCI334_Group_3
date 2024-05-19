package com.example.foodsmartcsci334group3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class FoodItemRecyclerAdapter extends RecyclerView.Adapter<FoodItemRecyclerAdapter.ViewHolder>{
    private final LayoutInflater mLayoutInflater;
    private final List<FoodItem> mFoodItems;
    private final ShoppingList mShoppingList;
    private final Context mContext;
    private ClickDetector mClickDetector;

    public FoodItemRecyclerAdapter(Context context, List<FoodItem> foodItems, ShoppingList shoppingList, ClickDetector clickDetector) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mFoodItems = foodItems;
        mShoppingList = shoppingList;
        mClickDetector = clickDetector;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.food_item_recycler_card, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mItemName.setText(mFoodItems.get(position).getName());
        holder.mFoodItem = mFoodItems.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mShoppingList.addItem(new ValuePair<>(holder.mFoodItem, 1), mContext);
                } catch (JSONException | IOException e) {
                    throw new RuntimeException(e);
                }
                mClickDetector.clicked(holder.getAdapterPosition(), "item added");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFoodItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mItemName;
        private FoodItem mFoodItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemName = itemView.findViewById(R.id.foodItemRecyclerName);
        }
    }
}
