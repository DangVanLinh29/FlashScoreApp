package com.example.flashscoreapp.ui.match_details;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import com.example.flashscoreapp.data.model.domain.LineupDisplayData;
import com.example.flashscoreapp.data.model.domain.Match;
import com.example.flashscoreapp.data.model.domain.MatchDetails;
import com.example.flashscoreapp.data.model.domain.PlayerDisplay;
import com.example.flashscoreapp.data.model.remote.ApiDetailedPlayerInfo;
import com.example.flashscoreapp.data.model.remote.ApiLineup;
import com.example.flashscoreapp.data.model.remote.ApiLineupPlayer;
import com.example.flashscoreapp.data.model.remote.ApiPlayerResponse;
import com.example.flashscoreapp.data.repository.MatchRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MatchDetailsViewModel extends AndroidViewModel {

    private final MatchRepository repository;
    private final LiveData<MatchDetails> matchDetails;
    private final MediatorLiveData<Pair<List<Match>, List<Match>>> recentMatchesPair = new MediatorLiveData<>();

    // LiveData chính, đã được "làm giàu", để cho Fragment sử dụng
    private final MediatorLiveData<Pair<LineupDisplayData, LineupDisplayData>> enrichedLineupData = new MediatorLiveData<>();

    // Constructor giờ nhận 5 tham số, bao gồm cả seasonYear
    public MatchDetailsViewModel(@NonNull Application application, int matchId, int homeTeamId, int awayTeamId, int seasonYear) {
        super(application);
        repository = new MatchRepository(application);
        matchDetails = repository.getMatchDetailsFromApi(matchId);

        // 1. Lấy tất cả các nguồn dữ liệu thô từ Repository
        LiveData<List<ApiLineup>> rawLineupsSource = repository.getLineups(matchId);
        // Gọi getPlayersForTeam với đúng seasonYear của trận đấu
        LiveData<List<ApiPlayerResponse>> homePlayersSource = repository.getPlayersForTeam(homeTeamId, seasonYear);
        LiveData<List<ApiPlayerResponse>> awayPlayersSource = repository.getPlayersForTeam(awayTeamId, seasonYear);

        // 2. Thêm các nguồn dữ liệu thô vào MediatorLiveData
        // Bất kỳ nguồn nào thay đổi, hàm combineData sẽ được gọi để thử kết hợp lại
        enrichedLineupData.addSource(rawLineupsSource, value -> combineData(rawLineupsSource.getValue(), homePlayersSource.getValue(), awayPlayersSource.getValue(), homeTeamId));
        enrichedLineupData.addSource(homePlayersSource, value -> combineData(rawLineupsSource.getValue(), homePlayersSource.getValue(), awayPlayersSource.getValue(), homeTeamId));
        enrichedLineupData.addSource(awayPlayersSource, value -> combineData(rawLineupsSource.getValue(), homePlayersSource.getValue(), awayPlayersSource.getValue(), homeTeamId));

        // Phần xử lý cho H2H không thay đổi
        LiveData<List<Match>> homeMatchesH2HSource = repository.getLastMatchesForTeam(homeTeamId, 5);
        LiveData<List<Match>> awayMatchesH2HSource = repository.getLastMatchesForTeam(awayTeamId, 5);
        recentMatchesPair.addSource(homeMatchesH2HSource, homeMatches -> {
            if (homeMatches != null && awayMatchesH2HSource.getValue() != null) {
                recentMatchesPair.setValue(new Pair<>(homeMatches, awayMatchesH2HSource.getValue()));
            }
        });
        recentMatchesPair.addSource(awayMatchesH2HSource, awayMatches -> {
            if (awayMatches != null && homeMatchesH2HSource.getValue() != null) {
                recentMatchesPair.setValue(new Pair<>(homeMatchesH2HSource.getValue(), awayMatches));
            }
        });
    }

    /**
     * Hàm này là trung tâm xử lý. Nó sẽ được gọi mỗi khi có một nguồn dữ liệu mới về.
     * Nó sẽ kiểm tra nếu đã đủ 3 nguồn dữ liệu, nó sẽ kết hợp chúng lại.
     */
    private void combineData(List<ApiLineup> lineups, List<ApiPlayerResponse> homePlayers, List<ApiPlayerResponse> awayPlayers, int homeTeamId) {
        // Chỉ thực hiện khi tất cả dữ liệu đã sẵn sàng
        if (lineups == null || homePlayers == null || awayPlayers == null || lineups.isEmpty()) {
            return;
        }

        // 3. Tạo map để tra cứu thông tin chi tiết (gồm cả ảnh) của cầu thủ bằng ID
        Map<Integer, ApiDetailedPlayerInfo> homePlayerInfoMap = homePlayers.stream()
                .map(ApiPlayerResponse::getPlayer)
                .collect(Collectors.toMap(ApiDetailedPlayerInfo::getId, player -> player, (a, b) -> a));

        Map<Integer, ApiDetailedPlayerInfo> awayPlayerInfoMap = awayPlayers.stream()
                .map(ApiPlayerResponse::getPlayer)
                .collect(Collectors.toMap(ApiDetailedPlayerInfo::getId, player -> player, (a, b) -> a));

        // 4. "Làm giàu" dữ liệu cho từng đội
        LineupDisplayData homeDisplayData = null;
        LineupDisplayData awayDisplayData = null;

        // API có thể trả về đội nhà/khách không theo thứ tự, cần kiểm tra bằng ID
        ApiLineup firstTeamLineup = lineups.get(0);
        if (firstTeamLineup.getTeam().getId() == homeTeamId) {
            homeDisplayData = createLineupDisplayData(firstTeamLineup, homePlayerInfoMap);
            if (lineups.size() > 1) {
                awayDisplayData = createLineupDisplayData(lineups.get(1), awayPlayerInfoMap);
            }
        } else {
            awayDisplayData = createLineupDisplayData(firstTeamLineup, awayPlayerInfoMap);
            if (lineups.size() > 1) {
                homeDisplayData = createLineupDisplayData(lineups.get(1), homePlayerInfoMap);
            }
        }

        // 5. Gửi dữ liệu đã hoàn chỉnh cho Fragment
        enrichedLineupData.setValue(new Pair<>(homeDisplayData, awayDisplayData));
    }

    /**
     * Hàm trợ giúp, chuyển đổi dữ liệu thô của một đội thành dữ liệu hiển thị.
     */
    private LineupDisplayData createLineupDisplayData(ApiLineup rawLineup, Map<Integer, ApiDetailedPlayerInfo> infoMap) {
        List<PlayerDisplay> enrichedStarters = enrichPlayerList(rawLineup.getStartXI(), infoMap);
        List<PlayerDisplay> enrichedSubs = enrichPlayerList(rawLineup.getSubstitutes(), infoMap);

        return new LineupDisplayData(
                rawLineup.getTeam().getName(),
                rawLineup.getFormation(),
                enrichedStarters,
                enrichedSubs
        );
    }

    /**
     * Hàm trợ giúp, chuyển đổi danh sách cầu thủ thô sang danh sách cầu thủ hiển thị (có ảnh).
     */
    private List<PlayerDisplay> enrichPlayerList(List<ApiLineupPlayer> rawPlayers, Map<Integer, ApiDetailedPlayerInfo> infoMap) {
        if (rawPlayers == null) return new ArrayList<>();

        return rawPlayers.stream()
                .map(rawPlayer -> {
                    ApiDetailedPlayerInfo info = infoMap.get(rawPlayer.getPlayer().getId());
                    String photoUrl = (info != null) ? info.getPhoto() : null; // Lấy URL ảnh từ map
                    return new PlayerDisplay(
                            rawPlayer.getPlayer().getId(),
                            rawPlayer.getPlayer().getName(),
                            rawPlayer.getPlayer().getNumber(),
                            rawPlayer.getPlayer().getPos(),
                            photoUrl
                    );
                })
                .collect(Collectors.toList());
    }

    // ---- Các hàm Getter cho Fragment ----
    public LiveData<Pair<LineupDisplayData, LineupDisplayData>> getEnrichedLineupData() {
        return enrichedLineupData;
    }

    public LiveData<MatchDetails> getMatchDetails() { return matchDetails; }
    public LiveData<Pair<List<Match>, List<Match>>> getRecentMatchesPair() { return recentMatchesPair; }
}