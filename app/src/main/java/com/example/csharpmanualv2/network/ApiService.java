package com.example.csharpmanualv2.network;

import com.example.csharpmanualv2.Task.Task;
import com.example.csharpmanualv2.Task.TaskResult;
import com.example.csharpmanualv2.Task.UserTaskResults;
import com.example.csharpmanualv2.UserProfile;
import com.example.csharpmanualv2.model.Chapter;
import com.example.csharpmanualv2.model.Subchapter;
import com.example.csharpmanualv2.model.UserStats;
import com.example.csharpmanualv2.response.MaterialsResponse;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // ========== КЛАССЫ ЗАПРОСОВ (REQUESTS) ==========

    class RegisterRequest {
        public String name;
        public String email;
        public String password;
        public RegisterRequest(String name, String email, String password) {
            this.name = name;
            this.email = email;
            this.password = password;
        }
    }

    class LoginRequest {
        public String email;
        public String password;
        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    class VerifyEmailRequest {
        public String email;
        public String code;
        public VerifyEmailRequest(String email, String code) {
            this.email = email;
            this.code = code;
        }
    }

    class ResendCodeRequest {
        public String email;
        public ResendCodeRequest(String email) {
            this.email = email;
        }
    }

    class CompleteRequest {
        public int subchapter_id;
        public int score;
        public CompleteRequest(int subchapter_id, int score) {
            this.subchapter_id = subchapter_id;
            this.score = score;
        }
    }

    class CheckAnswerRequest {
        public int task_id;
        public int selected_option_id;
        public CheckAnswerRequest(int task_id, int selected_option_id) {
            this.task_id = task_id;
            this.selected_option_id = selected_option_id;
        }
    }

    class UpdateProfileRequest {
        public String name;
        public UpdateProfileRequest(String name) {
            this.name = name;
        }
    }

    class ChangePasswordRequest {
        public String old_password;
        public String new_password;
        public ChangePasswordRequest(String old_password, String new_password) {
            this.old_password = old_password;
            this.new_password = new_password;
        }
    }

    // ========== КЛАССЫ ОТВЕТОВ (RESPONSES) ==========

    class User {
        public int id;
        public String name;
        public String email;
    }

    class LoginResponse {
        public String message;
        public String token;
        public User user;
    }

    class SimpleMessage {
        public String message;
        public int userId;
        public String email;
    }

    class ChapterProgressResponse {
        public int chapter_id;
        public int total;
        public int completed;
        public int progress_percent;
    }



    class AvatarResponse {
        public String message;
        public String avatar_url;
    }

    // ========== API МЕТОДЫ ==========
// ========== АУТЕНТИФИКАЦИЯ (Адреса Supabase Auth) ==========
    // Для регистрации используем стандартный путь Supabase
    @POST("auth/v1/signup")
    Call<SimpleMessage> register(@Body RegisterRequest body);

    @POST("auth/v1/token?grant_type=password")
    Call<LoginResponse> login(@Body LoginRequest body);

    @POST("auth/v1/verify")
    Call<LoginResponse> verifyEmail(@Body VerifyEmailRequest body);

    @POST("auth/v1/resend")
    Call<SimpleMessage> resendCode(@Body ResendCodeRequest body);

    // ========== КУРС (Таблицы в БД) ==========

    // Получаем все главы
    @GET("rest/v1/chapters?select=*")
    Call<List<Chapter>> getChapters();

    // Получаем подглавы конкретной главы (используем Query вместо Body)
    @GET("rest/v1/subchapters?select=*")
    Call<List<Subchapter>> getSubchapters(@Query("chapter_id") int chapterId);

    // Получаем материалы (теорию и практику)
    // Здесь используем фильтрацию по subchapter_id через URL
    @GET("rest/v1/materials?select=*")
    Call<MaterialsResponse> getMaterials(@Query("subchapter_id") int subchapterId);

    // ========== ПРОГРЕСС И ЗАДАЧИ ==========

    @GET("rest/v1/user_stats?select=*&limit=1")
    Call<UserStats> getUserStats();

    @POST("rest/v1/user_progress")
    Call<SimpleMessage> markComplete(@Body CompleteRequest body);

    @GET("rest/v1/tasks?select=*,task_options(*)")
    Call<List<Task>> getTasks(@Query("subchapter_id") int subchapterId);

    // ========== ПРОФИЛЬ ==========

    @GET("rest/v1/profiles?select=*&limit=1")
    Call<UserProfile> getUserProfile();

    @PATCH("rest/v1/profiles")
    Call<SimpleMessage> updateProfile(@Body UpdateProfileRequest body);

    @Multipart
    @POST("storage/v1/object/avatars/avatar_{userId}.jpg")
    Call<AvatarResponse> uploadAvatar(@Path("userId") String userId, @Part MultipartBody.Part avatar);

}