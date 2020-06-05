package com.galphie.dietme.patient_only;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.galphie.dietme.R;
import com.galphie.dietme.adapters.PatientAppointmentListAdapter;
import com.galphie.dietme.appointment.NewAppointmentActivity;
import com.galphie.dietme.dialog.ConfirmActionDialog;
import com.galphie.dietme.instantiable.Appointment;
import com.galphie.dietme.instantiable.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class PatientMainAppointment extends Fragment implements ValueEventListener, PatientAppointmentListAdapter.OnPatientAppointmentClickListener {
    private String patientId;
    private User patient;
    private ArrayList<Appointment> appointments = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private RecyclerView recyclerView;
    private TextView noAppointmentsText;
    private ImageView arrow;
    private User currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUser = getArguments().getParcelable("currentUser");
            patient = getArguments().getParcelable("patient");
            patientId = getArguments().getString("patientId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_patient_main_appointment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DatabaseReference patientAppointmentsReference = database.getReference().child("Citas/users").child(patientId);

        recyclerView = view.findViewById(R.id.patient_main_appointments_recycler_view);
        noAppointmentsText = view.findViewById(R.id.no_patient_main_appointments);
        FloatingActionButton addAppointment = view.findViewById(R.id.add_patient_main_appointment);
        arrow = view.findViewById(R.id.arrow);

        addAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NewAppointmentActivity.class);
            intent.putExtra("patient", patient);
            startActivity(intent);
        });

        initRecyclerView();
        patientAppointmentsReference.addValueEventListener(this);
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        PatientAppointmentListAdapter adapter = new PatientAppointmentListAdapter(appointments, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        appointments.clear();
        if (dataSnapshot.hasChildren()) {
            noAppointmentsText.setVisibility(View.GONE);
            arrow.setVisibility(View.GONE);
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                Appointment appointment = ds.getValue(Appointment.class);
                appointments.add(appointment);
            }
            Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
        } else {
            noAppointmentsText.setVisibility(View.VISIBLE);
            arrow.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    @Override
    public void onPatientAppointmentClick(View view, int position) {
        showPopUp(view, position);
    }

    private void showPopUp(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.inflate(R.menu.patient_contextual_menu);
        popupMenu.setOnMenuItemClickListener(item -> onPopUpItemSelected(position, item));
        popupMenu.show();
    }

    private boolean onPopUpItemSelected(int position, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_option:
                Intent intent = new Intent(getActivity(), NewAppointmentActivity.class);
                intent.putExtra("edit", true);
                intent.putExtra("patient", patient);
                intent.putExtra("appointmentToEdit", appointments.get(position));
                startActivity(intent);
                break;
            case R.id.remove_option:
                DialogFragment dialogFragment = new ConfirmActionDialog();
                Bundle confirmActionBundle = new Bundle();
                confirmActionBundle.putString("confirm_action_dialog_message", "¿Eliminar cita del día " +
                        PatientAppointmentListAdapter.setDisplayDate(appointments.get(position).getDate()) + ", a las " + appointments.get(position).getTime() + "?");
                confirmActionBundle.putInt("type", ConfirmActionDialog.DELETE_APPOINTMENT_CODE);
                confirmActionBundle.putParcelable("object", appointments.get(position));
                confirmActionBundle.putString("patientId", patientId);
                dialogFragment.setArguments(confirmActionBundle);
                dialogFragment.show(getActivity().getSupportFragmentManager(), "Confirm");
                break;
        }
        return true;
    }
}
