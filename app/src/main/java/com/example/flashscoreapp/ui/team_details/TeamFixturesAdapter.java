package com.example.flashscoreapp.ui.team_details;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TeamFixturesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_LEAGUE = 0;
    private static final int TYPE_MATCH = 1;

    private List<Object> items = new ArrayList<>();

    // Interface để xử lý sự kiện click (nếu bạn muốn thêm sau này)
    public interface OnFixtureClickListener {
        void onFixtureClicked(Match match);
    }
    private OnFixtureClickListener clickListener;
    public void setOnFixtureClickListener(OnFixtureClickListener listener) {
        this.clickListener = listener;
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
        } else { // TYPE_MATCH
            View view = inflater.inflate(R.layout.item_team_fixture, parent, false);
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
            holder.itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onFixtureClicked(match);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ViewHolder cho Tiêu đề Giải đấu
    static class LeagueHeaderViewHolder extends RecyclerView.ViewHolder {
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

    // ViewHolder cho một trận đấu sắp tới
    static class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView textDate, textTime, textHomeName, textAwayName;
        ImageView imageHomeLogo, imageAwayLogo;

        MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.text_fixture_date);
            textTime = itemView.findViewById(R.id.text_fixture_time);
            textHomeName = itemView.findViewById(R.id.text_home_name);
            textAwayName = itemView.findViewById(R.id.text_away_name);
            imageHomeLogo = itemView.findViewById(R.id.image_home_logo);
            imageAwayLogo = itemView.findViewById(R.id.image_away_logo);
        }

        void bind(Match match) {
            textHomeName.setText(match.getHomeTeam().getName());
            textAwayName.setText(match.getAwayTeam().getName());
            Glide.with(itemView.getContext()).load(match.getHomeTeam().getLogoUrl()).into(imageHomeLogo);
            Glide.with(itemView.getContext()).load(match.getAwayTeam().getLogoUrl()).into(imageAwayLogo);

            Date matchDate = new Date(match.getMatchTime());
            textDate.setText(new SimpleDateFormat("dd.MM.", Locale.getDefault()).format(matchDate));
            textTime.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(matchDate));
        }
    }
}