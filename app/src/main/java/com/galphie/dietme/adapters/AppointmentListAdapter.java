package com.galphie.dietme.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.galphie.dietme.R;
import com.galphie.dietme.instantiable.Signing;

import java.util.ArrayList;

public class AppointmentListAdapter extends RecyclerView.Adapter<AppointmentListAdapter.ViewHolder> {

    private ArrayList<Signing> signings;

    public AppointmentListAdapter(ArrayList<Signing> appointments) {
        this.signings = appointments;
    }

    @NonNull
    @Override
    public AppointmentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_list_item, parent, false);
        return new AppointmentListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentListAdapter.ViewHolder holder, int position) {
        holder.time.setText(signings.get(position).getAppointment().getTime());
        holder.name.setText(signings.get(position).getUser().getName()
                + " " + signings.get(position).getUser().getForenames());

    }

    @Override
    public int getItemCount() {
        return signings.size();
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
