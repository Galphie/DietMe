package com.galphie.dietme.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.galphie.dietme.R;
import com.galphie.dietme.instantiable.Appointment;

import java.util.ArrayList;

public class PatientAppointmentListAdapter extends RecyclerView.Adapter<PatientAppointmentListAdapter.ViewHolder> {

    private ArrayList<Appointment> appointments;
    private OnPatientAppointmentClickListener mListener;

    public PatientAppointmentListAdapter(ArrayList<Appointment> appointments, OnPatientAppointmentClickListener mListener) {
        this.mListener = mListener;
        this.appointments = appointments;
    }

    @NonNull
    @Override
    public PatientAppointmentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_appointment_list_item, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientAppointmentListAdapter.ViewHolder holder, int position) {
        holder.time.setText(appointments.get(position).getTime());
        holder.date.setText(setDisplayDate(appointments.get(position).getDate()));

    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView date, time;
        private ImageButton optionsButton;

        ViewHolder(@NonNull View itemView, OnPatientAppointmentClickListener mListener) {
            super(itemView);

            date = itemView.findViewById(R.id.patient_appointment_date);
            time = itemView.findViewById(R.id.patient_appointment_time);
            optionsButton = itemView.findViewById(R.id.patient_appointment_options_button);

            optionsButton.setOnClickListener(v -> mListener.onPatientAppointmentClick(optionsButton, getAdapterPosition()));
        }
    }

    public interface OnPatientAppointmentClickListener {
        void onPatientAppointmentClick(View view, int position);
    }

    public static String setDisplayDate(String date) {
        String[] parts = date.split("-");
        String displayDate = parts[2] + " de ";
        switch (Integer.parseInt(parts[1])) {
            case 1:
                displayDate = displayDate + "enero, ";
                break;
            case 2:
                displayDate = displayDate + "febrero, ";
                break;
            case 3:
                displayDate = displayDate + "marzo, ";
                break;
            case 4:
                displayDate = displayDate + "abril, ";
                break;
            case 5:
                displayDate = displayDate + "mayo, ";
                break;
            case 6:
                displayDate = displayDate + "junio, ";
                break;
            case 7:
                displayDate = displayDate + "julio, ";
                break;
            case 8:
                displayDate = displayDate + "agosto, ";
                break;
            case 9:
                displayDate = displayDate + "septiembre, ";
                break;
            case 10:
                displayDate = displayDate + "octubre, ";
                break;
            case 11:
                displayDate = displayDate + "noviembre, ";
                break;
            case 12:
                displayDate = displayDate + "diciembre, ";
                break;

        }
        displayDate = displayDate + parts[0];
        return displayDate;
    }
}
