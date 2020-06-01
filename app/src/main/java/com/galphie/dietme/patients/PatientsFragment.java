package com.galphie.dietme.patients;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.galphie.dietme.R;
import com.galphie.dietme.Utils;
import com.galphie.dietme.adapters.PatientsListAdapter;
import com.galphie.dietme.dialog.ConfirmActionDialog;
import com.galphie.dietme.instantiable.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class PatientsFragment extends Fragment implements PatientsListAdapter.OnPatientClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef = database.getReference("Usuario");
    private ArrayList<User> patientsList = new ArrayList<>();
    private User currentUser;
    private RecyclerView recyclerView;
    private FloatingActionButton addPatientButton;
    private SwipeRefreshLayout swipeRefreshLayout;

    public PatientsFragment() {
    }

    public static PatientsFragment newInstance(String param1, String param2) {
        PatientsFragment fragment = new PatientsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUser = getArguments().getParcelable("currentUser");
        }
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Bundle homeBd = new Bundle();
                homeBd.putParcelable("currentUser", currentUser);
                Navigation
                        .findNavController(getActivity(), R.id.nav_host_fragment)
                        .navigate(R.id.homeFragment, homeBd);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_patients, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.patients_recycler_view);
        initRecyclerView();
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                patientsList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    patientsList.add(user);
                }
                Collections.sort(patientsList, (o1, o2) ->
                        o1.getForenames().compareToIgnoreCase(o2.getForenames()));
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
            }
        });
        addPatientButton = view.findViewById(R.id.add_patient_button);
        addPatientButton.setOnClickListener(v -> {
            if (currentUser.isAdmin()) {
                Intent intent = new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), NewPatientActivity.class);
                intent.putExtra("Edit", false);
                startActivity(intent);
            } else {
                Utils.toast(Objects.requireNonNull(getActivity()).getApplicationContext(), getString(R.string.developer_action_only));
            }

        });
        swipeRefreshLayout = view.findViewById(R.id.patients_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            initRecyclerView();
            recyclerView.getAdapter().notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        PatientsListAdapter adapter = new PatientsListAdapter(patientsList, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onPatientClick(int position) {
        String patientId = Objects.requireNonNull(Utils.MD5(patientsList.get(position).getEmail())).substring(0, 6).toUpperCase();
        Intent intent = new Intent(getContext(), PatientInfoActivity.class);
        intent.putExtra("CurrentUser", currentUser);
        intent.putExtra("PatientID", patientId);
        intent.putExtra("Patient", patientsList.get(position));
        startActivity(intent);
    }

    @Override
    public void onPatientContextClick(View view, int position) {
        showPopUp(view, position);
    }

    private void showPopUp(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.inflate(R.menu.patient_contextual_menu);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.edit_option:
                    if (currentUser.isAdmin()) {
                        Intent intent = new Intent(getActivity().getApplicationContext(), NewPatientActivity.class);
                        intent.putExtra("Edit", true);
                        intent.putExtra("Patient", patientsList.get(position));
                        startActivity(intent);
                    } else {
                        Utils.toast(getActivity().getApplicationContext(), getString(R.string.developer_action_only));
                    }
                    return true;
                case R.id.remove_option:
                    if (currentUser.isAdmin()) {
                        Bundle args = new Bundle();
                        args.putString("confirm_action_dialog_message", "¿Eliminar a " + patientsList.get(position).getName() + "?");
                        args.putInt("type", ConfirmActionDialog.DELETE_PATIENT_CODE);
                        args.putString("object", Utils.MD5(patientsList.get(position).getEmail()).substring(0, 6));
                        DialogFragment confirmActionDialog = new ConfirmActionDialog();
                        confirmActionDialog.setArguments(args);
                        confirmActionDialog.show(getActivity().getSupportFragmentManager(), "Confirm");
                    } else {
                        Utils.toast(getActivity().getApplicationContext(), getString(R.string.developer_action_only));
                    }
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.show();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        initRecyclerView();
    }
}
