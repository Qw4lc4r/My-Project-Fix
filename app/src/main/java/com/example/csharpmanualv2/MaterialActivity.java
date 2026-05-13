package com.example.csharpmanualv2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.csharpmanualv2.adapter.MaterialPagerAdapter;
import com.example.csharpmanualv2.network.RetrofitClient;
import com.example.csharpmanualv2.response.MaterialsResponse;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MaterialActivity extends AppCompatActivity {

        private MaterialToolbar toolbar;
        private TabLayout tabLayout;
        private ViewPager2 viewPager;

        private int subchapterId;
        private String subchapterTitle;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_material);

            toolbar = findViewById(R.id.toolbar);
            tabLayout = findViewById(R.id.tabLayout);
            viewPager = findViewById(R.id.viewPager);

            subchapterId = getIntent().getIntExtra("subchapterId", 0);
            subchapterTitle = getIntent().getStringExtra("subchapterTitle");

            toolbar.setTitle(subchapterTitle);
            toolbar.setNavigationOnClickListener(v -> finish());

            Log.d("MATERIAL", "subchapterId = " + subchapterId);
            Log.d("MATERIAL", "subchapterTitle = " + subchapterTitle);

            loadMaterials();
        }

        private void loadMaterials() {
            RetrofitClient.getApi().getMaterials(subchapterId).enqueue(new Callback<MaterialsResponse>() {
                @Override
                public void onResponse(Call<MaterialsResponse> call, Response<MaterialsResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        MaterialsResponse data = response.body();

                        MaterialPagerAdapter adapter = new MaterialPagerAdapter(
                                MaterialActivity.this,
                                data.theory,
                                data.practice,
                                subchapterId,
                                subchapterTitle);

                        viewPager.setAdapter(adapter);

                        viewPager.post(new Runnable() {
                            @Override
                            public void run() {
                                new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                                    if (position == 0) {
                                        tab.setText("Теория");
                                    } else {
                                        tab.setText("Практика");
                                    }
                                }).attach();
                            }
                        });

                    } else {
                        Toast.makeText(MaterialActivity.this, "Ошибка загрузки материалов", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<MaterialsResponse> call, Throwable t) {
                    Toast.makeText(MaterialActivity.this, "Сеть: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }