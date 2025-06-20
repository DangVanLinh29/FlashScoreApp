package com.example.flashscoreapp.ui.match_details.doidau;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashscoreapp.R;
import com.example.flashscoreapp.data.model.domain.Match;
import com.example.flashscoreapp.ui.home.MatchAdapter;

import java.util.ArrayList;
import java.util.List;

public class RecentMatchesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_MATCH = 1;

    private List<Object> items = new ArrayList<>();
    // Sửa tên biến cho rõ ràng hơn
    private MatchAdapter.OnItemClickListener matchClickListener;

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<Object> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(MatchAdapter.OnItemClickListener listener) {
        this.matchClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof String) {
            return TYPE_HEADER;
        }
        return TYPE_MATCH;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_group_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            // Dùng layout item_match đã được cập nhật
            View view = inflater.inflate(R.layout.item_match, parent, false);
            // Trả về ViewHolder chung từ MatchAdapter
            return new MatchAdapter.MatchViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_HEADER) {
            ((HeaderViewHolder) holder).bind((String) items.get(position));
        } else {
            // --- SỬA LỖI TẠI ĐÂY ---
            MatchAdapter.MatchViewHolder matchViewHolder = (MatchAdapter.MatchViewHolder) holder;
            Match match = (Match) items.get(position);

            // Gọi hàm bind với đủ 3 tham số, bao gồm cả listener.
            // isFavorite là false vì đây là danh sách các trận gần đây, không phải danh sách yêu thích.
            matchViewHolder.bind(match, false, matchClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView textHeader;
        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            textHeader = itemView.findViewById(R.id.text_group_header);
        }
        void bind(String title) {
            textHeader.setText(title);
        }
    }
}