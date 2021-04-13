package com.ajikartiko.go_wisuda_dosen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.ajikartiko.go_wisuda_dosen.model.History;
import com.ajikartiko.go_wisuda_dosen.utils.OnItemClickListener;

public class HistoryAdapter extends ListAdapter<History, HistoryAdapter.HistoryViewHolder> {
    private final OnItemClickListener listener;

    public HistoryAdapter(OnItemClickListener listener) {
        super(History.DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        History history = getItem(position);
        if (history != null) {
            holder.bindTo(history, listener);
        }
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout main;
        AppCompatTextView title, count;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            main = itemView.findViewById(R.id.history_root);
            title = itemView.findViewById(R.id.history_title);
            count = itemView.findViewById(R.id.history_count);
        }

        public void bindTo(History history, OnItemClickListener listener) {
            title.setText(history.getDay());
            main.setOnClickListener(v -> listener.onItemClick(history));
            count.setText(count.getContext().getResources().getString(R.string.chat_count, history.getMessages().size()));
        }
    }


}
