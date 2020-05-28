package com.galphie.dietme.config;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.galphie.dietme.R;
import com.galphie.dietme.Utils;
import com.galphie.dietme.dialog.ConfirmActionDialog;
import com.galphie.dietme.instantiable.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AppointmentsManagementFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "User";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference appointmentsRef = database.getReference("Citas");

    private String mParam1;
    private User currentUser;

    private Button restartButton;

    public AppointmentsManagementFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static AppointmentsManagementFragment newInstance(String param1, User currentUser) {
        AppointmentsManagementFragment fragment = new AppointmentsManagementFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putParcelable(ARG_PARAM2, currentUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            currentUser = getArguments().getParcelable(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointments_management, container, false);

        restartButton = view.findViewById(R.id.restartDatabaseButton);
        restartButton.setOnClickListener(v -> {
            if (currentUser.isAdmin()) {
                Bundle args = new Bundle();
                args.putString("Message", getString(R.string.restarting_conditions));
                args.putString("Type", "Restart");
                DialogFragment confirmActionDialog = new ConfirmActionDialog();
                confirmActionDialog.setArguments(args);
                confirmActionDialog.show(getActivity().getSupportFragmentManager(), "Confirm");
            } else {
                Utils.toast(getActivity().getApplicationContext(),getString(R.string.developer_action_only));
            }
        });

        return view;
    }
}
