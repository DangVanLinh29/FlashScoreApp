package com.example.flashscoreapp.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.flashscoreapp.data.model.local.FavoriteMatch;
import com.example.flashscoreapp.data.model.domain.Match;

import java.util.List;

@Dao
public interface MatchDao {

    // Thêm một trận đấu vào danh sách yêu thích
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addFavorite(FavoriteMatch favoriteMatch);

    // Xóa một trận đấu khỏi danh sách yêu thích
    @Delete
    void removeFavorite(FavoriteMatch favoriteMatch);

    // Lấy tất cả các trận đấu yêu thích.
    // Trả về LiveData để UI có thể tự động cập nhật khi có thay đổi.
    @Query("SELECT match_object FROM favorite_matches WHERE userEmail = :userEmail ORDER BY matchTime DESC")
    LiveData<List<Match>> getAllFavoriteMatches(String userEmail);

    // Kiểm tra xem một trận đấu có trong danh sách yêu thích hay không
    @Query("SELECT * FROM favorite_matches WHERE matchId = :matchId AND userEmail = :userEmail")
    FavoriteMatch getFavoriteMatchById(int matchId, String userEmail);
}