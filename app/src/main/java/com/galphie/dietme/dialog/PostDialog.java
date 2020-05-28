package com.galphie.dietme.dialog;

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
import com.galphie.dietme.instantiable.Post;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.util.Objects;

public class PostDialog extends DialogFragment {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference postsRef = database.getReference("Publicaciones");

    private EditText editPost;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.post_dialog_writer, null);

        editPost = view.findViewById(R.id.editPost);

        builder.setView(view)
                .setPositiveButton(getString(R.string.send), (dialog, which) -> {
                    Post post = new Post(editPost.getText().toString(),Utils.localDateTimeToDisplayString(LocalDateTime.now()));
                    postsRef.child(Utils.localDateTimeToDisplayString(LocalDateTime.now())).setValue(post);
                    dismiss();
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dismiss());

        return builder.create();
    }
}
