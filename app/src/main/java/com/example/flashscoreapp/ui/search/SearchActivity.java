package com.example.flashscoreapp.ui.search;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.flashscoreapp.R;
import com.example.flashscoreapp.data.model.domain.Team;
import com.example.flashscoreapp.data.model.remote.ApiLeagueData;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private SearchViewModel viewModel;
    private SearchAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView placeholderText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        setupUI();
        observeViewModel();
    }

    private void setupUI() {
        recyclerView = findViewById(R.id.recycler_view_search);
        progressBar = findViewById(R.id.progress_bar_search);
        placeholderText = findViewById(R.id.text_search_placeholder);
        SearchView searchView = findViewById(R.id.search_view);

        adapter = new SearchAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onLeagueClick(ApiLeagueData leagueData) {
                // Tạm thời hiển thị Toast.
                // Việc điều hướng về Fragment từ Activity này cần cấu trúc lại navigation.
                Toast.makeText(SearchActivity.this, "Đã nhấn vào giải: " + leagueData.getLeague().getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTeamClick(Team team) {
                // Tạm thời hiển thị Toast.
                Toast.makeText(SearchActivity.this, "Đã nhấn vào đội: " + team.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().length() > 2) {
                    progressBar.setVisibility(View.VISIBLE);
                    placeholderText.setVisibility(View.GONE);
                    viewModel.setSearchQuery(newText);
                } else {
                    adapter.setItems(new ArrayList<>());
                }
                return true;
            }
        });
    }

    private void observeViewModel() {
        viewModel.getSearchResults().observe(this, results -> {
            progressBar.setVisibility(View.GONE);
            if (results != null && !results.isEmpty()) {
                recyclerView.setVisibility(View.VISIBLE);
                placeholderText.setVisibility(View.GONE);
                adapter.setItems(results);
            } else {
                recyclerView.setVisibility(View.GONE);
                placeholderText.setVisibility(View.VISIBLE);
                placeholderText.setText("Không tìm thấy kết quả.");
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}