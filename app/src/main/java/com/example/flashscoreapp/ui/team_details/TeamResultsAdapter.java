package com.example.flashscoreapp.ui.team_details;

import android.annotation.SuppressLint;
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
import com.example.flashscoreapp.data.model.domain.League;
import com.example.flashscoreapp.data.model.domain.Match;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TeamResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_LEAGUE = 0;
    private static final int TYPE_MATCH = 1;

    private List<Object> items = new ArrayList<>();
    private final int currentTeamId;
    private OnMatchClickListener matchClickListener;

    public interface OnMatchClickListener {
        void onMatchClicked(Match match);
    }

    public void setOnMatchClickListener(OnMatchClickListener listener) {
        this.matchClickListener = listener;
    }

    public TeamResultsAdapter(int currentTeamId) {
        this.currentTeamId = currentTeamId;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<Object> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof League) {
            return TYPE_LEAGUE;
        }
        return TYPE_MATCH;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_LEAGUE) {
            View view = inflater.inflate(R.layout.item_league_header, parent, false);
            return new LeagueHeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_team_match, parent, false);
            return new MatchViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_LEAGUE) {
            ((LeagueHeaderViewHolder) holder).bind((League) items.get(position));
        } else {
            Match match = (Match) items.get(position);
            ((MatchViewHolder) holder).bind(match);

            // 3. Gán sự kiện click cho cả hàng (itemView)
            holder.itemView.setOnClickListener(v -> {
                if (matchClickListener != null) {
                    matchClickListener.onMatchClicked(match);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class LeagueHeaderViewHolder extends RecyclerView.ViewHolder {
        ImageView leagueLogo;
        TextView leagueName, leagueCountry;
        LeagueHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            leagueLogo = itemView.findViewById(R.id.image_header_league_logo);
            leagueName = itemView.findViewById(R.id.text_header_league_name);
            leagueCountry = itemView.findViewById(R.id.text_header_league_country);
        }
        void bind(League league) {
            leagueName.setText(league.getName());
            leagueCountry.setText(league.getCountry());
            Glide.with(itemView.getContext()).load(league.getLogoUrl()).into(leagueLogo);
        }
    }

    class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView textDate, textHomeName, textAwayName, textHomeScore, textAwayScore, textResultLozenge;
        ImageView imageHomeLogo, imageAwayLogo;

        MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.text_match_date);
            textHomeName = itemView.findViewById(R.id.text_home_name);
            textAwayName = itemView.findViewById(R.id.text_away_name);
            textHomeScore = itemView.findViewById(R.id.text_home_score);
            textAwayScore = itemView.findViewById(R.id.text_away_score);
            textResultLozenge = itemView.findViewById(R.id.text_result_lozenge);
            imageHomeLogo = itemView.findViewById(R.id.image_home_logo);
            imageAwayLogo = itemView.findViewById(R.id.image_away_logo);
        }

        void bind(Match match) {
            // Reset style
            textHomeName.setTypeface(null, Typeface.NORMAL);
            textAwayName.setTypeface(null, Typeface.NORMAL);
            textHomeScore.setTypeface(null, Typeface.NORMAL);
            textAwayScore.setTypeface(null, Typeface.NORMAL);

            // Gán dữ liệu chung
            textDate.setText(new SimpleDateFormat("dd.MM.", Locale.getDefault()).format(new Date(match.getMatchTime())));
            textHomeName.setText(match.getHomeTeam().getName());
            textAwayName.setText(match.getAwayTeam().getName());
            textHomeScore.setText(String.valueOf(match.getScore().getHome()));
            textAwayScore.setText(String.valueOf(match.getScore().getAway()));
            Glide.with(itemView.getContext()).load(match.getHomeTeam().getLogoUrl()).into(imageHomeLogo);
            Glide.with(itemView.getContext()).load(match.getAwayTeam().getLogoUrl()).into(imageAwayLogo);

            // Xác định kết quả và style
            boolean isHomeTeamTheCurrentTeam = match.getHomeTeam().getId() == currentTeamId;
            int homeScore = match.getScore().getHome();
            int awayScore = match.getScore().getAway();

            int resultLozengeDrawable;
            String resultText;

            if (homeScore == awayScore) {
                resultLozengeDrawable = R.drawable.background_result_draw;
                resultText = "H"; // Hòa
            } else if ((isHomeTeamTheCurrentTeam && homeScore > awayScore) || (!isHomeTeamTheCurrentTeam && awayScore > homeScore)) {
                resultLozengeDrawable = R.drawable.background_result_win;
                resultText = "T"; // Thắng
            } else {
                resultLozengeDrawable = R.drawable.background_result_loss;
                resultText = "B"; // Bại
            }

            textResultLozenge.setBackground(ContextCompat.getDrawable(itemView.getContext(), resultLozengeDrawable));
            textResultLozenge.setText(resultText);

            // In đậm tên của đội đang xem chi tiết
            if (isHomeTeamTheCurrentTeam) {
                textHomeName.setTypeface(null, Typeface.BOLD);
                textHomeScore.setTypeface(null, Typeface.BOLD);
            } else {
                textAwayName.setTypeface(null, Typeface.BOLD);
                textAwayScore.setTypeface(null, Typeface.BOLD);
            }
        }
    }
}