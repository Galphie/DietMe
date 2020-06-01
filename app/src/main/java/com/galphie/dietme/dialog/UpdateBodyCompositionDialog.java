package com.galphie.dietme.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.galphie.dietme.R;
import com.galphie.dietme.Utils;
import com.galphie.dietme.instantiable.Measures;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.util.Objects;

public class UpdateBodyCompositionDialog extends DialogFragment {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef = database.getReference("Usuario");
    private DatabaseReference registersRef = database.getReference("Registros");
    private Measures patientMeasures;
    private double height; //m
    private double weight, leanMass, fatMass; //kg
    private double waist, hip, thigh, arm; //cm
    private double subscapularisFold, abdominalFold, suprailiacalFold, tricipitalFold, bicipitalFold; //mm
    private EditText editWaist, editHip, editArms, editThighs, editSub,
            editAbs, editSupra, editBici, editTrici, editLean, editFat, editHeight, editWeight;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.update_body_composition_dialog, null);

        patientMeasures = Objects.requireNonNull(getArguments()).getParcelable("patientMeasures");
        String patientId = getArguments().getString("patientId");
        DatabaseReference patientMeasuresRef = usersRef.child(patientId + "/measures");
        DatabaseReference registeredMeasuresRef = registersRef.child(Objects.requireNonNull(patientId));

        editWaist = view.findViewById(R.id.editWaist);
        editHip = view.findViewById(R.id.editHip);
        editArms = view.findViewById(R.id.editArms);
        editThighs = view.findViewById(R.id.editThighs);
        editSub = view.findViewById(R.id.editSubs);
        editAbs = view.findViewById(R.id.editAbs);
        editSupra = view.findViewById(R.id.editSupra);
        editBici = view.findViewById(R.id.editBici);
        editTrici = view.findViewById(R.id.editTrici);
        editLean = view.findViewById(R.id.editLean);
        editFat = view.findViewById(R.id.editFat);
        editHeight = view.findViewById(R.id.editHeight);
        editWeight = view.findViewById(R.id.editWeight);


        builder.setView(view)
                .setPositiveButton(R.string.send, (dialog, id) -> {
                    checkData();
                    Measures measures = new Measures(height,
                            weight,
                            leanMass,
                            fatMass,
                            waist,
                            hip,
                            thigh,
                            arm,
                            subscapularisFold,
                            abdominalFold,
                            suprailiacalFold,
                            tricipitalFold,
                            bicipitalFold);


                    patientMeasuresRef.setValue(measures);
                    registeredMeasuresRef.child(LocalDate.now().toString()).setValue(measures);
                    Utils.toast(getActivity().getApplicationContext(), getString(R.string.updated));
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss());

        return builder.create();

    }

    private void checkData() {
        if (editHeight.getText().toString().equals("")) {
            height = patientMeasures.getHeight();
        } else {
            height = Double.parseDouble(editHeight.getText().toString());
        }
        if (editWeight.getText().toString().equals("")) {
            weight = patientMeasures.getWeight();
        } else {
            weight = Double.parseDouble(editWeight.getText().toString());
        }
        if (editWaist.getText().toString().equals("")) {
            waist = patientMeasures.getWaist();
        } else {
            waist = Double.parseDouble(editWaist.getText().toString());
        }
        if (editHip.getText().toString().equals("")) {
            hip = patientMeasures.getHip();
        } else {
            hip = Double.parseDouble(editHip.getText().toString());
        }
        if (editArms.getText().toString().equals("")) {
            arm = patientMeasures.getArm();
        } else {
            arm = Double.parseDouble(editArms.getText().toString());
        }
        if (editThighs.getText().toString().equals("")) {
            thigh = patientMeasures.getThigh();
        } else {
            thigh = Double.parseDouble(editThighs.getText().toString());
        }
        if (editAbs.getText().toString().equals("")) {
            abdominalFold = patientMeasures.getAbdominalFold();
        } else {
            abdominalFold = Double.parseDouble(editAbs.getText().toString());
        }
        if (editSub.getText().toString().equals("")) {
            subscapularisFold = patientMeasures.getSubscapularisFold();
        } else {
            subscapularisFold = Double.parseDouble(editSub.getText().toString());
        }
        if (editSupra.getText().toString().equals("")) {
            suprailiacalFold = patientMeasures.getSuprailiacalFold();
        } else {
            suprailiacalFold = Double.parseDouble(editSupra.getText().toString());
        }
        if (editBici.getText().toString().equals("")) {
            bicipitalFold = patientMeasures.getBicipitalFold();
        } else {
            bicipitalFold = Double.parseDouble(editBici.getText().toString());
        }
        if (editTrici.getText().toString().equals("")) {
            tricipitalFold = patientMeasures.getTricipitalFold();
        } else {
            tricipitalFold = Double.parseDouble(editTrici.getText().toString());
        }
        if (editLean.getText().toString().equals("")) {
            leanMass = patientMeasures.getLeanMass();
        } else {
            leanMass = Double.parseDouble(editLean.getText().toString());
        }
        if (editFat.getText().toString().equals("")) {
            fatMass = patientMeasures.getFatMass();
        } else {
            fatMass = Double.parseDouble(editFat.getText().toString());
        }
    }
}
