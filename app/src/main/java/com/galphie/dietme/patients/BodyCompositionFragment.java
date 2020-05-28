package com.galphie.dietme.patients;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.galphie.dietme.R;
import com.galphie.dietme.dialog.UpdateBodyCompositionDialog;
import com.galphie.dietme.instantiable.Measures;
import com.galphie.dietme.instantiable.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.ParseException;


public class BodyCompositionFragment extends Fragment {

    private static final String ARG_PARAM1 = "Patient";
    private static final String ARG_PARAM2 = "PatientID";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef = database.getReference("Usuario");
    private User patient;
    private Measures patientMeasures, dbMeasures;
    private String patientId;

    private Button updateButton;
    private TextView heightText, weightText, bmiText, waistText, hipText,
            thighText, armsText, subscapularisText, supraText, bicipitalText, tricipitalText,
            absText, waistHipIndexText, leanMassText, fatMassText;


    public BodyCompositionFragment() {
    }

    static BodyCompositionFragment newInstance(User patient, String patientId) {
        BodyCompositionFragment fragment = new BodyCompositionFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, patient);
        args.putString(ARG_PARAM2, patientId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            patient = getArguments().getParcelable(ARG_PARAM1);
            patientId = getArguments().getString(ARG_PARAM2);

            patientMeasures = patient.getMeasures();
            DatabaseReference patientRef = database.getReference("Usuario/" + patientId);

            patientRef.child("measures").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dbMeasures = dataSnapshot.getValue(Measures.class);
                    init(dbMeasures);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_body_composition, container, false);

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

        updateButton = view.findViewById(R.id.update_measures_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new UpdateBodyCompositionDialog();
                Bundle args = new Bundle();
                args.putParcelable("Patient", patientMeasures);
                args.putString("PatientId", patientId);
                dialogFragment.setArguments(args);
                dialogFragment.show(getActivity().getSupportFragmentManager(), "Update");
            }
        });

        return view;
    }

    private void init(Measures measures) {
        heightText.setText(String.format("%sm", measures.getHeight()));
        weightText.setText(String.format("%skg", measures.getWeight()));
        if (patientMeasures.getWeight() == 0 && measures.getHeight() == 0) {
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
        DecimalFormat f = new DecimalFormat("0.00");
        double whI = (double) waist / hip;
        try {
            whI = (double) f.parse(f.format(whI));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return whI;
    }

    private static double calculateBMI(double weight, double height) {
        DecimalFormat f = new DecimalFormat("0.00");
        double bmi = (double) (weight / Math.pow(height, 2));
        try {
            bmi = (double) f.parse(f.format(bmi));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return bmi;
    }
}
