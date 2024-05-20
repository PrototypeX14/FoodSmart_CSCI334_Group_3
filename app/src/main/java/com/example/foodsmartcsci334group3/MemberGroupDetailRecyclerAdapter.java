package com.example.foodsmartcsci334group3;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class MemberGroupDetailRecyclerAdapter extends RecyclerView.Adapter<MemberGroupDetailRecyclerAdapter.ViewHolder>{
    private final int mUserId;
    private final Group mGroup;
    private final LayoutInflater mLayoutInflater;
    private final List<Integer> mMemberIds;
    private final Context mContext;

    public MemberGroupDetailRecyclerAdapter(int userId, Group group, Context context) {
        mUserId = userId;
        mGroup= group;
        mLayoutInflater = LayoutInflater.from(context);
        mMemberIds = mGroup.getUserIds();
        mContext = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.group_detail_member_card, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (!mGroup.getAdminIds().contains(mUserId)) {
            holder.kickButton.setVisibility(View.GONE);
            holder.adminRoleAssigner.setVisibility(View.GONE);
        }

        try {
            holder.mUser = User.getUser(mGroup.getUserIds().get(position), mContext);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        holder.icon.setImageResource(R.drawable.user);
        holder.username.setText(holder.mUser.getUsername());
        if (mGroup.isUserAdmin(holder.mUser.getId())) {
            holder.role.setText(R.string.admin);
        }
        else {
            holder.role.setText(R.string.member);
        }
    }

    @Override
    public int getItemCount() {
        return mMemberIds.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnItemSelectedListener {
        private User mUser;
        private final ImageView icon;
        private final TextView username;
        private final TextView role;
        private final Spinner adminRoleAssigner;
        private final Button kickButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.genericIcon);
            username = itemView.findViewById(R.id.memberName);
            role = itemView.findViewById(R.id.memberRank);
            kickButton = itemView.findViewById(R.id.adminKickUserButton);
            adminRoleAssigner = itemView.findViewById(R.id.adminRoleSpinner);
            kickButton.setOnClickListener(this::adminKick_OnClick);
            ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(
                    itemView.getContext(),
                    R.array.adminGroupSpinnerOptions,
                    android.R.layout.simple_spinner_item
            );
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            adminRoleAssigner.setAdapter(arrayAdapter);
            if (mGroup.isUserAdmin(mUserId)) {
                adminRoleAssigner.setSelection(1);
            }
        }

        public void adminKick_OnClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setCancelable(true);
            builder.setTitle("Confirm Kick");
            builder.setMessage("Are you certain that you want to kick this user?");
            builder.setPositiveButton(R.string.confirm,
                    (dialog, which) -> {
                        try {
                            mUser.leaveGroup(mGroup.getId(), mContext);
                        } catch (JSONException | IOException e) {
                            throw new RuntimeException(e);
                        }
                        mGroup.removeUserId(mUser.getId(), mContext);
                        notifyItemRemoved(getAdapterPosition());
                        dialog.dismiss();
                    });
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String choice = (String) parent.getItemAtPosition(position);
            if (choice.equals("Admin")) {
                mGroup.addAdmin(mUser.getId());
            } else if (choice.equals("Member")) {
                mGroup.removeAdmin(mUser.getId());
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
