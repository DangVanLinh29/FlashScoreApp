package com.example.flashscoreapp.ui.search;

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
import com.example.flashscoreapp.data.model.domain.Team;
import com.example.flashscoreapp.data.model.remote.ApiLeagueData;
import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_LEAGUE = 1;
    private static final int TYPE_TEAM = 2;

    private List<Object> items = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onLeagueClick(ApiLeagueData leagueData);
        void onTeamClick(Team team);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<Object> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof String) return TYPE_HEADER;
        if (item instanceof ApiLeagueData) return TYPE_LEAGUE;
        if (item instanceof Team) return TYPE_TEAM;
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            return new HeaderViewHolder(inflater.inflate(R.layout.item_group_header, parent, false));
        } else if (viewType == TYPE_LEAGUE) {
            return new LeagueViewHolder(inflater.inflate(R.layout.item_league, parent, false));
        } else { // TYPE_TEAM
            return new TeamViewHolder(inflater.inflate(R.layout.item_league, parent, false)); // Tái sử dụng layout
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = items.get(position);
        if (holder.getItemViewType() == TYPE_HEADER) {
            ((HeaderViewHolder) holder).bind((String) item);
        } else if (holder.getItemViewType() == TYPE_LEAGUE) {
            ApiLeagueData leagueData = (ApiLeagueData) item;
            ((LeagueViewHolder) holder).bind(leagueData.getLeague());
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onLeagueClick(leagueData);
            });
        } else if (holder.getItemViewType() == TYPE_TEAM) {
            Team team = (Team) item;
            ((TeamViewHolder) holder).bind(team);
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onTeamClick(team);
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ViewHolders
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView textHeader;
        HeaderViewHolder(View itemView) {
            super(itemView);
            textHeader = itemView.findViewById(R.id.text_group_header);
        }
        void bind(String title) {
            textHeader.setText(title);
        }
    }

    static class LeagueViewHolder extends RecyclerView.ViewHolder {
        ImageView logo;
        TextView name;
        LeagueViewHolder(View itemView) {
            super(itemView);
            logo = itemView.findViewById(R.id.image_league_logo);
            name = itemView.findViewById(R.id.text_league_name);
        }
        void bind(League league) {
            name.setText(league.getName());
            Glide.with(itemView.getContext()).load(league.getLogoUrl()).into(logo);
        }
    }

    static class TeamViewHolder extends RecyclerView.ViewHolder {
        ImageView logo;
        TextView name;
        TeamViewHolder(View itemView) {
            super(itemView);
            logo = itemView.findViewById(R.id.image_league_logo);
            name = itemView.findViewById(R.id.text_league_name);
        }
        void bind(Team team) {
            name.setText(team.getName());
            Glide.with(itemView.getContext()).load(team.getLogoUrl()).into(logo);
        }
    }
}