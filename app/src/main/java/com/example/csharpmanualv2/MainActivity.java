package com.example.csharpmanualv2;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNav;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        bottomNav = findViewById(R.id.bottomNav);

        // По умолчанию показываем главы
        if (savedInstanceState == null) {
            loadFragment(new ChaptersFragment());
            toolbar.setTitle("Пособие по C#");
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            String title = "";

            if (item.getItemId() == R.id.nav_chapters) {
                fragment = new ChaptersFragment();
                title = "Пособие по C#";
            } else if (item.getItemId() == R.id.nav_profile) {
                fragment = new ProfileFragment();
                title = "Профиль";
            }

            if (fragment != null) {
                loadFragment(fragment);
                toolbar.setTitle(title);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        currentFragment = fragment;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Обновляем текущий фрагмент при возврате в активити
        refreshCurrentFragment();
    }

    private void refreshCurrentFragment() {
        if (currentFragment == null) {
            return;
        }

        if (currentFragment instanceof ChaptersFragment) {
            ((ChaptersFragment) currentFragment).refreshData();
        } else if (currentFragment instanceof ProfileFragment) {
            ((ProfileFragment) currentFragment).refreshData();
        }
    }
}
