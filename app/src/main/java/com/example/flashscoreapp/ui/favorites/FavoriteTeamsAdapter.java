package com.example.flashscoreapp.ui.favorites;

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
import com.example.flashscoreapp.data.model.domain.Match;
import com.example.flashscoreapp.data.model.domain.Team;
import com.example.flashscoreapp.ui.home.MatchAdapter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoriteTeamsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_TEAM_HEADER = 0;
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
        if (displayList.get(position) instanceof Team) {
            return TYPE_TEAM_HEADER;
        }
        return TYPE_MATCH;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_TEAM_HEADER) {
            View view = inflater.inflate(R.layout.item_favorite_team_header, parent, false);
            return new TeamHeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_match, parent, false);
            return new MatchAdapter.MatchViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_TEAM_HEADER) {
            ((TeamHeaderViewHolder) holder).bind((Team) displayList.get(position));
        } else {
            MatchAdapter.MatchViewHolder matchViewHolder = (MatchAdapter.MatchViewHolder) holder;
            Match match = (Match) displayList.get(position);
            boolean isFavorite = favoriteMatchIds.contains(match.getMatchId());
            matchViewHolder.bind(match, isFavorite, listener);
        }
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    static class TeamHeaderViewHolder extends RecyclerView.ViewHolder {
        private final ImageView teamLogo;
        private final TextView teamName;

        TeamHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            teamLogo = itemView.findViewById(R.id.image_team_logo);
            teamName = itemView.findViewById(R.id.text_team_name);
        }

        void bind(Team team) {
            teamName.setText(team.getName());
            Glide.with(itemView.getContext())
                    .load(team.getLogoUrl())
                    .placeholder(R.drawable.ic_leagues_24)
                    .into(teamLogo);
        }
    }
}