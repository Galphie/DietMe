package com.galphie.dietme.patients;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.galphie.dietme.R;
import com.galphie.dietme.Utils;
import com.galphie.dietme.adapters.SharedFileListAdapter;
import com.galphie.dietme.instantiable.CustomFile;
import com.galphie.dietme.instantiable.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;


public class SharedFilesFragment extends Fragment implements SharedFileListAdapter.OnSharedFileClickListener {
    private static final String ARG_PARAM1 = "patient";
    private static final String ARG_PARAM2 = "patientId";

    private User patient;
    private String patientId;

    private RecyclerView recyclerView;
    private ArrayList<CustomFile> sharedFiles = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    public SharedFilesFragment() {
    }

    public static SharedFilesFragment newInstance(User patient, String patientId) {
        SharedFilesFragment fragment = new SharedFilesFragment();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shared_files, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DatabaseReference userFilesRef = database.getReference("Archivos/users").child(patientId);
        recyclerView = view.findViewById(R.id.shared_files_recycler_view);
        initRecyclerView();
        sharedFiles.clear();
        userFilesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    CustomFile customFile = new CustomFile(ds.getValue(CustomFile.class).getName(),
                            ds.getValue(CustomFile.class).getUrl());
                    sharedFiles.add(customFile);
                    Collections.sort(sharedFiles, (o1, o2) -> o1.getName().compareTo(o2.getName()));
                }
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SharedFileListAdapter adapter = new SharedFileListAdapter(sharedFiles, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSharedFileClick(int position) {
        String name = sharedFiles.get(position).getName();
        String[] parts = name.split("\\.");
        String fileName = parts[0];
        String fileExtension = "." + parts[1];
        String fileUrl = sharedFiles.get(position).getUrl();
        Utils.downloadFileFromUrl(getActivity().getApplicationContext(),
                fileName,
                fileExtension,
                Environment.DIRECTORY_DOWNLOADS,
                fileUrl);

        Utils.toast(getActivity().getApplicationContext(), "Descargando...");
    }
}
