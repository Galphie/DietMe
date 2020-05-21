package com.galphie.dietme.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.galphie.dietme.MainActivity;
import com.galphie.dietme.R;
import com.galphie.dietme.User;
import com.galphie.dietme.Utils;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PasswordFragment extends Fragment implements TextWatcher {
    private static final String CHANGE = "param1";
    private static final String USER = "param2";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersRef = database.getReference("Usuario");

    private User currentUser;
    private TextView oldPassText, newPassText, repeatPassText, textIsStrong;
    private EditText oldPassInput, newPassInput, repeatPassInput;
    private Button updateButton;
    private CheckBox checkShowChange;

    public PasswordFragment() {
    }

    public static PasswordFragment newInstance(boolean param1, User param2) {
        PasswordFragment fragment = new PasswordFragment();
        Bundle args = new Bundle();
        args.putBoolean(CHANGE, param1);
        args.putParcelable(USER, param2);
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
            currentUser = getArguments().getParcelable(USER);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_password, container, false);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String userID = preferences.getString("UserID", "");
        oldPassInput = (EditText) view.findViewById(R.id.oldPassInput);
        newPassInput = (EditText) view.findViewById(R.id.newPassInput);
        repeatPassInput = (EditText) view.findViewById(R.id.repeatPassInput);

        newPassInput.addTextChangedListener(this);

        textIsStrong = (TextView) view.findViewById(R.id.textIsStrong);
        oldPassText = (TextView) view.findViewById(R.id.oldPassText);
        newPassText = (TextView) view.findViewById(R.id.newPassText);
        repeatPassText = (TextView) view.findViewById(R.id.repeatPassText);

        checkShowChange = (CheckBox) view.findViewById(R.id.checkShowChange);
        checkShowChange.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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
        });
        if (getArguments().getBoolean(CHANGE)) {
            oldPassInput.setText(currentUser.getPassword());
            oldPassText.setVisibility(View.INVISIBLE);
            oldPassInput.setVisibility(View.INVISIBLE);
        }
        updateButton = (Button) view.findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oldPassInput.getText().toString().equals(currentUser.getPassword())
                        || Utils.SHA256(oldPassInput.getText().toString()).equals(currentUser.getPassword())) {
                    oldPassText.setTextColor(getResources().getColor(R.color.design_default_color_on_secondary));
                    if (Utils.hasCompletePasswordFormat(newPassInput.getText().toString())) {
                        newPassText.setTextColor(getResources().getColor(R.color.design_default_color_on_secondary));
                        if (newPassInput.getText().toString().equals(repeatPassInput.getText().toString())) {
                            repeatPassText.setTextColor(getResources().getColor(R.color.design_default_color_on_secondary));
                            usersRef.child(userID)
                                    .child("password")
                                    .setValue(Utils.SHA256(newPassInput.getText().toString()));
                            Utils.toast(getActivity().getApplicationContext(), getString(R.string.password_changed));
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
            }
        });
        return view;
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
