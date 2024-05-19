package com.example.foodsmartcsci334group3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;

import java.io.IOException;

public class ShoppingListRecyclerAdapter extends RecyclerView.Adapter<ShoppingListRecyclerAdapter.ViewHolder> {
    private final ShoppingList mShoppingList;
    private final LayoutInflater mLayoutInflater;

    public ShoppingListRecyclerAdapter(ShoppingList shoppingList, Context context) {
        mShoppingList = shoppingList;
        mLayoutInflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.shopping_list_item_card, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mFoodName.setText(mShoppingList.getFoodList().get(position).getLabel().getName());
        holder.mQuantity.setText(mShoppingList.getFoodList().get(position).getValue());
    }

    @Override
    public int getItemCount() {
        return mShoppingList.getFoodList().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mFoodName;
        private final EditText mQuantity;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mFoodName = itemView.findViewById(R.id.foodItemName);
            FloatingActionButton fabPlus = itemView.findViewById(R.id.addItemFab);
            FloatingActionButton fabMinus = itemView.findViewById(R.id.subtractItemFab);
            mQuantity = itemView.findViewById(R.id.shoppingListQuantity);
            fabMinus.setOnClickListener(v -> {
                try {
                    mShoppingList.reduceItemCount(getAdapterPosition(), itemView.getContext());
                } catch (JSONException | IOException e) {
                    throw new RuntimeException(e);
                }
                mQuantity.setText(String.valueOf (Integer.parseInt(mQuantity.getText().toString()) - 1));
            });
            fabPlus.setOnClickListener(v -> {
                try {
                    mShoppingList.addItemCount(getAdapterPosition(), itemView.getContext());
                } catch (JSONException | IOException e) {
                    throw new RuntimeException(e);
                }
                mQuantity.setText(String.valueOf (Integer.parseInt(mQuantity.getText().toString()) + 1));
            });
        }

    }
}
