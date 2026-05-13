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
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    // ========== КЛАССЫ ЗАПРОСОВ ==========

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

    // ========== КЛАССЫ ОТВЕТОВ ==========

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

    // Аутентификация
    @POST("auth/register")
    Call<SimpleMessage> register(@Body RegisterRequest body);

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest body);

    @POST("auth/verify-email")
    Call<LoginResponse> verifyEmail(@Body VerifyEmailRequest body);

    @POST("auth/resend-code")
    Call<SimpleMessage> resendCode(@Body ResendCodeRequest body);

    // Курс
    @GET("course/chapters")
    Call<List<Chapter>> getChapters();

    @GET("course/chapters/{chapterId}/subchapters")
    Call<List<Subchapter>> getSubchapters(@Path("chapterId") int chapterId);

    @GET("course/subchapters/{subchapterId}/materials")
    Call<MaterialsResponse> getMaterials(@Path("subchapterId") int subchapterId);

    // Прогресс
    @GET("progress/stats")
    Call<UserStats> getUserStats();

    @POST("progress/complete")
    Call<SimpleMessage> markComplete(@Body CompleteRequest body);

    @GET("progress/chapter/{chapterId}")
    Call<ChapterProgressResponse> getChapterProgress(@Path("chapterId") int chapterId);

    // Задачи
    @GET("tasks/subchapter/{subchapterId}")
    Call<List<Task>> getTasks(@Path("subchapterId") int subchapterId);

    @POST("tasks/check")
    Call<TaskResult> checkAnswer(@Body CheckAnswerRequest body);

    @GET("tasks/results/{subchapterId}")
    Call<UserTaskResults> getTaskResults(@Path("subchapterId") int subchapterId);

    // Профиль
    @GET("user/profile")
    Call<UserProfile> getUserProfile();

    @PUT("user/profile")
    Call<SimpleMessage> updateProfile(@Body UpdateProfileRequest body);

    @POST("user/change-password")
    Call<SimpleMessage> changePassword(@Body ChangePasswordRequest body);

    @Multipart
    @POST("user/upload-avatar")
    Call<AvatarResponse> uploadAvatar(@Part MultipartBody.Part avatar);
}
