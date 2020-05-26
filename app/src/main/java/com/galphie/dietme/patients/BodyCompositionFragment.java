package com.galphie.dietme.patients;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.galphie.dietme.R;
import com.galphie.dietme.Utils;
import com.galphie.dietme.instantiable.Measures;
import com.galphie.dietme.instantiable.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.ParseException;


public class BodyCompositionFragment extends Fragment {

    private static final String ARG_PARAM1 = "Patient";
    private static final String ARG_PARAM2 = "PatientID";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersRef = database.getReference("Usuario");
    private User patient;
    private Measures patientMeasures;
    private String patientId;

    private Button updateButton;
    private TextView heightText, weightText, bmiText, waistText, hipText,
            thighText, armsText, subscapularisText, supraText, bicipitalText, tricipitalText,
            absText, waistHipIndexText, leanMassText, fatMassText;


    public BodyCompositionFragment() {
        // Required empty public constructor
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
                Utils.toast(getActivity().getApplicationContext(),"Actualizando...");
            }
        });
        init(view);

        return view;
    }

    private void init(View view) {
        heightText.setText(patientMeasures.getHeight() + "m");
        weightText.setText(patientMeasures.getWeight() + "kg");
        double bmi = calculateBMI(patientMeasures.getWeight(), patientMeasures.getHeight());
        if (patientMeasures.getWeight() == 0 && patientMeasures.getHeight() == 0) {
            bmiText.setText(R.string.empty_text);
        } else {
            bmiText.setText(String.valueOf(bmi));
        }

        waistText.setText(patientMeasures.getWaist() + "cm");
        hipText.setText(patientMeasures.getHip() + "cm");
        armsText.setText(patientMeasures.getArm() + "cm");
        thighText.setText(patientMeasures.getThigh() + "cm");

        subscapularisText.setText(patientMeasures.getSubscapularisFold() + "mm");
        supraText.setText(patientMeasures.getSuprailiacalFold() + "mm");
        absText.setText(patientMeasures.getAbdominalFold() + "mm");
        bicipitalText.setText(patientMeasures.getBicipitalFold() + "mm");
        tricipitalText.setText(patientMeasures.getTricipitalFold() + "mm");
        double whI = calculateWaistHipIndex(patientMeasures.getWaist(), patientMeasures.getHip());
        if (patientMeasures.getWaist() == 0 && patientMeasures.getHip() == 0) {
            waistHipIndexText.setText(R.string.empty_text);
        } else {
            waistHipIndexText.setText(String.valueOf(whI));
        }

        leanMassText.setText(String.valueOf(patientMeasures.getLeanMass()) + "%");
        fatMassText.setText(String.valueOf(patientMeasures.getFatMass()) + "%");
    }

    public static double calculateWaistHipIndex(double waist, double hip) {
        DecimalFormat f = new DecimalFormat("0.00");
        double whI = (double) waist / hip;
        try {
            whI = (double) f.parse(f.format(whI));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return whI;
    }

    public static double calculateBMI(double weight, double height) {
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
