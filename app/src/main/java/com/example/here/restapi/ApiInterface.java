package com.example.here.restapi;


import com.example.here.models.Race;
import com.example.here.models.Invitation;
import com.example.here.models.UserData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("/api/user/name")
    Call<Name> getName(@Header("Authorization") String authorization);

    @GET("/api/user/username")
    Call<Username> getUsername(@Header("Authorization") String authorization);

    @GET("/api/user/email")
    Call<UserEmail> getEmail(@Header("Authorization") String authorization);

    @GET("/api/ongoingActivity")
    Call<UserMass> getMass(@Header("Authorization") String authorization);

    @GET("/api/user/friends")
    Call<List<UserData>> getFriends(@Header("Authorization") String authorization);

    @GET("/api/race/get_races_id")
    Call<List<Integer>> getRacesId(@Header("Authorization") String authorization);

    @GET("/api/race/{race_id}/get_participants_limit")
    Call<Integer> getLimit(@Header("Authorization") String authorization, @Path("race_id") int id);

//    @POST("/api/race/{race_id}/add_participant")
//    Call<Integer> addParticipant(@Header("Authorization") String authorization, @Path("race_id") int id);

    @POST("/api/race/{race_id}/join_race")
    Call<Integer> joinRace(@Header("Authorization") String authorization, @Path("race_id") int id);
  
    @GET("/api/user/invitations")
    Call<List<Invitation>> getInvitations(@Header("Authorization") String authorization);

    @DELETE("/api/user/invitations/{id}/accept")
    Call<Void> acceptInvitation(@Header("Authorization") String authorization, @Path("id") int id);

    @DELETE("/api/user/invitations/{id}/reject")
    Call<Void> rejectInvitation(@Header("Authorization") String authorization, @Path("id") int id);

    @POST("/api/auth")
    Call<Token> getAuthToken(@Body Credentials credentials);

    @POST("/api/user/register")
    Call<Token> register(@Body RegisterCredentials credentials);

    @POST("/api/user/register")
    Call<Void> add_training_stats(@Body StatsCredentials credentials);

    @POST("/api/user/add_data")
    Call<Void> addData(@Header("Authorization") String authorization, @Body UserData userData);

    @GET("/api/user/get_data")
    Call<UserData> getMyData(@Header("Authorization") String authorization);

    @GET("api/user/{id}")
    Call<UserData> getUserData(@Path("id") int id);

    @POST("/api/user/edit_data")
    Call<Void> editData(@Header("Authorization") String authorization, @Body UserData userData);

    @POST("/api/user/edit_user")
    Call<Void> editName(@Header("Authorization") String authorization, @Body Name name);

    @GET("/api/training/get_last_route")
    Call<List<Coordinates>> getLastRoute(@Header("Authorization") String authorization);

    @GET("/api/training/get_statistics/{number}")
    Call<TrainingStats> getTrainingStatistics(@Header("Authorization") String authorization, @Path("number") int number);

    @GET("/api/users/")
    Call<List<UserData>> findUsersByNickname(@Header("Authorization") String authorization, @Query("nickname") String nickname);

    @GET("/api/user/{id}")
    Call<UserData> getUserDataById(@Path("id") int id);

    @GET("/api/user/{id}/email")
    Call<UserEmail> getUserEmailById(@Path("id") int id);

    @POST("/api/user/invite/{id}")
    Call<Void> invite(@Header("Authorization") String authorization, @Path("id") int id);
}
