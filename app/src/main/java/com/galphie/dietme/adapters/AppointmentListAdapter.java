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
    private OnAppointmentClickListener mListener;

    public AppointmentListAdapter(ArrayList<Signing> appointments, OnAppointmentClickListener mListener) {
        this.signings = appointments;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public AppointmentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_list_item, parent, false);
        return new AppointmentListAdapter.ViewHolder(view, mListener);
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
        private OnAppointmentClickListener mListener;

        public ViewHolder(@NonNull View itemView, OnAppointmentClickListener mListener) {
            super(itemView);

            time = itemView.findViewById(R.id.appointment_item_time);
            name = itemView.findViewById(R.id.appointment_item_patient_name);
            this.mListener = mListener;

            itemView.setOnClickListener(v -> mListener.onAppointmentClick(getAdapterPosition()));
        }
    }

    public interface OnAppointmentClickListener {
        void onAppointmentClick (int position);
    }
}
