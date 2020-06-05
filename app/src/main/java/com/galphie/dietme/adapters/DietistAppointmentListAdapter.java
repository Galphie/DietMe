package com.galphie.dietme.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.galphie.dietme.R;
import com.galphie.dietme.instantiable.Appointment;

import java.util.ArrayList;

public class DietistAppointmentListAdapter extends RecyclerView.Adapter<DietistAppointmentListAdapter.ViewHolder> {

    private ArrayList<Appointment> appointments;

    public DietistAppointmentListAdapter(ArrayList<Appointment> appointments) {
        this.appointments = appointments;
    }

    @NonNull
    @Override
    public DietistAppointmentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dietist_appointment_list_item, parent, false);
        return new DietistAppointmentListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DietistAppointmentListAdapter.ViewHolder holder, int position) {
        holder.time.setText(appointments.get(position).getTime());
        holder.date.setText(PatientAppointmentListAdapter.setDisplayDate(appointments.get(position).getDate()));
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView date, time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.dietist_date);
            time = itemView.findViewById(R.id.dietist_time);
        }
    }
}
