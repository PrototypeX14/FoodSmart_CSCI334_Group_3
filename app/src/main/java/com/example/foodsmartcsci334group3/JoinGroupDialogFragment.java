package com.example.foodsmartcsci334group3;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class JoinGroupDialogFragment extends DialogFragment {

    public interface JoinGroupDialogResult {
        void onDialogFinish(String resultName, String resultPassword);
    }

    private JoinGroupDialogResult mJoinGroupDialogResult;
    private EditText mGroupName;
    private EditText mGroupPassword;
    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();
        mGroupName = requireActivity().findViewById(R.id.joinGroupNameInput);
        mGroupPassword = requireActivity().findViewById(R.id.joinGroupPasswordInput);
        builder.setView(layoutInflater.inflate(R.layout.join_group_dialog, null))
                .setPositiveButton(R.string.join, ((dialog, which) -> Objects.requireNonNull(JoinGroupDialogFragment.this.getDialog()).dismiss()))
                .setNegativeButton(R.string.cancel, (dialog, which) -> Objects.requireNonNull(JoinGroupDialogFragment.this.getDialog()).cancel());
        return builder.create();
    }

    public void setJoinGroupDialogResult(JoinGroupDialogResult dialogResult) {
        mJoinGroupDialogResult = dialogResult;
    }
}
