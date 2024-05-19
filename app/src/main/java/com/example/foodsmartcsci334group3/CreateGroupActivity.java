package com.example.foodsmartcsci334group3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateGroupActivity extends AppCompatActivity{

    private ImageView mGroupIcon;
    private EditText mGroupName;
    private EditText mGroupPassword;
    private List<Integer> members;
    private List<User> addedMembers;
    private String memberUsername;
    private String groupIconPath;
    private CreateGroupUserRecyclerAdapter mAdapter;
    private int mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_group);

        mGroupIcon = findViewById(R.id.genericIcon);
        mGroupIcon.setOnClickListener(this::uploadImage_OnClick);
        mGroupName = findViewById(R.id.groupCreateNameInput);
        mGroupPassword = findViewById(R.id.groupCreatePasswordInput);
        RecyclerView memberRecycler = findViewById(R.id.memberRecycler);
        addedMembers = new ArrayList<>();
        mUserId = getIntent().getIntExtra(MainActivity.ARG_USERID, -1);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        memberRecycler.setLayoutManager(layoutManager);
        mAdapter = new CreateGroupUserRecyclerAdapter(this, addedMembers);
        memberRecycler.setAdapter(mAdapter);

        members = new ArrayList<>();
        findViewById(R.id.createGroupConfirmButton).setOnClickListener(this::createGroup_OnClick);
        findViewById(R.id.addGroupMembersButton).setOnClickListener(this::addMember_OnClick);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void addMember_OnClick(View v) {
        AddMemberDialogFragment dialog = new AddMemberDialogFragment();
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
                    members.add(u.getId());
                    addedMembers.add(u);
                    mAdapter.notifyItemInserted(addedMembers.size() - 1);
                }
            }
        });
    }

    public void createGroup_OnClick(View v) {
        List<Integer> admins = new ArrayList<>();
        admins.add(mUserId);
        try {
            Group.CreateGroup(mGroupName.getText().toString(), mGroupPassword.getText().toString(), groupIconPath, admins, members, this);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void uploadImage_OnClick(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 200);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 200) {
                Uri selectedImage = data.getData();
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (selectedImage != null) {
                    mGroupIcon.setImageURI(selectedImage);
                    groupIconPath = ResourceManager.saveToInternalStorage(bitmap, this);
                }
            }
        }
    }


}