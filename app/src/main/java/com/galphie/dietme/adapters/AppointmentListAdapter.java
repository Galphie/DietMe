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

public class AppointmentListAdapter extends RecyclerView.Adapter<AppointmentListAdapter.ViewHolder> {

    private ArrayList<Appointment> appointments;

    public AppointmentListAdapter(ArrayList<Appointment> appointments) {
        this.appointments = appointments;
    }

    @NonNull
    @Override
    public AppointmentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_list_item, parent, false);
        return new AppointmentListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentListAdapter.ViewHolder holder, int position) {
        holder.time.setText(appointments.get(position).getTime());
        holder.name.setText(appointments.get(position).getPatient().getName()
                + " " + appointments.get(position).getPatient().getForenames());

    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView time, name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.appointment_item_time);
            name = itemView.findViewById(R.id.appointment_item_patient_name);
        }
    }
}
