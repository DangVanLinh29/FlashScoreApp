package com.example.flashscoreapp.ui.leaguedetails.standings;

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
import com.example.flashscoreapp.data.model.domain.StandingItem;
import com.example.flashscoreapp.data.model.domain.Team;
import com.example.flashscoreapp.data.model.remote.ApiTeamInfo; // Import ApiTeamInfo

import java.util.ArrayList;
import java.util.List;

public class StandingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<Object> displayList = new ArrayList<>();
    private OnTeamClickListener teamClickListener;

    public interface OnTeamClickListener {
        void onTeamClicked(Team team);
    }

    public void setOnTeamClickListener(OnTeamClickListener listener) {
        this.teamClickListener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDisplayList(List<Object> list) {
        this.displayList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (displayList.get(position) instanceof String) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_group_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_standing, parent, false);
            return new StandingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_HEADER) {
            ((HeaderViewHolder) holder).bind((String) displayList.get(position));
        } else {
            StandingItem item = (StandingItem) displayList.get(position);
            ((StandingViewHolder) holder).bind(item);

            // Gán sự kiện click cho cả hàng ở đây
            holder.itemView.setOnClickListener(v -> {
                if (teamClickListener != null) {
                    ApiTeamInfo apiTeam = item.getTeam();
                    // Chuyển đổi từ ApiTeamInfo sang Team domain model
                    Team team = new Team(apiTeam.getId(), apiTeam.getName(), apiTeam.getLogo());
                    teamClickListener.onTeamClicked(team);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView textHeader;
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            textHeader = itemView.findViewById(R.id.text_group_header);
        }
        void bind(String title) {
            textHeader.setText(title);
        }
    }

    static class StandingViewHolder extends RecyclerView.ViewHolder {
        TextView textRank, textTeamName, textPlayed, text_goals_for_against, textPoints;
        ImageView imageTeamLogo;

        public StandingViewHolder(@NonNull View itemView) {
            super(itemView);
            textRank = itemView.findViewById(R.id.text_rank);
            textTeamName = itemView.findViewById(R.id.text_team_name);
            textPlayed = itemView.findViewById(R.id.text_played);
            text_goals_for_against = itemView.findViewById(R.id.text_goals_for_against);
            textPoints = itemView.findViewById(R.id.text_points);
            imageTeamLogo = itemView.findViewById(R.id.image_team_logo);
        }

        void bind(final StandingItem item) {
            textRank.setText(String.valueOf(item.getRank()));
            textTeamName.setText(item.getTeam().getName());
            textPlayed.setText(String.valueOf(item.getAll().getPlayed()));
            String goals = item.getAll().getGoals().getGoalsFor() + ":" + item.getAll().getGoals().getGoalsAgainst();
            text_goals_for_against.setText(goals);
            textPoints.setText(String.valueOf(item.getPoints()));
            Glide.with(itemView.getContext()).load(item.getTeam().getLogo()).into(imageTeamLogo);
        }
    }
}