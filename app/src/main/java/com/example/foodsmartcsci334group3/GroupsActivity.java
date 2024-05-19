package com.example.foodsmartcsci334group3;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class GroupsActivity extends AppCompatActivity {
    private int mUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups_activity);
        mUserId = getIntent().getIntExtra(MainActivity.ARG_USERID, -1);
        RecyclerView myGroupsRecycler = findViewById(R.id.myGroupsRecycler);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        myGroupsRecycler.setLayoutManager(layoutManager);
        User user;
        try {
            user = User.getUser(mUserId, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MyGroupsRecyclerAdapter myGroupsRecyclerAdapter = new MyGroupsRecyclerAdapter(this, user);
        myGroupsRecycler.setAdapter(myGroupsRecyclerAdapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_create_group) {
            Intent intent = new Intent(this, CreateGroupActivity.class);
            intent.putExtra(MainActivity.ARG_USERID, mUserId);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_join_group) {
            JoinGroupDialogFragment dialogFragment = new JoinGroupDialogFragment();
            dialogFragment.show(getSupportFragmentManager(), "GROUP_JOIN");
            dialogFragment.setJoinGroupDialogResult((resultName, resultPassword) -> {
                List<Group> allGroups;
                try {
                    allGroups = Group.getGroups(getApplicationContext());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (!allGroups.isEmpty()) {
                    for(Group g:allGroups) {
                        if (g.getName().equals(resultName) && g.getPassword().equals(resultPassword)) {
                            g.addUserId(mUserId, this);
                            User user;
                            try {
                                user = User.getUser(mUserId, getApplicationContext());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            try {
                                user.joinGroup(g.getId(), getApplicationContext());
                            } catch (JSONException | IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
