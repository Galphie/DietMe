package com.galphie.dietme.patients;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.galphie.dietme.R;
import com.galphie.dietme.Utils;
import com.galphie.dietme.dialog.UpdateBodyCompositionDialog;
import com.galphie.dietme.instantiable.Measures;
import com.galphie.dietme.instantiable.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class BodyCompositionFragment extends Fragment implements ValueEventListener {

    private static final String PATIENT = "Patient";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private Measures dbMeasures;
    private String patientId;

    private TextView heightText, weightText, bmiText, waistText, hipText,
            thighText, armsText, subscapularisText, supraText, bicipitalText, tricipitalText,
            absText, waistHipIndexText, leanMassText, fatMassText;


    public BodyCompositionFragment() {
    }

    static BodyCompositionFragment newInstance(User patient) {
        BodyCompositionFragment fragment = new BodyCompositionFragment();
        Bundle args = new Bundle();
        args.putParcelable(PATIENT, patient);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            User patient = getArguments().getParcelable(PATIENT);
            patientId = Objects.requireNonNull(Utils.MD5(Objects.requireNonNull(patient).getEmail())).substring(0,6).toUpperCase();

            DatabaseReference measuresRef = database.getReference("Usuario/" + patientId).child("measures");

             measuresRef.addValueEventListener(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_body_composition, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        heightText = view.findViewById(R.id.height_text);
        weightText = view.findViewById(R.id.weight_text);
        bmiText = view.findViewById(R.id.bmiText);

        waistText = view.findViewById(R.id.waistText);
        hipText = view.findViewById(R.id.hipText);
        armsText = view.findViewById(R.id.armsText);
        thighText = view.findViewById(R.id.thighsText);

        subscapularisText = view.findViewById(R.id.subscapularisText);
        supraText = view.findViewById(R.id.supraText);
        absText = view.findViewById(R.id.absText);
        bicipitalText = view.findViewById(R.id.bicipitalText);
        tricipitalText = view.findViewById(R.id.tricipitalText);
        waistHipIndexText = view.findViewById(R.id.waistHipIndexText);

        leanMassText = view.findViewById(R.id.leanMassText);
        fatMassText = view.findViewById(R.id.fatMassText);

        Button updateButton = view.findViewById(R.id.update_measures_button);
        updateButton.setOnClickListener(v -> {
            DialogFragment dialogFragment = new UpdateBodyCompositionDialog();
            Bundle args = new Bundle();
            args.putParcelable("patientMeasures", dbMeasures);
            args.putString("patientId", patientId);
            dialogFragment.setArguments(args);
            dialogFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "Update");
        });
    }

    private void init(Measures measures) {
        heightText.setText(String.format("%sm", measures.getHeight()));
        weightText.setText(String.format("%skg", measures.getWeight()));
        if (measures.getWeight() == 0 || measures.getHeight() == 0) {
            bmiText.setText(R.string.empty_text);
        } else {
            double bmi = calculateBMI(measures.getWeight(), measures.getHeight());
            bmiText.setText(String.valueOf(bmi));
        }
        waistText.setText(String.format("%scm", measures.getWaist()));
        hipText.setText(String.format("%scm", measures.getHip()));
        armsText.setText(String.format("%scm", measures.getArm()));
        thighText.setText(String.format("%scm", measures.getThigh()));

        subscapularisText.setText(String.format("%smm", measures.getSubscapularisFold()));
        supraText.setText(String.format("%smm", measures.getSuprailiacalFold()));
        absText.setText(String.format("%smm", measures.getAbdominalFold()));
        bicipitalText.setText(String.format("%smm", measures.getBicipitalFold()));
        tricipitalText.setText(String.format("%smm", measures.getTricipitalFold()));
        if (measures.getWaist() == 0 && measures.getHip() == 0) {
            waistHipIndexText.setText(R.string.empty_text);
        } else {
            double whI = calculateWaistHipIndex(measures.getWaist(), measures.getHip());
            waistHipIndexText.setText(String.valueOf(whI));
        }
        leanMassText.setText(String.format("%s%%", String.valueOf(measures.getLeanMass())));
        fatMassText.setText(String.format("%s%%", String.valueOf(measures.getFatMass())));
    }

    private static double calculateWaistHipIndex(double waist, double hip) {
        double whI = waist / hip;
        whI = Math.floor(whI * 100) / 100;
        return whI;
    }

    private static double calculateBMI(double weight, double height) {
        double bmi = (weight / Math.pow(height, 2));
        bmi = Math.floor(bmi * 100) / 100;
        return bmi;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() != null) {
            dbMeasures = dataSnapshot.getValue(Measures.class);
            init(Objects.requireNonNull(dbMeasures));
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
