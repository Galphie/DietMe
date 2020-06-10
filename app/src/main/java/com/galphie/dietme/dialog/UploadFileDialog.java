package com.galphie.dietme.dialog;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.galphie.dietme.R;
import com.galphie.dietme.Utils;
import com.galphie.dietme.instantiable.CustomFile;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class UploadFileDialog extends DialogFragment implements TextWatcher {

    private TextView nameText;
    private EditText nameEdit;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.upload_file_name_writer, null);

        nameEdit = view.findViewById(R.id.new_file_name_edit);
        nameText = view.findViewById(R.id.new_file_name_text);

        nameEdit.addTextChangedListener(this);

        builder.setView(view)
                .setTitle(R.string.introduce_file_name)
                .setPositiveButton(getString(R.string.upload), (dialog, which) -> {
                    String newFileName = nameEdit.getText().toString();
                    String patientId = Objects.requireNonNull(getArguments()).getString("patientId");
                    newFileName = newFileName.concat(".pdf");
                    Uri selectedPdf = Uri.parse(getArguments().getString("pdf"));
                    StorageMetadata metadata = new StorageMetadata.Builder()
                            .setContentType("application/pdf")
                            .build();
                    StorageReference userFiles = storage.getReference().child("users/" + patientId);
                    DatabaseReference userFilesDatabase = database.getReference().child("Archivos/users/" + patientId);
                    StorageReference selectedPdfRef = userFiles.child(newFileName);
                    UploadTask uploadTask = selectedPdfRef.putFile(selectedPdf, metadata);
                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                    });
                    Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw Objects.requireNonNull(task.getException());
                        }
                        return selectedPdfRef.getDownloadUrl();
                    }).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            CustomFile uploadedFile = new CustomFile(selectedPdfRef.getName(), Objects.requireNonNull(downloadUri).toString(), selectedPdfRef.getPath());
                            userFilesDatabase.child(Objects.requireNonNull(Utils.MD5(uploadedFile.getName()))).setValue(uploadedFile);
                        }
                    });
                    Utils.toast(getActivity().getApplicationContext(), getString(R.string.succesfully_upload));
                    dismiss();
                });

        return builder.create();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        nameText.setText(String.format("%s.pdf", s.toString()));
    }
}
