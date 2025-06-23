package com.example.flashscoreapp.ui.favorites;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.flashscoreapp.data.model.domain.Match;
import com.example.flashscoreapp.data.model.local.FavoriteTeam;
import com.example.flashscoreapp.data.repository.MatchRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoritesViewModel extends AndroidViewModel {

    private static final String TAG = "FavoritesViewModel";
    private final MatchRepository repository;
    private final MediatorLiveData<List<Match>> favoriteTeamMatches = new MediatorLiveData<>();
    private final LiveData<List<Match>> favoriteMatches;
    private final LiveData<List<FavoriteTeam>> favoriteTeams;

    // Danh sách để theo dõi các nguồn LiveData đã thêm, giúp chúng ta gỡ bỏ chúng khi cần
    private final List<LiveData<List<Match>>> activeSources = new ArrayList<>();

    public FavoritesViewModel(@NonNull Application application) {
        super(application);
        // Sử dụng Singleton (nếu bạn đã áp dụng)
        repository = MatchRepository.getInstance(application);

        // Lấy danh sách các trận đã bấm yêu thích (cho tab 1)
        favoriteMatches = repository.getAllFavoriteMatches();
        // Lấy danh sách các đội đã bấm yêu thích
        this.favoriteTeams = repository.getAllFavoriteTeams();

        // --- LOGIC LẤY TRẬN ĐẤU MỚI ---
        // Lắng nghe sự thay đổi của danh sách đội yêu thích
        favoriteTeamMatches.addSource(this.favoriteTeams, teams -> {
            // 1. Xóa tất cả các nguồn lắng nghe cũ để tránh rò rỉ hoặc dữ liệu thừa
            for (LiveData<List<Match>> source : activeSources) {
                favoriteTeamMatches.removeSource(source);
            }
            activeSources.clear();

            // Nếu không có đội yêu thích, trả về danh sách rỗng
            if (teams == null || teams.isEmpty()) {
                Log.d(TAG, "Không có đội yêu thích nào, trả về danh sách rỗng.");
                favoriteTeamMatches.setValue(new ArrayList<>());
                return;
            }

            Log.d(TAG, "Tìm thấy " + teams.size() + " đội yêu thích. Bắt đầu lấy lịch thi đấu cho từng đội.");
            // 2. Với mỗi đội yêu thích, gọi API để lấy lịch thi đấu của họ
            for (FavoriteTeam team : teams) {
                // Lấy 10 trận đấu sắp tới của mỗi đội
                LiveData<List<Match>> teamFixturesSource = repository.getNextFixturesForTeam(team.teamId, 10);
                activeSources.add(teamFixturesSource);

                favoriteTeamMatches.addSource(teamFixturesSource, matchesFromTeam -> {
                    // 3. Mỗi khi có kết quả trả về từ bất kỳ đội nào, gộp tất cả các kết quả lại
                    recombineAllMatches();
                });
            }
        });
    }

    // Hàm này sẽ gộp kết quả từ tất cả các nguồn và cập nhật LiveData cuối cùng
    private void recombineAllMatches() {
        List<Match> combinedList = new ArrayList<>();
        Set<Integer> matchIds = new HashSet<>(); // Dùng Set để tránh trùng lặp trận đấu

        for (LiveData<List<Match>> source : activeSources) {
            if (source.getValue() != null) {
                for (Match match : source.getValue()) {
                    // Nếu thêm thành công (trận đấu chưa có trong Set) thì mới thêm vào list
                    if (matchIds.add(match.getMatchId())) {
                        combinedList.add(match);
                    }
                }
            }
        }

        // Sắp xếp lại danh sách cuối cùng theo thời gian
        combinedList.sort(Comparator.comparingLong(Match::getMatchTime));

        Log.d(TAG, "Gộp xong. Tổng cộng có " + combinedList.size() + " trận đấu.");
        favoriteTeamMatches.setValue(combinedList);
    }

    // Các getters và setters khác giữ nguyên
    public LiveData<List<Match>> getFavoriteMatches() { return favoriteMatches; }
    public LiveData<List<Match>> getFavoriteTeamMatches() { return favoriteTeamMatches; }
    public LiveData<List<FavoriteTeam>> getFavoriteTeams() { return favoriteTeams; }
    public void addFavorite(Match match) { repository.addFavorite(match); }
    public void removeFavorite(Match match) { repository.removeFavorite(match); }
}