package com.galphie.dietme.patients;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.galphie.dietme.PdfViewerActivity;
import com.galphie.dietme.R;
import com.galphie.dietme.Utils;
import com.galphie.dietme.adapters.SharedFileListAdapter;
import com.galphie.dietme.dialog.ConfirmActionDialog;
import com.galphie.dietme.instantiable.CustomFile;
import com.galphie.dietme.instantiable.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;


public class SharedFilesFragment extends Fragment implements SharedFileListAdapter.OnSharedFileClickListener,
        ValueEventListener {

    private static final String ARG_PATIENT = "currentUser";
    private static final String ARG_PATIENT_ID = "patientId";

    private User currentUser;
    private String patientId;

    private RecyclerView recyclerView;
    private TextView noSharedFilesText;
    private ArrayList<CustomFile> sharedFiles = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    public SharedFilesFragment() {
    }

    static SharedFilesFragment newInstance(User currentUser, String patientId) {
        SharedFilesFragment fragment = new SharedFilesFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PATIENT, currentUser);
        args.putString(ARG_PATIENT_ID, patientId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUser = getArguments().getParcelable(ARG_PATIENT);
            patientId = getArguments().getString(ARG_PATIENT_ID);
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

        recyclerView = view.findViewById(R.id.shared_files_recycler_view);
        noSharedFilesText = view.findViewById(R.id.no_shared_files_text);
        initRecyclerView();

        DatabaseReference userFilesRef = database.getReference("Archivos/users").child(patientId);
        userFilesRef.addValueEventListener(this);
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SharedFileListAdapter adapter = new SharedFileListAdapter(sharedFiles, this);
        recyclerView.setAdapter(adapter);
        sharedFiles.clear();
    }

    @Override
    public void onSharedFileDownloadClick(int position) {
        String name = sharedFiles.get(position).getName();
        String fileUrl = sharedFiles.get(position).getUrl();
        downloadSharedFile(name, fileUrl);
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

    @Override
    public void onSharedFileItemClick(int position) {
        String fileUrl = sharedFiles.get(position).getUrl();
        viewPdfInViewer(fileUrl);
    }

    @Override
    public void onSharedFileItemLongClick(int position) {
        if (currentUser.isAdmin()) {
            CustomFile fileToDelete = sharedFiles.get(position);
            DialogFragment dialogFragment = new ConfirmActionDialog();
            Bundle bundle = new Bundle();
            bundle.putString("confirm_action_dialog_message", "¿Eliminar " + fileToDelete.getName() + "?");
            bundle.putInt("type", ConfirmActionDialog.DELETE_FILE_CODE);
            bundle.putString("patientId", patientId);
            bundle.putString("fileName", fileToDelete.getName());
            bundle.putString("path", fileToDelete.getPath());
            dialogFragment.setArguments(bundle);
            dialogFragment.show(getParentFragmentManager(), "Delete file");
        }
    }


    private void viewPdfInViewer(String fileUrl) {
        Intent intent = new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), PdfViewerActivity.class);
        intent.putExtra("ViewType", "external");
        intent.putExtra("Url", fileUrl);
        startActivity(intent);
    }

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
}
