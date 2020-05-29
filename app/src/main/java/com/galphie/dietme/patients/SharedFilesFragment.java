package com.galphie.dietme.patients;

import android.net.Uri;
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
import com.galphie.dietme.instantiable.CustomFile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class SharedFilesFragment extends Fragment implements SharedFileListAdapter.OnSharedFileClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private ArrayList<CustomFile> sharedFiles = new ArrayList<>();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private StorageReference pdfFiles = storageRef.child("documents/pdf");


    public SharedFilesFragment() {
    }

    public static SharedFilesFragment newInstance(String param1, String param2) {
        SharedFilesFragment fragment = new SharedFilesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
        initRecyclerView();
        pdfFiles.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {

                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    CustomFile file = new CustomFile(item.getName(), uri.toString());
                                    sharedFiles.add(file);
                                    recyclerView.getAdapter().notifyDataSetChanged();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Utils.toast(getActivity().getApplicationContext(), "Fallo recogiendo uri");
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

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
