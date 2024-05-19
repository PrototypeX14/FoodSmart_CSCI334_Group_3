package com.example.foodsmartcsci334group3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CreateGroupUserRecyclerAdapter extends RecyclerView.Adapter<CreateGroupUserRecyclerAdapter.ViewHolder>{
    private final List<User> mUsers;
    private final LayoutInflater mLayoutInflater;

    public CreateGroupUserRecyclerAdapter(Context context, List<User> members){
        mUsers = members;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.user_card, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mUserIcon.setImageResource(R.drawable.user);
        holder.mUserName.setText(mUsers.get(position).getUsername());
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mUserIcon;
        private final TextView mUserName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mUserIcon = itemView.findViewById(R.id.genericIcon);
            mUserName = itemView.findViewById(R.id.usernameCardLabel);

            itemView.setOnClickListener(v -> {
                mUsers.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
            });
        }
    }
}
