package com.galphie.dietme.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.galphie.dietme.R;
import com.galphie.dietme.Utils;
import com.galphie.dietme.instantiable.Post;

import java.util.ArrayList;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ViewHolder> {
    private ArrayList<Post> postArrayList;

    public PostListAdapter(ArrayList<Post> postArrayList) {
        this.postArrayList = postArrayList;
    }

    @NonNull
    @Override
    public PostListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostListAdapter.ViewHolder holder, int position) {
        String time = Utils.timeBetween(Utils.stringToLocalDateTime(postArrayList.get(position).getPublishDate()));
        holder.postMessage.setText(postArrayList.get(position).getMessage());
        holder.postPublishDate.setText(time);
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView postPublishDate, postMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            postPublishDate = itemView.findViewById(R.id.post_publish_date);
            postMessage = itemView.findViewById(R.id.post_message);
        }
    }
}
