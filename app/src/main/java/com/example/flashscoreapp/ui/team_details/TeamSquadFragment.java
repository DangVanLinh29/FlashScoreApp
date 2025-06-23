package com.example.flashscoreapp.ui.team_details;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.flashscoreapp.R;
import com.example.flashscoreapp.data.model.remote.ApiPlayerResponse;
import com.example.flashscoreapp.data.model.remote.ApiPlayerStatistics;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TeamSquadFragment extends Fragment {
    // Thêm tag để lọc log cho dễ
    private static final String TAG = "SquadDebug";

    private TeamDetailsViewModel viewModel;
    private TeamSquadAdapter adapter;
    private TextView emptyText;
    private RecyclerView recyclerView;

    public static TeamSquadFragment newInstance() {
        return new TeamSquadFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(TeamDetailsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Sử dụng layout có sẵn text view cho thông báo
        return inflater.inflate(R.layout.fragment_recycler_view_only, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.main_recycler_view);
        emptyText = view.findViewById(R.id.text_empty_message);

        adapter = new TeamSquadAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        viewModel.getSquad().observe(getViewLifecycleOwner(), players -> {
            Log.d(TAG, "Fragment nhận được " + (players != null ? players.size() : "null") + " cầu thủ từ ViewModel.");

            if (players != null && !players.isEmpty()) {
                List<Object> displayList = groupPlayersByPosition(players);
                Log.d(TAG, "Sau khi nhóm, danh sách hiển thị có " + displayList.size() + " mục.");

                if(displayList.isEmpty()){
                    recyclerView.setVisibility(View.GONE);
                    emptyText.setVisibility(View.VISIBLE);
                    emptyText.setText("Không thể phân loại đội hình.");
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyText.setVisibility(View.GONE);
                    adapter.setItems(displayList);
                }

            } else {
                recyclerView.setVisibility(View.GONE);
                emptyText.setVisibility(View.VISIBLE);
                emptyText.setText("Không có dữ liệu đội hình.");
            }
        });
    }

    private List<Object> groupPlayersByPosition(List<ApiPlayerResponse> players) {
        Map<String, List<ApiPlayerResponse>> groupedMap = new LinkedHashMap<>();
        // Khởi tạo các nhóm theo đúng thứ tự mong muốn
        groupedMap.put("Goalkeeper", new ArrayList<>());
        groupedMap.put("Defender", new ArrayList<>());
        groupedMap.put("Midfielder", new ArrayList<>());
        groupedMap.put("Attacker", new ArrayList<>());
        // Thêm một nhóm dự phòng cho các cầu thủ không xác định được vị trí
        groupedMap.put("Unknown", new ArrayList<>());

        for (ApiPlayerResponse playerResponse : players) {
            String position = null;
            List<ApiPlayerStatistics> statsList = playerResponse.getStatistics();
            // Kiểm tra xem có dữ liệu thống kê và vị trí hay không
            if (statsList != null && !statsList.isEmpty() && statsList.get(0).getGames() != null) {
                position = statsList.get(0).getGames().getPosition();
            }

            // Nếu có vị trí và nằm trong các nhóm đã biết thì thêm vào
            if (position != null && groupedMap.containsKey(position)) {
                groupedMap.get(position).add(playerResponse);
            } else {
                // Nếu không có vị trí, thêm vào nhóm "Unknown"
                groupedMap.get("Unknown").add(playerResponse);
            }
        }

        List<Object> displayList = new ArrayList<>();
        // Dịch và thêm vào danh sách hiển thị
        for (Map.Entry<String, List<ApiPlayerResponse>> entry : groupedMap.entrySet()) {
            // Chỉ thêm tiêu đề nếu nhóm đó có cầu thủ
            if (!entry.getValue().isEmpty()) {
                String positionName = entry.getKey();
                switch (positionName) {
                    case "Goalkeeper":
                        displayList.add("Thủ môn");
                        break;
                    case "Defender":
                        displayList.add("Hậu vệ");
                        break;
                    case "Midfielder":
                        displayList.add("Tiền vệ");
                        break;
                    case "Attacker":
                        displayList.add("Tiền đạo");
                        break;
                    case "Unknown":
                        // Nếu nhóm không xác định có cầu thủ, cũng hiển thị họ
                        displayList.add("Chưa rõ vị trí");
                        break;
                }
                displayList.addAll(entry.getValue());
            }
        }
        return displayList;
    }
}