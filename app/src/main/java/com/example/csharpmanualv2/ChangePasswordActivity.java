package com.example.csharpmanualv2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.csharpmanualv2.network.ApiService;
import com.example.csharpmanualv2.network.RetrofitClient;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextInputEditText etOldPassword, etNewPassword, etConfirmPassword;
    private Button btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        toolbar = findViewById(R.id.toolbar);
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        toolbar.setNavigationOnClickListener(v -> finish());

        btnChangePassword.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String oldPassword = etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(this, "Новый пароль должен быть минимум 6 символов", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            return;
        }

        btnChangePassword.setEnabled(false);

        ApiService.ChangePasswordRequest request = new ApiService.ChangePasswordRequest(oldPassword, newPassword);

        RetrofitClient.getApi().changePassword(request).enqueue(new Callback<ApiService.SimpleMessage>() {
            @Override
            public void onResponse(Call<ApiService.SimpleMessage> call, Response<ApiService.SimpleMessage> response) {
                btnChangePassword.setEnabled(true);

                if (response.isSuccessful()) {
                    Toast.makeText(ChangePasswordActivity.this, "Пароль изменён", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Ошибка: проверьте текущий пароль", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.SimpleMessage> call, Throwable t) {
                btnChangePassword.setEnabled(true);
                Toast.makeText(ChangePasswordActivity.this, "Сеть: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
