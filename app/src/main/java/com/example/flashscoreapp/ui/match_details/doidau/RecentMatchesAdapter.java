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
    private MatchAdapter.OnItemClickListener listener;

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<Object> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(MatchAdapter.OnItemClickListener listener) {
        this.listener = listener;
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
            View view = inflater.inflate(R.layout.item_match, parent, false);
            return new MatchAdapter.MatchViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_HEADER) {
            ((HeaderViewHolder) holder).bind((String) items.get(position));
        } else {
            MatchAdapter.MatchViewHolder matchViewHolder = (MatchAdapter.MatchViewHolder) holder;
            Match match = (Match) items.get(position);

            // --- SỬA LẠI TẠI ĐÂY ---
            // 1. Gọi hàm bind chỉ với 2 tham số
            // Trong tab H2H, chúng ta không cần hiển thị sao yêu thích nên isFavorite là false
            matchViewHolder.bind(match, false);

            // 2. Gán sự kiện click cho cả hàng ở đây
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(match);
                }
            });

            // Nút yêu thích đã được ẩn đi cho các trận đã kết thúc bên trong ViewHolder
            // nên không cần gán listener cho nó ở đây nữa.
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