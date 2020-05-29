package com.galphie.dietme.patients;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.galphie.dietme.R;
import com.galphie.dietme.instantiable.CustomFile;

import java.util.ArrayList;

public class SharedFileListAdapter extends RecyclerView.Adapter<SharedFileListAdapter.ViewHolder> {

    private ArrayList<CustomFile> sharedFiles;
    private OnSharedFileClickListener mListener;

    public SharedFileListAdapter(ArrayList<CustomFile> sharedFiles, OnSharedFileClickListener mListener) {
        this.sharedFiles = sharedFiles;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public SharedFileListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_file_item, parent, false);
        return new ViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SharedFileListAdapter.ViewHolder holder, int position) {
        holder.name.setText(sharedFiles.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return sharedFiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        ImageButton button;
        OnSharedFileClickListener mListener;
        public ViewHolder(@NonNull View itemView, OnSharedFileClickListener mListener) {
            super(itemView);

            name = itemView.findViewById(R.id.shared_file_name);
            button = itemView.findViewById(R.id.shared_file_download);
            this.mListener = mListener;

            button.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onSharedFileClick(getAdapterPosition());
        }
    }

    public interface OnSharedFileClickListener {
        void onSharedFileClick (int position);
    }
}
