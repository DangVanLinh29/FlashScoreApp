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
import com.example.flashscoreapp.data.model.remote.ApiDetailedPlayerInfo;
import com.example.flashscoreapp.data.model.remote.ApiPlayerResponse;
import com.example.flashscoreapp.data.model.remote.ApiPlayerStatistics;
import com.example.flashscoreapp.util.CountryCodeMapper;

import java.util.ArrayList;
import java.util.List;

public class TeamSquadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_PLAYER = 1;

    private List<Object> items = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<Object> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof String) {
            return TYPE_HEADER;
        }
        return TYPE_PLAYER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_group_header, parent, false);
            return new PositionHeaderViewHolder(view);
        } else { // TYPE_PLAYER
            View view = inflater.inflate(R.layout.item_squad_player, parent, false);
            return new PlayerViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_HEADER) {
            ((PositionHeaderViewHolder) holder).bind((String) items.get(position));
        } else {
            ((PlayerViewHolder) holder).bind((ApiPlayerResponse) items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ViewHolder cho tiêu đề vị trí (Thủ môn, Hậu vệ...)
    static class PositionHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView textHeader;
        PositionHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            textHeader = itemView.findViewById(R.id.text_group_header);
        }
        void bind(String title) {
            textHeader.setText(title);
        }
    }

    // ViewHolder cho thông tin một cầu thủ
    static class PlayerViewHolder extends RecyclerView.ViewHolder {
        ImageView playerPhoto, playerFlag;
        TextView playerName, playerNumber;

        PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            playerPhoto = itemView.findViewById(R.id.image_player_photo);
            playerFlag = itemView.findViewById(R.id.image_player_flag);
            playerName = itemView.findViewById(R.id.text_player_name);
            playerNumber = itemView.findViewById(R.id.text_player_number);
        }

        void bind(ApiPlayerResponse playerResponse) {
            if (playerResponse == null) return;

            ApiDetailedPlayerInfo playerInfo = playerResponse.getPlayer();
            if (playerInfo != null) {
                playerName.setText(playerInfo.getName());
                Glide.with(itemView.getContext())
                        .load(playerInfo.getPhoto())
                        .placeholder(R.drawable.ic_person)
                        .circleCrop()
                        .into(playerPhoto);

                String countryCode = CountryCodeMapper.getCode(playerInfo.getNationality());
                if (countryCode != null) {
                    String flagUrl = "https://flagcdn.com/w40/" + countryCode.toLowerCase() + ".png";
                    Glide.with(itemView.getContext()).load(flagUrl).into(playerFlag);
                    playerFlag.setVisibility(View.VISIBLE);
                } else {
                    playerFlag.setVisibility(View.INVISIBLE);
                }
            }

            // Lấy số áo từ trong statistics
            List<ApiPlayerStatistics> stats = playerResponse.getStatistics();
            if (stats != null && !stats.isEmpty() && stats.get(0).getGames() != null) {
                Integer number = stats.get(0).getGames().getNumber(); // Giờ đây là Integer
                if (number != null) {
                    playerNumber.setText(String.valueOf(number));
                } else {
                    playerNumber.setText("-");
                }
            } else {
                playerNumber.setText("-");
            }
        }
    }
}