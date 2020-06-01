package com.galphie.dietme.dialog;

import android.app.Dialog;
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
import com.galphie.dietme.instantiable.Post;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.util.Objects;

public class PostDialog extends DialogFragment implements TextWatcher {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private TextView newPostMessage;
    private EditText editPost;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.post_dialog_writer, null);

        editPost = view.findViewById(R.id.editPost);
        newPostMessage = view.findViewById(R.id.new_post_message);
        editPost.addTextChangedListener(this);

        if (getArguments() != null) {
            editPost.setText(getArguments().getString("post_message"));
        }

        builder.setView(view)
                .setPositiveButton(getString(R.string.send), (dialog, which) -> {
                    String publishDate = "";
                    if (getArguments() != null) {
                        publishDate = getArguments().getString("post_publish_date");
                    } else {
                        publishDate = Utils.localDateTimeToDisplayString(LocalDateTime.now());
                    }
                    Post post = new Post(editPost.getText().toString(), publishDate);
                    DatabaseReference postsRef = database.getReference("Publicaciones");
                    if (editPost.getText().toString().equals("")) {
                        if (getArguments() != null) {
                            postsRef.child(Objects.requireNonNull(publishDate)).removeValue();
                        } else {
                            dismiss();
                        }
                    } else {
                        postsRef.child(Objects.requireNonNull(publishDate)).setValue(post);
                    }
                    dismiss();
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dismiss());

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
        newPostMessage.setText(s.toString());
    }
}
