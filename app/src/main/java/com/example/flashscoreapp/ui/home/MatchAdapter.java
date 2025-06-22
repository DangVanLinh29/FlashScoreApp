package com.example.flashscoreapp.ui.home;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.TypedValue;
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
import com.example.flashscoreapp.data.model.domain.Score;
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
        // Truyền listener vào hàm bind để xử lý click bên trong ViewHolder
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
        public final TextView textLeftColumn, textHomeTeamName, textAwayTeamName, textRightColumn;
        public final ImageView imageHomeLogo, imageAwayLogo, imageViewFavorite;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            textLeftColumn = itemView.findViewById(R.id.text_left_column);
            textHomeTeamName = itemView.findViewById(R.id.text_home_team_name);
            textAwayTeamName = itemView.findViewById(R.id.text_away_team_name);
            textRightColumn = itemView.findViewById(R.id.text_right_column);
            imageHomeLogo = itemView.findViewById(R.id.image_home_logo);
            imageAwayLogo = itemView.findViewById(R.id.image_away_logo);
            imageViewFavorite = itemView.findViewById(R.id.image_view_favorite);
        }

        public void bind(final Match match, final boolean isFavorite, final OnItemClickListener listener) {
            // 1. Gán dữ liệu vào các view
            if(match.getHomeTeam() != null) {
                textHomeTeamName.setText(match.getHomeTeam().getName());
                Glide.with(itemView.getContext()).load(match.getHomeTeam().getLogoUrl()).placeholder(R.drawable.ic_leagues_24).into(imageHomeLogo);
            }
            if(match.getAwayTeam() != null) {
                textAwayTeamName.setText(match.getAwayTeam().getName());
                Glide.with(itemView.getContext()).load(match.getAwayTeam().getLogoUrl()).placeholder(R.drawable.ic_leagues_24).into(imageAwayLogo);
            }

            // 2. Xử lý hiển thị cột trái (trạng thái/thời gian) và cột phải (tỉ số)
            String status = match.getStatus();
            TypedValue typedValue = new TypedValue();
            itemView.getContext().getTheme().resolveAttribute(android.R.attr.textColorSecondary, typedValue, true);
            int secondaryTextColor = ContextCompat.getColor(itemView.getContext(), typedValue.resourceId);

            textLeftColumn.setTextColor(secondaryTextColor); // Đặt màu mặc định

            if ("NS".equals(status)) { // Trận chưa đá
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                textLeftColumn.setText(timeFormat.format(new Date(match.getMatchTime())));
                textRightColumn.setText("");
            } else { // Các trạng thái khác (LIVE, FT, HT...)
                textLeftColumn.setText(status);
                Score score = match.getScore();
                if (score != null) {
                    textRightColumn.setText(score.getHome() + " - " + score.getAway());
                }
                if (isStatusLive(status)) {
                    textLeftColumn.setTextColor(Color.RED);
                }
            }

            // 3. Gán các sự kiện click
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(match);
            });
            textHomeTeamName.setOnClickListener(v -> {
                if (listener != null) listener.onTeamClick(match.getHomeTeam(), match);
            });
            textAwayTeamName.setOnClickListener(v -> {
                if (listener != null) listener.onTeamClick(match.getAwayTeam(), match);
            });

            // 4. Xử lý nút yêu thích
            if (isStatusFinished(status)) {
                imageViewFavorite.setVisibility(View.INVISIBLE);
                imageViewFavorite.setOnClickListener(null);
            } else {
                imageViewFavorite.setVisibility(View.VISIBLE);
                imageViewFavorite.setImageResource(isFavorite ? R.drawable.ic_star_filled : R.drawable.ic_star_empty);
                imageViewFavorite.setOnClickListener(v -> {
                    if (listener != null) listener.onFavoriteClick(match, isFavorite);
                });
            }
        }

        // --- Các hàm hỗ trợ ---
        private boolean isStatusFinished(String status) {
            if (status == null) return true; // Mặc định ẩn nếu không có trạng thái
            List<String> finishedStatus = Arrays.asList("FT", "AET", "PEN", "CANC", "ABD", "AWD", "WO");
            return finishedStatus.contains(status);
        }

        private boolean isStatusLive(String status) {
            if (status == null) return false;
            List<String> liveStatus = Arrays.asList("1H", "HT", "2H", "ET", "BT", "P", "LIVE");
            return liveStatus.contains(status) || status.matches("\\d+'?");
        }
    }
}