package com.example.flashscoreapp.ui.home;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.flashscoreapp.R;
import com.example.flashscoreapp.data.model.domain.League;
import com.example.flashscoreapp.data.model.domain.Match;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeGroupedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_LEAGUE_HEADER = 0;
    private static final int TYPE_MATCH = 1;

    private List<Object> displayList = new ArrayList<>();
    private MatchAdapter.OnItemClickListener listener;
    private Set<Integer> favoriteMatchIds = new HashSet<>();

    public void setOnItemClickListener(MatchAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDisplayList(List<Object> displayList) {
        this.displayList = displayList;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFavoriteMatchIds(Set<Integer> favoriteMatchIds) {
        this.favoriteMatchIds = favoriteMatchIds;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (displayList.get(position) instanceof League) {
            return TYPE_LEAGUE_HEADER;
        }
        return TYPE_MATCH;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_LEAGUE_HEADER) {
            View view = inflater.inflate(R.layout.item_league_header, parent, false);
            return new LeagueHeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_match, parent, false);
            return new MatchAdapter.MatchViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_LEAGUE_HEADER) {
            ((LeagueHeaderViewHolder) holder).bind((League) displayList.get(position));
        } else {
            // SỬA LẠI LOGIC NÀY CHO ĐÚNG
            MatchAdapter.MatchViewHolder matchViewHolder = (MatchAdapter.MatchViewHolder) holder;
            Match match = (Match) displayList.get(position);
            boolean isFavorite = favoriteMatchIds.contains(match.getMatchId());
            // Gọi hàm bind với đủ 3 tham số
            matchViewHolder.bind(match, isFavorite, listener);
        }
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    static class LeagueHeaderViewHolder extends RecyclerView.ViewHolder {
        private final ImageView leagueLogo;
        private final TextView leagueName, leagueCountry;

        public LeagueHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            leagueLogo = itemView.findViewById(R.id.image_header_league_logo);
            leagueName = itemView.findViewById(R.id.text_header_league_name);
            leagueCountry = itemView.findViewById(R.id.text_header_league_country);
        }

        void bind(League league) {
            leagueName.setText(league.getName());
            leagueCountry.setText(league.getCountry());
            Glide.with(itemView.getContext())
                    .load(league.getLogoUrl())
                    .placeholder(R.drawable.ic_leagues_24)
                    .into(leagueLogo);
        }
    }
}