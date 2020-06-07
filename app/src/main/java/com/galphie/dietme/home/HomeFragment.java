package com.galphie.dietme.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.galphie.dietme.R;
import com.galphie.dietme.Utils;
import com.galphie.dietme.adapters.PostListAdapter;
import com.galphie.dietme.dialog.ConfirmActionDialog;
import com.galphie.dietme.dialog.PostDialog;
import com.galphie.dietme.instantiable.Post;
import com.galphie.dietme.instantiable.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;


public class HomeFragment extends Fragment implements ValueEventListener, PostListAdapter.OnPostClickListener {

    private User currentUser;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private RecyclerView recyclerView;
    private ArrayList<Post> postArrayList = new ArrayList<>();

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUser = getArguments().getParcelable("currentUser");
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Objects.requireNonNull(getActivity()).finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton addPostButton = view.findViewById(R.id.add_post_button);
        recyclerView = view.findViewById(R.id.postRecyclerView);

        initRecyclerView();

        DatabaseReference postsRef = database.getReference("Publicaciones");
        postsRef.addValueEventListener(this);

        addPostButton.setOnClickListener(v -> {
            DialogFragment dialogFragment = new PostDialog();
            dialogFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "Post");
        });

        if (currentUser.isAdmin()) {
            addPostButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        PostListAdapter adapter = new PostListAdapter(postArrayList, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        postArrayList.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            Post post = ds.getValue(Post.class);
            postArrayList.add(post);
        }
        Collections.sort(postArrayList, (o1, o2) -> Utils.stringToLocalDateTime(o2.getPublishDate())
                .compareTo(Utils.stringToLocalDateTime(o1.getPublishDate())));
        Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    @Override
    public void onPostClick(View v, int position) {
        if (currentUser.isAdmin()) {
            showPopUp(v, position);
        }
    }

    private void showPopUp(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.inflate(R.menu.patient_contextual_menu);

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.edit_option:
                    if (currentUser.isAdmin()) {
                        Bundle bundle = new Bundle();
                        bundle.putString("post_message", postArrayList.get(position).getMessage());
                        bundle.putString("post_publish_date", postArrayList.get(position).getPublishDate());
                        DialogFragment dialogFragment = new PostDialog();
                        dialogFragment.setArguments(bundle);
                        dialogFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "Post");
                    } else {
                        Utils.toast(Objects.requireNonNull(getActivity()).getApplicationContext(), getString(R.string.developer_action_only));
                    }
                    return true;
                case R.id.remove_option:
                    if (currentUser.isAdmin()) {
                        DialogFragment dialogFragment = new ConfirmActionDialog();
                        Bundle bundle = new Bundle();
                        bundle.putString("confirm_action_dialog_message", "¿Eliminar publicación?");
                        bundle.putInt("type", ConfirmActionDialog.DELETE_POST_CODE);
                        bundle.putString("object", postArrayList.get(position).getPublishDate());
                        dialogFragment.setArguments(bundle);
                        dialogFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "DeletePost");
                    } else {
                        Utils.toast(Objects.requireNonNull(getActivity()).getApplicationContext(), getString(R.string.developer_action_only));
                    }
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.show();
    }
}
