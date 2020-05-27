package com.galphie.dietme.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
        View view = inflater.inflate(R.layout.access_request_dialog, null);
        Bundle mArgs = getArguments();

        builder.setView(view)
                .setPositiveButton(R.string.update, (dialog, id) -> {
                    dialog.dismiss();
                    mListener.setInfo(emailInput.getText().toString(), phoneInput.getText().toString());
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss());
        emailInput = view.findViewById(R.id.emailInput);
        emailInput.setText(mArgs.getString("mail"));
        phoneInput = view.findViewById(R.id.phoneInput);
        return builder.create();
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AccessRequestDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
