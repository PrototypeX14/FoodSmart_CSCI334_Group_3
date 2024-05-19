package com.example.foodsmartcsci334group3;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

public class MyGroupsRecyclerAdapter extends RecyclerView.Adapter<MyGroupsRecyclerAdapter.ViewHolder>{

    private final Context mContext;
    private final User mUser;
    private final LayoutInflater mLayoutInflater;

    public MyGroupsRecyclerAdapter(Context context, User user) {
        mContext = context;
        mUser = user;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.group_card, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        List<Group> userGroups;
        try {
            userGroups = Group.getUsersGroups(mContext, mUser.getId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!userGroups.isEmpty()) {
            holder.mGroupName.setText(userGroups.get(position).getName());
            holder.mGroupIcon.setImageResource(userGroups.get(position).getIconReference());
            holder.mGroupId = userGroups.get(position).getId();
        }
    }

    @Override
    public int getItemCount() {
        return mUser.getGroupIds().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mGroupIcon;
        private final TextView mGroupName;
        private int mGroupId;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mGroupIcon = itemView.findViewById(R.id.genericIcon);
            mGroupName = itemView.findViewById(R.id.groupNameLabel);

            itemView.setOnClickListener((v) -> {
                Intent intent = new Intent(mContext, GroupDetailActivity.class);
                intent.putExtra(MainActivity.ARG_USERID, mUser.getId());
                intent.putExtra(MainActivity.ARG_GROUPID, mGroupId);
                itemView.getContext().startActivity(intent);
            });
        }
    }
}
