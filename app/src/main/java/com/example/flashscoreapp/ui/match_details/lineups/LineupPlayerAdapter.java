package com.example.flashscoreapp.ui.match_details.lineups;

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
import com.example.flashscoreapp.data.model.remote.ApiPlayerDetail;
import com.example.flashscoreapp.data.model.remote.ApiLineupPlayer;
import com.example.flashscoreapp.util.CountryCodeMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LineupPlayerAdapter extends RecyclerView.Adapter<LineupPlayerAdapter.PlayerViewHolder> {
    private List<ApiLineupPlayer> players = new ArrayList<>();
    // Map để lưu trữ quốc tịch: Key là Player ID, Value là tên quốc gia
    private Map<Integer, String> nationalityMap = new HashMap<>();

    @SuppressLint("NotifyDataSetChanged")
    public void setPlayers(List<ApiLineupPlayer> players) {
        this.players = (players != null) ? players : new ArrayList<>();
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setNationalityMap(Map<Integer, String> nationalityMap) {
        this.nationalityMap = (nationalityMap != null) ? nationalityMap : new HashMap<>();
        // Không cần notify ở đây vì sẽ được gọi cùng setPlayers
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lineup_player, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        ApiLineupPlayer lineupPlayer = players.get(position);
        // Lấy quốc tịch từ map
        String nationality = nationalityMap.get(lineupPlayer.getPlayer().getId());
        holder.bind(lineupPlayer, nationality);
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView playerNumber, playerName;
        ImageView playerFlag;

        PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            playerNumber = itemView.findViewById(R.id.text_player_number);
            playerName = itemView.findViewById(R.id.text_player_name);
            playerFlag = itemView.findViewById(R.id.image_player_flag);
        }

        void bind(ApiLineupPlayer lineupPlayer, String nationality) {
            ApiPlayerDetail player = lineupPlayer.getPlayer();
            if (player != null) {
                playerNumber.setText(String.valueOf(player.getNumber()));
                String position = player.getPos() != null ? " (" + player.getPos() + ")" : "";
                String displayName = player.getName() + position;
                playerName.setText(displayName);

                // Lấy mã quốc gia và hiển thị cờ
                String countryCode = CountryCodeMapper.getCode(nationality);
                if (countryCode != null) {
                    // Dùng dịch vụ miễn phí flagcdn.com để lấy ảnh cờ
                    String flagUrl = "https://flagcdn.com/w40/" + countryCode.toLowerCase() + ".png";
                    Glide.with(itemView.getContext())
                            .load(flagUrl)
                            .into(playerFlag);
                    playerFlag.setVisibility(View.VISIBLE);
                } else {
                    playerFlag.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}