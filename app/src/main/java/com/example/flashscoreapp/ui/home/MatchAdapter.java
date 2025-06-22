package com.example.flashscoreapp.ui.home;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.flashscoreapp.R;
import com.example.flashscoreapp.data.model.domain.Match;
import com.example.flashscoreapp.data.model.domain.Team;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    // Interface hoàn chỉnh với 3 phương thức
    public interface OnItemClickListener {
        void onItemClick(Match match);
        void onFavoriteClick(Match match, boolean isFavorite);
        void onTeamClick(Team team, Match matchContext);
    }

    private List<Match> matches = new ArrayList<>();
    private OnItemClickListener listener;
    private Set<Integer> favoriteMatchIds = new HashSet<>();

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        Match match = matches.get(position);
        boolean isFavorite = favoriteMatchIds.contains(match.getMatchId());
        // Truyền listener vào hàm bind
        holder.bind(match, isFavorite, listener);
    }

    @Override
    public int getItemCount() {
        return matches == null ? 0 : matches.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setMatches(List<Match> newMatches) {
        this.matches = newMatches;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFavoriteMatchIds(Set<Integer> newFavoriteMatchIds) {
        this.favoriteMatchIds = newFavoriteMatchIds;
        notifyDataSetChanged();
    }

    public static class MatchViewHolder extends RecyclerView.ViewHolder {
        private final TextView textLeft, textRight, textHomeName, textAwayName;
        private final ImageView imageHomeLogo, imageAwayLogo, imageViewFavorite;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            textLeft = itemView.findViewById(R.id.text_left_column);
            textRight = itemView.findViewById(R.id.text_right_column);
            textHomeName = itemView.findViewById(R.id.text_home_team_name);
            textAwayName = itemView.findViewById(R.id.text_away_team_name);
            imageHomeLogo = itemView.findViewById(R.id.image_home_logo);
            imageAwayLogo = itemView.findViewById(R.id.image_away_logo);
            imageViewFavorite = itemView.findViewById(R.id.image_view_favorite);
        }

        public void bind(final Match match, final boolean isFavorite, final OnItemClickListener listener) {
            // 1. Gán dữ liệu vào các view
            textHomeName.setText(match.getHomeTeam().getName());
            textAwayName.setText(match.getAwayTeam().getName());
            Glide.with(itemView.getContext()).load(match.getHomeTeam().getLogoUrl()).placeholder(R.drawable.ic_leagues_24).into(imageHomeLogo);
            Glide.with(itemView.getContext()).load(match.getAwayTeam().getLogoUrl()).placeholder(R.drawable.ic_leagues_24).into(imageAwayLogo);

            String status = match.getStatus();
            int secondaryColor = ContextCompat.getColor(itemView.getContext(), R.color.season_date_text);
            int primaryColor = ContextCompat.getColor(itemView.getContext(), R.color.black);
            textLeft.setTextColor(secondaryColor);
            textRight.setTextColor(primaryColor);
            textRight.setTextSize(16f);

            if ("NS".equalsIgnoreCase(status)) {
                Date matchDate = new Date(match.getMatchTime());
                textLeft.setText(new SimpleDateFormat("dd.MM.", Locale.getDefault()).format(matchDate));
                textRight.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(matchDate));
                textRight.setTextSize(14f);
                textRight.setTextColor(secondaryColor);
            } else if (status != null && (status.equalsIgnoreCase("1H") || status.equalsIgnoreCase("2H") || status.equalsIgnoreCase("HT"))) {
                textLeft.setText(status);
                textLeft.setTextColor(Color.RED);
                textRight.setText(match.getScore().getHome() + " - " + match.getScore().getAway());
            } else {
                textLeft.setText(status);
                textRight.setText(match.getScore().getHome() + " - " + match.getScore().getAway());
            }

            // 2. Gán tất cả sự kiện click
            // Click vào cả hàng
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(match);
            });

            // Click vào logo đội nhà
            imageHomeLogo.setOnClickListener(v -> {
                if (listener != null) listener.onTeamClick(match.getHomeTeam(), match);
            });

            // Click vào logo đội khách
            imageAwayLogo.setOnClickListener(v -> {
                if (listener != null) listener.onTeamClick(match.getAwayTeam(), match);
            });

            // Click vào nút yêu thích
            if (isStatusFinished(status)) {
                imageViewFavorite.setVisibility(View.GONE);
                imageViewFavorite.setOnClickListener(null);
            } else {
                imageViewFavorite.setVisibility(View.VISIBLE);
                imageViewFavorite.setImageResource(isFavorite ? R.drawable.ic_star_filled : R.drawable.ic_star_empty);
                imageViewFavorite.setOnClickListener(v -> {
                    if (listener != null) listener.onFavoriteClick(match, isFavorite);
                });
            }
        }

        private boolean isStatusFinished(String status) {
            if (status == null) return false;
            List<String> finishedStatuses = Arrays.asList("FT", "AET", "PEN", "PST", "CANC", "ABD", "AWD", "WO");
            return finishedStatuses.contains(status);
        }
    }
}