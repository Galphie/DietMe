package com.galphie.dietme.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.galphie.dietme.R;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AccessRequestDialog extends DialogFragment {

    private AccessRequestDialogListener mListener;
    private EditText emailInput, phoneInput;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.access_request_dialog, null);
        Bundle mArgs = getArguments();

        builder.setView(view)
                .setPositiveButton(R.string.request_access, (dialog, id) -> {
                    dialog.dismiss();
                    mListener.setInfo(emailInput.getText().toString(), phoneInput.getText().toString());
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss());
        emailInput = view.findViewById(R.id.emailInput);
        emailInput.setText(Objects.requireNonNull(mArgs).getString("mail"));
        phoneInput = view.findViewById(R.id.phoneInput);
        return builder.create();
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        try {
            mListener = (AccessRequestDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public interface AccessRequestDialogListener {
        void setInfo(String email, String phone);
    }
}
