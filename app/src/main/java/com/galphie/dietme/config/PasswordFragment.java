package com.galphie.dietme.config;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.galphie.dietme.R;
import com.galphie.dietme.Utils;
import com.galphie.dietme.instantiable.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class PasswordFragment extends Fragment implements TextWatcher {
    private static final String ACCESS_REQUESTED = "accessRequested";
    private static final String CURRENT_USER = "currentUser";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef = database.getReference("Usuario");

    private User currentUser;
    private TextView oldPassText, newPassText, repeatPassText, textIsStrong;
    private EditText oldPassInput, newPassInput, repeatPassInput;

    public PasswordFragment() {
    }

    public static PasswordFragment newInstance(boolean accessRequested, User currentUser) {
        PasswordFragment fragment = new PasswordFragment();
        Bundle args = new Bundle();
        args.putBoolean(ACCESS_REQUESTED, accessRequested);
        args.putParcelable(CURRENT_USER, currentUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUser = getArguments().getParcelable(CURRENT_USER);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_password, container, false);

        String userId = Objects.requireNonNull(Utils.MD5(currentUser.getEmail())).substring(0, 6).toUpperCase();
        oldPassInput = view.findViewById(R.id.oldPassInput);
        newPassInput = view.findViewById(R.id.newPassInput);
        repeatPassInput = view.findViewById(R.id.repeatPassInput);

        newPassInput.addTextChangedListener(this);

        textIsStrong = view.findViewById(R.id.textIsStrong);
        oldPassText = view.findViewById(R.id.oldPassText);
        newPassText = view.findViewById(R.id.newPassText);
        repeatPassText = view.findViewById(R.id.repeatPassText);

        assert getArguments() != null;
        if (getArguments().getBoolean(ACCESS_REQUESTED)) {
            oldPassInput.setText(currentUser.getPassword());
            oldPassText.setVisibility(View.INVISIBLE);
            oldPassInput.setVisibility(View.INVISIBLE);
        }

        CheckBox checkShowChange = view.findViewById(R.id.checkShowChange);
        checkShowChange.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showPassword(isChecked);
        });

        Button updateButton = view.findViewById(R.id.updateButton);
        updateButton.setOnClickListener(v -> {
            if (oldPassInput.getText().toString().equals(currentUser.getPassword())
                    || Objects.equals(Utils.SHA256(oldPassInput.getText().toString()), currentUser.getPassword())) {
                oldPassText.setTextColor(getResources().getColor(R.color.design_default_color_on_secondary));
                if (Utils.hasCompletePasswordFormat(newPassInput.getText().toString())) {
                    newPassText.setTextColor(getResources().getColor(R.color.design_default_color_on_secondary));
                    if (newPassInput.getText().toString().equals(repeatPassInput.getText().toString())) {
                        repeatPassText.setTextColor(getResources().getColor(R.color.design_default_color_on_secondary));
                        usersRef.child(userId)
                                .child("password")
                                .setValue(Utils.SHA256(newPassInput.getText().toString()));
                        Utils.toast(Objects.requireNonNull(getActivity()).getApplicationContext(), getString(R.string.password_changed));
                        getActivity().finish();
                    } else {
                        repeatPassText.setTextColor(getResources().getColor(R.color.design_default_color_error));
                        Snackbar.make(view, getString(R.string.invalid_repeat_password), Snackbar.LENGTH_LONG)
                                .setAction("Action", null)
                                .show();
                    }
                } else {
                    newPassText.setTextColor(getResources().getColor(R.color.design_default_color_error));
                    Snackbar.make(view, getString(R.string.invalid_new_password), Snackbar.LENGTH_LONG)
                            .setAction("Action", null)
                            .show();
                }

            } else {
                oldPassText.setTextColor(getResources().getColor(R.color.design_default_color_error));
                Snackbar.make(view, getString(R.string.invalid_password), Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show();
            }
        });
        return view;
    }

    private void showPassword(boolean isChecked) {
        if (isChecked) {
            oldPassInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            newPassInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            repeatPassInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            oldPassInput.setInputType(129);
            newPassInput.setInputType(129);
            repeatPassInput.setInputType(129);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!Utils.hasCompletePasswordFormat(s.toString()) && !s.toString().equals("")) {
            textIsStrong.setVisibility(View.VISIBLE);
        } else if (Utils.hasCompletePasswordFormat(s.toString()) || s.toString().equals("")) {
            textIsStrong.setVisibility(View.GONE);
        }
    }
}
