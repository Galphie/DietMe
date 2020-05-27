package com.galphie.dietme.patients;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.galphie.dietme.R;
import com.galphie.dietme.instantiable.User;

import java.util.ArrayList;

public class PatientsListAdapter extends RecyclerView.Adapter<PatientsListAdapter.ViewHolder> {
    private ArrayList<User> patientsList;
    private OnPatientClickListener onPatientClickListener;

    public PatientsListAdapter(ArrayList<User> patientsList, OnPatientClickListener onPatientClickListener) {
        this.patientsList = patientsList;
        this.onPatientClickListener = onPatientClickListener;
    }

    @NonNull
    @Override
    public PatientsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_list_item, parent, false);
        return new ViewHolder(view, onPatientClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientsListAdapter.ViewHolder holder, int position) {
        holder.name.setText(patientsList.get(position).getForenames() + ", " + patientsList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return patientsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        ImageButton optionsButton;
        OnPatientClickListener onPatientClickListener;

        public ViewHolder(@NonNull View itemView, OnPatientClickListener onPatientClickListener) {
            super(itemView);
            name = itemView.findViewById(R.id.patient_list_item_name);
            optionsButton = itemView.findViewById(R.id.more_options_button);
            this.onPatientClickListener = onPatientClickListener;
            optionsButton.setOnClickListener(v -> onPatientClickListener.onPatientContextClick(v, getAdapterPosition()));
            itemView.setOnClickListener(v -> onPatientClickListener.onPatientClick(getAdapterPosition()));
            itemView.setOnLongClickListener(v -> {
                onPatientClickListener.onPatientContextClick(v, getAdapterPosition());
                return true;
            });

        }

        @Override
        public void onClick(View v) {
        }
    }

    public interface OnPatientClickListener {
        void onPatientClick(int position);

        void onPatientContextClick(View view, int position);
    }
}
