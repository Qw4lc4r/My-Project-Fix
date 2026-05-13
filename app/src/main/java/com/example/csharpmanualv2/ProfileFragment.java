package com.example.csharpmanualv2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.csharpmanualv2.SharedPreferences.UserPrefs;
import com.example.csharpmanualv2.model.UserStats;
import com.example.csharpmanualv2.network.ApiService;
import com.example.csharpmanualv2.network.RetrofitClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private ImageView ivAvatar, ivEditAvatar;
    private TextView tvUserName, tvUserEmail;
    private TextView tvChaptersCount, tvExercisesCount, tvAvgScore;
    private LinearLayout btnEditProfile, btnChangePassword, btnAbout, btnLogout;
    private UserPrefs userPrefs;
    private static final String BASE_URL = "http://188.235.162.32:28015/";


    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        userPrefs = new UserPrefs(requireContext());

        ivAvatar = view.findViewById(R.id.ivAvatar);
        ivEditAvatar = view.findViewById(R.id.ivEditAvatar);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        tvChaptersCount = view.findViewById(R.id.tvChaptersCount);
        tvExercisesCount = view.findViewById(R.id.tvExercisesCount);
        tvAvgScore = view.findViewById(R.id.tvAvgScore);

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnAbout = view.findViewById(R.id.btnAbout);
        btnLogout = view.findViewById(R.id.btnLogout);

        tvUserName.setText(userPrefs.getUserName());
        tvUserEmail.setText(userPrefs.getUserEmail());

        loadProfile();
        loadStats();

        // Регистрируем launcher для выбора изображения
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        uploadAvatar(imageUri);
                    }
                });

        ivEditAvatar.setOnClickListener(v -> openImagePicker());

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        btnAbout.setOnClickListener(v -> showAboutDialog());

        btnLogout.setOnClickListener(v -> {
            userPrefs.logout();
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }

    private void loadProfile() {
        RetrofitClient.getApi().getUserProfile().enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfile profile = response.body();

                    // Загружаем аватар если есть
                    if (profile.avatar_url != null && !profile.avatar_url.isEmpty()) {
                        String avatarUrl = BASE_URL + profile.avatar_url;

                        Log.d("PROFILE", "Загружаю аватар: " + avatarUrl);

                        Glide.with(ProfileFragment.this)
                                .load(avatarUrl)
                                .placeholder(android.R.drawable.ic_menu_gallery)
                                .error(android.R.drawable.ic_menu_gallery)
                                .circleCrop() // Делаем круглым
                                .into(ivAvatar);
                    } else {
                        // Если аватара нет, показываем placeholder
                        Glide.with(ProfileFragment.this)
                                .load(android.R.drawable.ic_menu_gallery)
                                .circleCrop()
                                .into(ivAvatar);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Log.e("PROFILE", "Ошибка загрузки профиля: " + t.getMessage());
                // Используем данные из SharedPreferences
            }
        });
    }


    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void uploadAvatar(Uri imageUri) {
        try {
            // Загружаем изображение
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
            Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            if (originalBitmap == null) {
                Toast.makeText(requireContext(), "Ошибка чтения изображения", Toast.LENGTH_SHORT).show();
                return;
            }

            // Масштабируем изображение (максимум 800x800)
            int maxSize = 800;
            int width = originalBitmap.getWidth();
            int height = originalBitmap.getHeight();

            float scale = Math.min(((float) maxSize / width), ((float) maxSize / height));

            if (scale < 1.0f) {
                int newWidth = Math.round(width * scale);
                int newHeight = Math.round(height * scale);
                originalBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
            }

            // Сжимаем в JPEG с качеством 80%
            File tempFile = new File(requireContext().getCacheDir(), "temp_avatar.jpg");
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            outputStream.flush();
            outputStream.close();
            originalBitmap.recycle();

            Log.d("AVATAR", "Размер сжатого файла: " + (tempFile.length() / 1024) + " KB");

            // Создаём Multipart request
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), tempFile);
            MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", "avatar.jpg", requestFile);

            // Отправляем на сервер
            RetrofitClient.getApi().uploadAvatar(body).enqueue(new Callback<ApiService.AvatarResponse>() {
                @Override
                public void onResponse(Call<ApiService.AvatarResponse> call, Response<ApiService.AvatarResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(requireContext(), "Аватар загружен!", Toast.LENGTH_SHORT).show();

                        String avatarUrl = RetrofitClient.BASE_URL + response.body().avatar_url;

                        Log.d("AVATARlololo", "BASE_URL = " + RetrofitClient.BASE_URL);
                        Log.d("AVATARlololo", "avatar_url = " + response.body().avatar_url);
                        Log.d("AVATARlololo", "Полный URL = " + avatarUrl);

                        Glide.with(ProfileFragment.this)
                                .load(avatarUrl)
                                .placeholder(android.R.drawable.ic_menu_gallery)
                                .error(android.R.drawable.ic_menu_gallery)
                                .circleCrop()
                                .into(ivAvatar);

                        loadProfile();
                    } else {
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Неизвестная ошибка";
                            Toast.makeText(requireContext(), "Ошибка: " + errorBody, Toast.LENGTH_LONG).show();
                            Log.e("AVATAR", "Ошибка загрузки: " + errorBody);
                        } catch (Exception e) {
                            Toast.makeText(requireContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiService.AvatarResponse> call, Throwable t) {
                    Log.e("AVATAR", "Сеть: " + t.getMessage(), t);
                    Toast.makeText(requireContext(), "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("AVATAR", "Исключение: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void loadStats() {
        RetrofitClient.getApi().getUserStats().enqueue(new Callback<UserStats>() {
            @Override
            public void onResponse(Call<UserStats> call, Response<UserStats> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserStats stats = response.body();
                    tvChaptersCount.setText(String.valueOf(stats.chapters_completed));
                    tvExercisesCount.setText(String.valueOf(stats.exercises_completed));
                    tvAvgScore.setText(stats.avg_score + "%");
                }
            }

            @Override
            public void onFailure(Call<UserStats> call, Throwable t) {
                Toast.makeText(requireContext(), "Ошибка загрузки статистики", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAboutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("О приложении")
                .setMessage("Пособие по C#\n\nВерсия: 1.0\n\nПриложение для изучения основ языка программирования C#\n\n© 2025")
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        tvUserName.setText(userPrefs.getUserName());
        tvUserEmail.setText(userPrefs.getUserEmail());
        loadProfile();
        loadStats();
    }

    public void refreshData() {
        if (isAdded() && getView() != null) {
            loadProfile();
            loadStats();
        }
    }
}