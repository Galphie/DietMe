package com.galphie.dietme.adapters;

import android.text.Html;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.galphie.dietme.R;
import com.galphie.dietme.Utils;
import com.galphie.dietme.instantiable.Post;

import java.util.ArrayList;
import java.util.regex.Matcher;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ViewHolder> {
    private ArrayList<Post> postArrayList;
    private OnPostClickListener mListener;

    public PostListAdapter(ArrayList<Post> postArrayList, OnPostClickListener mListener) {
        this.postArrayList = postArrayList;
        this.mListener = mListener;
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
        String message = postArrayList.get(position).getMessage();
        holder.postMessage.setText(message);
        holder.postPublishDate.setText(time);
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView postPublishDate, postMessage;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            postPublishDate = itemView.findViewById(R.id.post_publish_date);
            postMessage = itemView.findViewById(R.id.post_message);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onPostClick(v, getAdapterPosition());
        }
    }

    public interface OnPostClickListener {
        void onPostClick(View view, int position);
    }
}
