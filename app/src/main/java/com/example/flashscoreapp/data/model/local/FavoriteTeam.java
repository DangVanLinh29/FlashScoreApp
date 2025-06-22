package com.example.flashscoreapp.data.model.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;

// Thêm primaryKeys
@Entity(tableName = "favorite_teams", primaryKeys = {"teamId", "userEmail"})
public class FavoriteTeam {
    @NonNull
    public int teamId;

    @NonNull
    public String userEmail;

    // Sửa lại constructor
    public FavoriteTeam(int teamId, @NonNull String userEmail) {
        this.teamId = teamId;
        this.userEmail = userEmail;
    }
}