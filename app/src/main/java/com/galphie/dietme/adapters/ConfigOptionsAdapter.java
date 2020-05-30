package com.galphie.dietme.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.galphie.dietme.R;
import com.galphie.dietme.instantiable.Option;

import java.util.ArrayList;

public class ConfigOptionsAdapter extends RecyclerView.Adapter<ConfigOptionsAdapter.ViewHolder> {
    private ArrayList<Option> mOptions;
    private OnOptionClickListener mOnOptionClickListener;

    public ConfigOptionsAdapter(ArrayList<Option> mOptions, OnOptionClickListener mOnOptionClickListener) {
        this.mOptions = mOptions;
        this.mOnOptionClickListener = mOnOptionClickListener;
    }

    @NonNull
    @Override
    public ConfigOptionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.config_list_item, parent, false);
        return new ViewHolder(view, mOnOptionClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ConfigOptionsAdapter.ViewHolder holder, int position) {
        holder.name.setText(mOptions.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mOptions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        OnOptionClickListener onOptionClickListener;

        public ViewHolder(@NonNull View itemView, OnOptionClickListener onOptionClickListener) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            this.onOptionClickListener = onOptionClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onOptionClickListener.onOptionClick(getAdapterPosition());
        }
    }

    public interface OnOptionClickListener {
        void onOptionClick(int position);
    }
}
