package com.example.flashscoreapp.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.flashscoreapp.R;
import com.example.flashscoreapp.ui.favorites.FavoritesFragment;
import com.example.flashscoreapp.ui.home.HomeFragment;
import com.example.flashscoreapp.ui.leagues.LeaguesFragment;
import com.example.flashscoreapp.ui.live.LiveMatchesFragment;
import com.example.flashscoreapp.ui.settings.SettingsActivity;
import com.example.flashscoreapp.ui.settings.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private int currentNavItemId = R.id.navigation_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            currentNavItemId = item.getItemId(); // Cập nhật ID của tab được chọn

            if (currentNavItemId == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
                toolbar.setTitle("Today's Matches");
            } else if (currentNavItemId == R.id.navigation_leagues) {
                selectedFragment = new LeaguesFragment();
                toolbar.setTitle("Leagues");
            } else if (currentNavItemId == R.id.navigation_favorites) {
                selectedFragment = new FavoritesFragment();
                toolbar.setTitle("Favorites");
            } else if (currentNavItemId == R.id.navigation_live) {
                selectedFragment = new LiveMatchesFragment();
                toolbar.setTitle("Trực tiếp");
            }

            if (selectedFragment != null) {
                replaceFragment(selectedFragment);
            }

            // Yêu cầu hệ thống vẽ lại menu trên Toolbar
            invalidateOptionsMenu();

            return true;
        });

        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
            toolbar.setTitle("Today's Matches");
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Gắn tệp menu_toolbar.xml vào Toolbar
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isFavoritesTab = (currentNavItemId == R.id.navigation_favorites);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            searchItem.setVisible(!isFavoritesTab);
        }

        MenuItem settingsItem = menu.findItem(R.id.action_settings);
        if (settingsItem != null) {
            settingsItem.setVisible(!isFavoritesTab);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Xử lý sự kiện khi nhấn vào các nút trên Toolbar
        int itemId = item.getItemId();
        if (itemId == R.id.action_search) {
            Toast.makeText(this, "Nút Tìm kiếm được nhấn", Toast.LENGTH_SHORT).show();
            // TODO: Thêm logic tìm kiếm ở đây
            return true;
        } else if (itemId == R.id.action_settings) {

            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;

        }
        return super.onOptionsItemSelected(item);
    }
}