package com.example.csharpmanualv2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.csharpmanualv2.SharedPreferences.UserPrefs;
import com.example.csharpmanualv2.network.ApiService;
import com.example.csharpmanualv2.network.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyEmailActivity extends AppCompatActivity {

    private TextView tvEmailInfo, tvResendCode;
    private TextInputEditText etCode;
    private Button btnVerify;
    private String email;
    private UserPrefs userPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        tvEmailInfo = findViewById(R.id.tvEmailInfo);
        tvResendCode = findViewById(R.id.tvResendCode);
        etCode = findViewById(R.id.etCode);
        btnVerify = findViewById(R.id.btnVerify);

        userPrefs = new UserPrefs(this);

        email = getIntent().getStringExtra("email");
        tvEmailInfo.setText("Код отправлен на " + email);

        btnVerify.setOnClickListener(v -> verifyCode());
        tvResendCode.setOnClickListener(v -> resendCode());
    }

    private void verifyCode() {
        String code = etCode.getText().toString().trim();

        if (code.isEmpty() || code.length() != 6) {
            Toast.makeText(this, "Введите 6-значный код", Toast.LENGTH_SHORT).show();
            return;
        }

        btnVerify.setEnabled(false);

        ApiService.VerifyEmailRequest request = new ApiService.VerifyEmailRequest(email, code);

        RetrofitClient.getApi().verifyEmail(request).enqueue(new Callback<ApiService.LoginResponse>() {
            @Override
            public void onResponse(Call<ApiService.LoginResponse> call, Response<ApiService.LoginResponse> response) {
                btnVerify.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    ApiService.LoginResponse data = response.body();

                    // Сохраняем токен
                    userPrefs.saveUser(
                            data.token,
                            data.user.id,
                            data.user.name,
                            data.user.email
                    );

                    Toast.makeText(VerifyEmailActivity.this, "Email подтверждён!", Toast.LENGTH_SHORT).show();

                    // Переходим в главное меню
                    Intent intent = new Intent(VerifyEmailActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(VerifyEmailActivity.this, "Неверный или истёкший код", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.LoginResponse> call, Throwable t) {
                btnVerify.setEnabled(true);
                Toast.makeText(VerifyEmailActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resendCode() {
        tvResendCode.setEnabled(false);

        ApiService.ResendCodeRequest request = new ApiService.ResendCodeRequest(email);

        RetrofitClient.getApi().resendCode(request).enqueue(new Callback<ApiService.SimpleMessage>() {
            @Override
            public void onResponse(Call<ApiService.SimpleMessage> call, Response<ApiService.SimpleMessage> response) {
                tvResendCode.setEnabled(true);

                if (response.isSuccessful()) {
                    Toast.makeText(VerifyEmailActivity.this, "Код отправлен повторно", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(VerifyEmailActivity.this, "Ошибка отправки кода", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.SimpleMessage> call, Throwable t) {
                tvResendCode.setEnabled(true);
                Toast.makeText(VerifyEmailActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}