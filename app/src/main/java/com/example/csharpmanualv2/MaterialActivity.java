package com.example.csharpmanualv2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.csharpmanualv2.adapter.MaterialPagerAdapter;
import com.example.csharpmanualv2.model.Material;
import com.example.csharpmanualv2.network.RetrofitClient;
import com.example.csharpmanualv2.response.MaterialsResponse;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

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

                    // 1. СОЗДАЕМ СПИСОК (т.к. адаптер ждет List<Material>)
                    List<Material> materialsList = new ArrayList<>();

                    // 2. ДОБАВЛЯЕМ ОБЪЕКТЫ (проверяем на null, чтобы не упало)
                    if (data.theory != null) {
                        data.theory.type = "theory";
                        materialsList.add(data.theory);
                    }
                    if (data.practice != null) {
                        data.practice.type = "practice";
                        materialsList.add(data.practice);
                    }

                    // 3. ПЕРЕДАЕМ СПИСОК В АДАПТЕР (теперь аргументов 4, как и просит адаптер)
                    MaterialPagerAdapter adapter = new MaterialPagerAdapter(
                            MaterialActivity.this,
                            materialsList, // Передаем список, а не два объекта отдельно
                            subchapterId,
                            subchapterTitle);

                    viewPager.setAdapter(adapter);

                    // Настройка вкладок через Mediator
                    new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                        if (position == 0) {
                            tab.setText("Теория");
                        } else {
                            tab.setText("Практика");
                        }
                    }).attach();

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