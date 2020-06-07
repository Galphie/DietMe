package com.galphie.dietme.config;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.activity.OnBackPressedCallback;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.galphie.dietme.R;
import com.galphie.dietme.Utils;
import com.galphie.dietme.dialog.ConfirmActionDialog;
import com.galphie.dietme.instantiable.User;

import java.util.Objects;

public class AppointmentsManagementFragment extends Fragment {

    private static final String CURRENT_USER = "currentUser";
    public static CardView progressBar;
    private User currentUser;

    public AppointmentsManagementFragment() {
    }

    static AppointmentsManagementFragment newInstance(User currentUser) {
        AppointmentsManagementFragment fragment = new AppointmentsManagementFragment();
        Bundle args = new Bundle();
        args.putParcelable(CURRENT_USER, currentUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUser = getArguments().getParcelable(CURRENT_USER);
        }
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (progressBar.getVisibility() == View.VISIBLE) {
                    Utils.toast(getActivity().getApplicationContext(),"Reinicio en progreso...");
                } else {
                    getActivity().finish();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointments_management, container, false);

        Button restartButton = view.findViewById(R.id.restartDatabaseButton);
        progressBar = view.findViewById(R.id.progressCard);
        progressBar.setVisibility(View.INVISIBLE);

        restartButton.setOnClickListener(v -> {
            if (currentUser.isAdmin()) {
                Bundle args = new Bundle();
                args.putString("confirm_action_dialog_message", getString(R.string.restarting_conditions));
                args.putInt("type", ConfirmActionDialog.RESTART_CODE);
                DialogFragment confirmActionDialog = new ConfirmActionDialog();
                confirmActionDialog.setArguments(args);
                confirmActionDialog.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "Confirm");
            } else {
                Utils.toast(Objects.requireNonNull(getActivity()).getApplicationContext(), getString(R.string.developer_action_only));
            }

        });
        return view;
    }



}
