package com.example.flashscoreapp.ui.match_details.lineups;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.flashscoreapp.R;
import com.example.flashscoreapp.data.model.domain.PlayerDisplay;
import java.util.ArrayList;
import java.util.List;

public class LineupPlayerAdapter extends RecyclerView.Adapter<LineupPlayerAdapter.PlayerViewHolder> {

    private List<PlayerDisplay> players = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    public void setPlayers(List<PlayerDisplay> players) {
        this.players = (players != null) ? players : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lineup_player, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        holder.bind(players.get(position));
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView playerNumber, playerName;

        PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            playerNumber = itemView.findViewById(R.id.text_player_number);
            playerName = itemView.findViewById(R.id.text_player_name);
            // Ẩn cờ đi vì nó không cần thiết nữa
            itemView.findViewById(R.id.image_player_flag).setVisibility(View.GONE);
        }

        void bind(PlayerDisplay player) {
            if (player != null) {
                playerNumber.setText(String.valueOf(player.getNumber()));
                String position = player.getPosition() != null ? " (" + player.getPosition() + ")" : "";
                String displayName = player.getName() + position;
                playerName.setText(displayName);
            }
        }
    }
}