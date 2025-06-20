package com.example.flashscoreapp.ui.leaguedetails.results;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.flashscoreapp.R;
import com.example.flashscoreapp.data.model.domain.Match;
import com.example.flashscoreapp.data.model.domain.RoundHeader;
import com.example.flashscoreapp.ui.home.MatchAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_MATCH = 1;

    private List<Object> items = new ArrayList<>();
    private MatchAdapter.OnItemClickListener matchClickListener;
    // Thêm Set để lưu ID các trận yêu thích
    private Set<Integer> favoriteMatchIds = new HashSet<>();

    public void setOnItemClickListener(MatchAdapter.OnItemClickListener listener) {
        this.matchClickListener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<Object> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    // Thêm hàm này để nhận danh sách yêu thích từ Fragment
    @SuppressLint("NotifyDataSetChanged")
    public void setFavoriteMatchIds(Set<Integer> favoriteMatchIds) {
        this.favoriteMatchIds = favoriteMatchIds;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof RoundHeader) {
            return TYPE_HEADER;
        }
        return TYPE_MATCH;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_round_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_match, parent, false);
            return new MatchAdapter.MatchViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_HEADER) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.bind((RoundHeader) items.get(position));
        } else {
            MatchAdapter.MatchViewHolder matchViewHolder = (MatchAdapter.MatchViewHolder) holder;
            Match match = (Match) items.get(position);
            // Kiểm tra xem trận đấu này có trong danh sách yêu thích không
            boolean isFavorite = favoriteMatchIds.contains(match.getMatchId());
            // Gọi hàm bind với trạng thái isFavorite chính xác
            matchViewHolder.bind(match, isFavorite, matchClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView textRoundName;
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            textRoundName = itemView.findViewById(R.id.text_round_name);
        }
        public void bind(RoundHeader header) {
            textRoundName.setText(header.getRoundName());
        }
    }
}