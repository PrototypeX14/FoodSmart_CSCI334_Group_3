package com.example.foodsmartcsci334group3;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class GroupDetailActivity extends AppCompatActivity {
    private Group mGroup;
    private String memberUsername;
    private MemberGroupDetailRecyclerAdapter mRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_group_detail_activty);
        int userId = getIntent().getIntExtra(MainActivity.ARG_USERID, -1);
        int groupId = getIntent().getIntExtra(MainActivity.ARG_GROUPID, -1);
        RecyclerView gMemberRecycler = findViewById(R.id.groupDetailMemberRecycler);
        try {
            mGroup = Group.getGroupFromId(groupId, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerAdapter = new MemberGroupDetailRecyclerAdapter(userId, mGroup, this);
        gMemberRecycler.setLayoutManager(layoutManager);
        gMemberRecycler.setAdapter(mRecyclerAdapter);

        EditText groupName = findViewById(R.id.groupNameEditText);
        groupName.setText(mGroup.getName());
        EditText groupPassword = findViewById(R.id.groupPasswordEditText);
        groupPassword.setText(mGroup.getPassword());

        ImageView imageView = findViewById(R.id.genericIcon);
        imageView.setImageResource(mGroup.getIconReference());

        if (mGroup.isUserAdmin(userId)) {
            findViewById(R.id.groupDetailName).setVisibility(View.INVISIBLE);
            findViewById(R.id.groupDetailPassword).setVisibility(View.INVISIBLE);
            groupName.setVisibility(View.VISIBLE);
            groupPassword.setVisibility(View.VISIBLE);
        }
        groupName.setOnFocusChangeListener((v, hasFocus) -> {
            EditText editText = (EditText) v;
            if(!hasFocus) {
                if (!editText.getText().toString().equals(mGroup.getName())) {
                    mGroup.setName(editText.getText().toString(), v.getContext());
                }
            }
        });

        groupPassword.setOnFocusChangeListener((v, hasFocus) -> {
            EditText editText = (EditText) v;
            if(!hasFocus) {
                if (!editText.getText().toString().equals(mGroup.getPassword())) {
                    mGroup.setPassword(editText.getText().toString(), v.getContext());
                }
            }
        });

        findViewById(R.id.addMembersFab).setOnClickListener(this::addMemberFab_OnClick);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void addMemberFab_OnClick(View v) {
        AddMemberDialogFragment dialog = new AddMemberDialogFragment();
        dialog.setDialogResult(new AddMemberDialogFragment.AddMemberDialogResult() {
            @Override
            public void onDialogFinish(String result) {
                memberUsername = result;
                List<User> allUsers;
                try {
                    allUsers = User.getUsers(getApplicationContext());
                } catch (IOException | JSONException e) {
                    throw new RuntimeException(e);
                }
                for (User u: allUsers) {
                    if (u.getUsername().equals(memberUsername)) {
                        mGroup.addUserId(u.getId(), v.getContext());
                        mRecyclerAdapter.notifyItemInserted(mGroup.getUserIds().size() - 1);
                    }
                }
            }
        });
        dialog.show(getSupportFragmentManager(), "USER_ADD");
        dialog.setDialogResult(result -> {
            memberUsername = result;
            List<User> allUsers;
            try {
                allUsers = User.getUsers(getApplicationContext());
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
            for (User u: allUsers) {
                if (u.getUsername().equals(memberUsername)) {
                    mGroup.addUserId(u.getId(), v.getContext());
                    mRecyclerAdapter.notifyItemInserted(mGroup.getUserIds().size() - 1);
                }
            }
        });
    }
}