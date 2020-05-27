package com.galphie.dietme.patients;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.galphie.dietme.R;
import com.galphie.dietme.Utils;
import com.galphie.dietme.dialog.ConfirmActionDialog;
import com.galphie.dietme.instantiable.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class PatientsFragment extends Fragment implements PatientsListAdapter.OnPatientClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef = database.getReference("Usuario");
    private String mParam1;
    private String mParam2;
    private ArrayList<User> patientsList;
    private User currentUser;
    private RecyclerView recyclerView;
    private FloatingActionButton addPatientButton;
    private SwipeRefreshLayout swipeRefreshLayout;

    public PatientsFragment() {
    }

    // TODO: Rename and change types and number of parameters
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
            patientsList = getArguments().getParcelableArrayList("PatientsList");
            currentUser = getArguments().getParcelable("CurrentUser");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patients, container, false);

        recyclerView = view.findViewById(R.id.patients_recycler_view);
        initRecyclerView();
        addPatientButton = view.findViewById(R.id.add_patient_button);
        addPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser.isAdmin()) {
                    Intent intent = new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), NewPatientActivity.class);
                    startActivity(intent);
                } else {
                    Utils.toast(Objects.requireNonNull(getActivity()).getApplicationContext(), getString(R.string.developer_action_only));
                }

            }
        });
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initRecyclerView();
                recyclerView.getAdapter().notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return view;
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
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit_option:
                        if (currentUser.isAdmin()) {
                            Intent intent = new Intent(getActivity().getApplicationContext(), NewPatientActivity.class);
                            intent.putExtra("Edit", true);
                            intent.putExtra("Patient", patientsList.get(position));
                            startActivity(intent);
                        } else {
                            Utils.toast(getActivity().getApplicationContext(),getString(R.string.developer_action_only));
                        }
                        return true;
                    case R.id.remove_option:
                        Bundle args = new Bundle();
                        args.putString("Message", "¿Eliminar a " + patientsList.get(position).getName() + "?");
                        args.putString("Type", "Delete");
                        args.putString("Object", Utils.MD5(patientsList.get(position).getEmail()).substring(0,6));
                        DialogFragment confirmActionDialog = new ConfirmActionDialog();
                        confirmActionDialog.setArguments(args);
                        confirmActionDialog.show(getActivity().getSupportFragmentManager(),"Confirm");
                        return true;
                    default:
                        return false;
                }
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
