package com.example.flashscoreapp.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.flashscoreapp.data.model.local.FavoriteTeam;
import java.util.List;

@Dao
public interface TeamDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addFavorite(FavoriteTeam team);

    @Delete
    void removeFavorite(FavoriteTeam team);

    @Query("SELECT * FROM favorite_teams WHERE userEmail = :userEmail")
    LiveData<List<FavoriteTeam>> getAllFavoriteTeams(String userEmail);
}