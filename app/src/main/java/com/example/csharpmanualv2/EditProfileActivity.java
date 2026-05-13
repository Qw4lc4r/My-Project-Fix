package com.example.csharpmanualv2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.csharpmanualv2.SharedPreferences.UserPrefs;
import com.example.csharpmanualv2.network.ApiService;
import com.example.csharpmanualv2.network.RetrofitClient;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextInputEditText etName;
    private TextView tvEmail;
    private Button btnSave;
    private UserPrefs userPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        toolbar = findViewById(R.id.toolbar);
        etName = findViewById(R.id.etName);
        tvEmail = findViewById(R.id.tvEmail);
        btnSave = findViewById(R.id.btnSave);

        userPrefs = new UserPrefs(this);

        toolbar.setNavigationOnClickListener(v -> finish());

        // Загружаем текущие данные
        etName.setText(userPrefs.getUserName());
        tvEmail.setText("Email: " + userPrefs.getUserEmail());

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String newName = etName.getText().toString().trim();

        if (newName.isEmpty()) {
            Toast.makeText(this, "Введите имя", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSave.setEnabled(false);

        ApiService.UpdateProfileRequest request = new ApiService.UpdateProfileRequest(newName);

        RetrofitClient.getApi().updateProfile(request).enqueue(new Callback<ApiService.SimpleMessage>() {
            @Override
            public void onResponse(Call<ApiService.SimpleMessage> call, Response<ApiService.SimpleMessage> response) {
                btnSave.setEnabled(true);

                if (response.isSuccessful()) {
                    // Обновляем имя в SharedPreferences
                    userPrefs.saveUser(
                            userPrefs.getToken(),
                            userPrefs.getUserId(),
                            newName,
                            userPrefs.getUserEmail()
                    );

                    Toast.makeText(EditProfileActivity.this, "Профиль обновлён", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Ошибка обновления", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.SimpleMessage> call, Throwable t) {
                btnSave.setEnabled(true);
                Toast.makeText(EditProfileActivity.this, "Сеть: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
