package com.example.foodsmartcsci334group3;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class AddMemberDialogFragment extends DialogFragment {

    public interface AddMemberDialogResult {
        void onDialogFinish(String result);
    }

    AddMemberDialogResult mAddMemberDialogResult;
    EditText memberUsername;

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();
        memberUsername = requireActivity().findViewById(R.id.groupMemberUsernameInput);
        builder.setView(layoutInflater.inflate(R.layout.add_member_dialog, null))
                .setPositiveButton(R.string.add, (dialog, which) -> Objects.requireNonNull(AddMemberDialogFragment.this.getDialog()).dismiss())
                .setNegativeButton(R.string.cancel, (dialog, which) -> Objects.requireNonNull(AddMemberDialogFragment.this.getDialog()).cancel());
        return builder.create();
    }

    public void setDialogResult(AddMemberDialogResult dialogResult) {
        mAddMemberDialogResult = dialogResult;
    }
}
