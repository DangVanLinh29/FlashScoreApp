package com.example.flashscoreapp.data.model.local;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.TypeConverters;
import com.example.flashscoreapp.data.db.Converters;
import com.example.flashscoreapp.data.model.domain.Match;
import androidx.annotation.NonNull;

// Thêm primaryKeys để xác định một mục yêu thích là duy nhất cho mỗi user
@Entity(tableName = "favorite_matches", primaryKeys = {"matchId", "userEmail"})
public class FavoriteMatch {

    @NonNull // matchId không được null
    private int matchId;

    @NonNull // userEmail không được null
    private String userEmail;

    private long matchTime;

    @TypeConverters(Converters.class)
    @ColumnInfo(name = "match_object")
    private Match match;

    // Sửa lại constructor để nhận thêm userEmail
    public FavoriteMatch(int matchId, @NonNull String userEmail, long matchTime, Match match) {
        this.matchId = matchId;
        this.userEmail = userEmail;
        this.matchTime = matchTime;
        this.match = match;
    }

    // Thêm Getter và Setter cho userEmail
    @NonNull
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(@NonNull String userEmail) {
        this.userEmail = userEmail;
    }

    // Các getter/setter khác giữ nguyên
    public int getMatchId() { return matchId; }
    public void setMatchId(int matchId) { this.matchId = matchId; }
    public Match getMatch() { return match; }
    public void setMatch(Match match) { this.match = match; }
    public long getMatchTime() { return matchTime; }
    public void setMatchTime(long matchTime) { this.matchTime = matchTime; }
}