package com.galphie.dietme.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.galphie.dietme.R;
import com.galphie.dietme.instantiable.Appointment;

import java.util.ArrayList;

public class PatientAppointmentListAdapter extends RecyclerView.Adapter<PatientAppointmentListAdapter.ViewHolder> {

    private ArrayList<Appointment> appointments;

    public PatientAppointmentListAdapter(ArrayList<Appointment> appointments) {
        this.appointments = appointments;
    }

    @NonNull
    @Override
    public PatientAppointmentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_appointment_list_item, parent, false);
        return new PatientAppointmentListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientAppointmentListAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
