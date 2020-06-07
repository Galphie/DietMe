package com.galphie.dietme.patient_only;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.galphie.dietme.PdfViewerActivity;
import com.galphie.dietme.R;
import com.galphie.dietme.Utils;
import com.galphie.dietme.adapters.SharedFileListAdapter;
import com.galphie.dietme.instantiable.CustomFile;
import com.galphie.dietme.instantiable.Measures;
import com.galphie.dietme.instantiable.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class BasicInfoProfileFragment extends Fragment implements ValueEventListener, SharedFileListAdapter.OnSharedFileClickListener {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final String PATIENT_ID = "patientId";
    private static final String PATIENT = "patient";

    private String patientId;
    private User patient;
    private Measures dbMeasures;
    private ArrayList<CustomFile> sharedFiles = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView noSharedFilesText;

    private TextView height, weight, hip, waist, arms, thighs;

    public BasicInfoProfileFragment() {
    }

    public static BasicInfoProfileFragment newInstance(String patientId, User patient) {
        BasicInfoProfileFragment fragment = new BasicInfoProfileFragment();
        Bundle args = new Bundle();
        args.putString(PATIENT_ID, patientId);
        args.putParcelable(PATIENT, patient);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            patientId = getArguments().getString(PATIENT_ID);
            patient = getArguments().getParcelable(PATIENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_basic_info_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DatabaseReference measuresReference = database.getReference().child("Usuario/"+patientId+"/measures");
        DatabaseReference filesReference = database.getReference().child("Archivos/users/" + patientId);

        measuresReference.addValueEventListener(this);
        filesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    noSharedFilesText.setVisibility(View.VISIBLE);
                    sharedFiles.clear();
                    Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
                } else {
                    sharedFiles.clear();
                    noSharedFilesText.setVisibility(View.INVISIBLE);
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        CustomFile customFile = ds.getValue(CustomFile.class);
                        sharedFiles.add(customFile);
                        Collections.sort(sharedFiles, (o1, o2) -> o1.getName().compareTo(o2.getName()));
                    }
                    Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recyclerView = view.findViewById(R.id.profile_files_recycler_view);
        noSharedFilesText = view.findViewById(R.id.profile_no_share_files);
        height = view.findViewById(R.id.profile_height_text);
        weight = view.findViewById(R.id.profile_weight_text);
        hip = view.findViewById(R.id.profile_hip_text);
        waist = view.findViewById(R.id.profile_waist_text);
        arms = view.findViewById(R.id.profile_arms_text);
        thighs = view.findViewById(R.id.profile_thigs_text);

        initRecyclerView();
    }

    private void init(Measures measures) {
        height.setText(String.format("%sm", measures.getHeight()));
        weight.setText(String.format("%skg", measures.getWeight()));
        waist.setText(String.format("%scm", measures.getWaist()));
        hip.setText(String.format("%scm", measures.getHip()));
        arms.setText(String.format("%scm", measures.getArm()));
        thighs.setText(String.format("%scm", measures.getThigh()));
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SharedFileListAdapter adapter = new SharedFileListAdapter(sharedFiles, this);
        recyclerView.setAdapter(adapter);
        sharedFiles.clear();
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

    @Override
    public void onSharedFileDownloadClick(int position) {
        String name = sharedFiles.get(position).getName();
        String fileUrl = sharedFiles.get(position).getUrl();
        downloadSharedFile(name, fileUrl);
    }

    @Override
    public void onSharedFileItemClick(int position) {
        String fileUrl = sharedFiles.get(position).getUrl();
        viewPdfInViewer(fileUrl);
    }

    @Override
    public void onSharedFileItemLongClick(int position) {

    }

    private void viewPdfInViewer(String fileUrl) {
        Intent intent = new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), PdfViewerActivity.class);
        intent.putExtra("ViewType", "external");
        intent.putExtra("Url", fileUrl);
        startActivity(intent);
    }

    private void downloadSharedFile(String name, String fileUrl) {
        String[] parts = name.split("\\.");
        String fileName = parts[0];
        String fileExtension = ".".concat(parts[1]);
        Utils.downloadFileFromUrl(Objects.requireNonNull(getActivity()).getApplicationContext(),
                fileName,
                fileExtension,
                Environment.DIRECTORY_DOWNLOADS,
                fileUrl);

        Utils.toast(getActivity().getApplicationContext(), getString(R.string.downloading));
    }
}